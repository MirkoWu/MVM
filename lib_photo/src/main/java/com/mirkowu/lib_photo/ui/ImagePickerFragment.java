package com.mirkowu.lib_photo.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.PickerConfig;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.adapter.FolderAdapter;
import com.mirkowu.lib_photo.adapter.ImageGridAdapter;
import com.mirkowu.lib_photo.bean.Folder;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.callback.ICollectionLoaderCallback;
import com.mirkowu.lib_photo.mediaLoader.MediaCollectionLoader;
import com.mirkowu.lib_photo.utils.PermissionsUtils;
import com.mirkowu.lib_photo.view.MediaGridDivider;
import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_util.utilcode.util.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Multi image selector Fragment
 */
public class ImagePickerFragment extends Fragment {

    public static final String TAG = "ImagePickerFragment";


    private static final String KEY_TEMP_FILE = "key_temp_file";



    // image result data set
    private ArrayList<String> mSelectedList = new ArrayList<>();
    // image result data set 数据过大会导致传送失败使用静态变量
    public static ArrayList<String> mLoadList = new ArrayList<>();
    // folder result data set
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    //private GridView mGridView;
    private RecyclerView mRvMedia;

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private ListPopupWindow mFolderPopupWindow;

    private TextView mCategoryText;
    private View mPopupAnchorView;


    private int mSpanCount = 3;
    private int mMaxSelectCount = 9;
    //   private File mTmpFile;

    private MediaCollectionLoader mLoaderCallback;
    private RecyclerView.OnScrollListener onScrollListener;

    public static ImagePickerFragment newInstance(Bundle args) {

        ImagePickerFragment fragment = new ImagePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ivp_fragment_multi_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int mode = selectMode();
        if (mode == PickerConfig.MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(PickerConfig.EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                mSelectedList = tmp;
            }
            mMaxSelectCount = selectImageCount();
        } else {
            mMaxSelectCount = 1;
        }
        //初始化
        initView(view, mode);

        mLoaderCallback = new MediaCollectionLoader(getContext(), new ICollectionLoaderCallback() {
            @Override
            public void onLoadFinish(List<Folder> folderList) {
                List<MediaBean> allList = folderList.get(0).mediaBeans;
                mImageAdapter.setData(allList);

                if (mSelectedList != null && mSelectedList.size() > 0) {
                    mImageAdapter.setDefaultSelected(mSelectedList);
                }
                mFolderAdapter.setData(folderList);
                updatePreviewOriginList(allList);//更新预览数据源
            }
        });

        checkPermission();
    }

    private void checkPermission() {
        PermissionsUtil.getInstance().requestPermissions(this, PermissionsUtil.PERMISSION_STORAGE,
                new PermissionsUtil.OnPermissionsListener() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        /*** 加载图片数据 */
                        startLoadImagesTask(false, null);
                    }

                    @Override
                    public void onPermissionShowRationale(int requestCode, String[] permissions) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.ivp_permission_dialog_title)
                                .setMessage(R.string.ivp_permission_rationale)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermission();//如果想继续同意权限 就重新调用改方法
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.ivp_error_no_permission)
                                .setMessage(R.string.ivp_permission_lack)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }
                });
    }

    private void initView(View view, final int mode) {
        mRvMedia = view.findViewById(R.id.mRvMedia);
        mPopupAnchorView = view.findViewById(R.id.footer);
        mCategoryText = view.findViewById(R.id.category_btn);

        //图片列表
        mImageAdapter = new ImageGridAdapter(getActivity(), showCamera(), mSpanCount);
        mImageAdapter.setMultiSelect(mode == PickerConfig.MODE_MULTI);
        mRvMedia.setAdapter(mImageAdapter);
        int spacing = getResources().getDimensionPixelSize(R.dimen.ivp_space_size);
        mRvMedia.addItemDecoration(new MediaGridDivider(mSpanCount, spacing, false));
        mRvMedia.setLayoutManager(new GridLayoutManager(getContext(), mSpanCount));
        mImageAdapter.setOnItemClickListener(new ImageGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImageGridAdapter adapter, View itemView, int position) {
                if (mImageAdapter.isShowCamera()) {//前往拍照
                    if (position == 0) {
                        showCameraAction();
                    } else {
                        ImagePreviewActivity.startFromPick(ImagePickerFragment.this,
                                mSelectedList,
                                position - 1, mMaxSelectCount);
                    }
                } else {//前往预览
                    ImagePreviewActivity.startFromPick(ImagePickerFragment.this,
                            mSelectedList,
                            position, mMaxSelectCount);
                }
            }

            @Override
            public void onItemViewClick(ImageGridAdapter adapter, View view, int position) {
                if (mImageAdapter.isShowCamera() && position == 0) {//前往拍照
                    showCameraAction();
                } else {
                    MediaBean mediaBean = adapter.getItem(position);
                    selectImageFromGrid(mediaBean, mode);
                }
            }
        });
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //滚动时，停止加载
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    ImagePicker.getInstance().getImageEngine().pause(getContext());
                } else {
                    ImagePicker.getInstance().getImageEngine().resume(getContext());
                }
            }
        };
        mRvMedia.addOnScrollListener(onScrollListener);

        //文件夹
        mFolderAdapter = new FolderAdapter(getActivity());
        mCategoryText.setText(R.string.ivp_folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

    }

    /**
     * Create popup ListView
     */
    private void createPopupFolderList() {
        int width = ScreenUtils.getScreenWidth();
        int height = (int) (ScreenUtils.getScreenHeight() * 0.6f);
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mFolderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();

                        //startLoadImagesTask(folder.path);//此处直接使用列表中的数据，不再查询
                        Folder folder = (Folder) v.getAdapter().getItem(index);
                        if (null != folder) {
                            mImageAdapter.setData(folder.mediaBeans);
                            mCategoryText.setText(folder.name);
                            if (mSelectedList != null && mSelectedList.size() > 0) {
                                mImageAdapter.setDefaultSelected(mSelectedList);
                            }

                            if (showCamera() && index == 0) {
                                mImageAdapter.setShowCamera(true);
                            } else {
                                mImageAdapter.setShowCamera(false);
                            }
                            updatePreviewOriginList(folder.mediaBeans);//更新预览数据源
                        }
                        mRvMedia.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    private void updatePreviewOriginList(List<MediaBean> mediaBeans) {
        mLoadList.clear();
        for (MediaBean bean : mediaBeans) {
            mLoadList.add(bean.path);
        }
    }

    /**
     * 开始查询图片任务
     *
     * @param path 指定路径
     */
    private void startLoadImagesTask(boolean isRestart, String path) {
        if (TextUtils.isEmpty(path)) {//所有图片
            if (isRestart) {
                LoaderManager.getInstance(this).restartLoader(MediaCollectionLoader.LOADER_ALL, null, mLoaderCallback);
            } else {
                LoaderManager.getInstance(this).initLoader(MediaCollectionLoader.LOADER_ALL, null, mLoaderCallback);
            }
        } else {//加载指定路径图片
            Bundle bundle = new Bundle();
            bundle.putString(MediaCollectionLoader.KEY_PATH, path);
            LoaderManager.getInstance(this).initLoader(MediaCollectionLoader.LOADER_CATEGORY, bundle, mLoaderCallback);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //  outState.putSerializable(KEY_TEMP_FILE, mTmpFile);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//        if (savedInstanceState != null) {
//            mTmpFile = (File) savedInstanceState.getSerializable(KEY_TEMP_FILE);
//        }
    }


    @Override
    public void onDestroyView() {
        if (mRvMedia != null) {
            mRvMedia.removeOnScrollListener(onScrollListener);
        }
        super.onDestroyView();
    }

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionsUtil.getInstance().onActivityResult(this, requestCode, resultCode, data);

        //更新已选中图片
        if (requestCode == ImagePreviewActivity.REQUEST_CODE_PREVIEW && resultCode == RESULT_OK) {
            ArrayList<String> list = data.getStringArrayListExtra(ImagePreviewActivity.KEY_SELECTED_DATA);
            boolean submit = data.getBooleanExtra(ImagePreviewActivity.KEY_SUBMIT, false);
            if (list != null && list.size() > 0) {
                mImageAdapter.setDefaultSelected(list);
                mSelectedList = list;
                updateActivityToolbar();
            }
            if (submit) {
                submitResult();//提交结果
            }
        }


            File imageFile = PermissionsUtils.onActivityResult(requestCode, resultCode, data);
            if (imageFile != null) {
                // notify system the image has change
                getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

                mSelectedList.add(imageFile.getAbsolutePath());

                submitResult();
            }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFolderPopupWindow != null) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Open camera
     */
    private void showCameraAction() {
        PermissionsUtils.showCameraAction(this);
    }

    /**
     * notify callback
     *
     * @param mediaBean mediaBean data
     */
    private void selectImageFromGrid(MediaBean mediaBean, int mode) {
        if (mediaBean != null) {
            if (mode == PickerConfig.MODE_MULTI) {
                if (mSelectedList.contains(mediaBean.path)) {
                    mSelectedList.remove(mediaBean.path);
//                    if (mCallback != null) {
//                        mCallback.onImageUnselected(mediaBean.path);
//                    }
                } else {
                    if (mMaxSelectCount == mSelectedList.size()) {
                        Toast.makeText(getActivity(), R.string.ivp_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSelectedList.add(mediaBean.path);
//                    if (mCallback != null) {
//                        mCallback.onImageSelected(mediaBean.path);
//                    }
                }
                mImageAdapter.select(mediaBean);
            } else if (mode == PickerConfig.MODE_SINGLE) {
                mSelectedList.clear();
                mSelectedList.add(mediaBean.path);
                mImageAdapter.select(mediaBean);
//                if (mCallback != null) {
//                    mCallback.onSingleImageSelected(mediaBean.path);
//                }
            }
            updateActivityToolbar();
        }
    }

    private Folder getFolderByPath(String path) {
        if (mResultFolder != null) {
            for (Folder folder : mResultFolder) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }

    private boolean showCamera() {
        return getArguments() == null || getArguments().getBoolean(PickerConfig.EXTRA_SHOW_CAMERA, true);
    }

    private int selectMode() {
        return getArguments() == null ? PickerConfig.MODE_MULTI : getArguments().getInt(PickerConfig.EXTRA_SELECT_MODE);
    }

    private int selectImageCount() {
        return getArguments() == null ? 9 : getArguments().getInt(PickerConfig.EXTRA_SELECT_COUNT);
    }

    public ArrayList<String> getSelectedList() {
        return mSelectedList;
    }

    private void updateActivityToolbar() {
        if (getActivity() != null && !getActivity().isFinishing() && (getActivity() instanceof ImagePickerActivity)) {
            ((ImagePickerActivity) getActivity()).updateDoneText(mSelectedList);
        }
    }

    public void submitResult() {
        if (mSelectedList != null && mSelectedList.size() > 0) {
            if (ImagePicker.getInstance().getOnPickResultListener() != null) {
                ImagePicker.getInstance().getOnPickResultListener().onPickResult(mSelectedList);
            }
        }
        getActivity().finish();
    }

}
