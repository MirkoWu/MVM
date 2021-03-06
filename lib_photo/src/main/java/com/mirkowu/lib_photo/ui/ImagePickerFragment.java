package com.mirkowu.lib_photo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import com.mirkowu.lib_photo.bean.FolderBean;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.callback.ICollectionLoaderCallback;
import com.mirkowu.lib_photo.mediaLoader.MediaCollectionLoader;
import com.mirkowu.lib_photo.mediaLoader.MediaModel;
import com.mirkowu.lib_photo.mediaLoader.ResultModel;
import com.mirkowu.lib_photo.utils.CameraUtil;
import com.mirkowu.lib_photo.view.MediaGridDivider;
import com.mirkowu.lib_util.LogUtil;
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

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private ListPopupWindow mFolderPopupWindow;
    private RecyclerView mRvMedia;
    private TextView mCategoryText;
    private View mPopupAnchorView;

    private int mSpanCount;
    private int mMaxPickCount;
    private boolean mIsSingleMode;
    private boolean mIsShowCamera;
    private boolean mIsShowVideo;

    private MediaCollectionLoader mLoaderCallback;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private PickerConfig mConfig = ImagePicker.getInstance().getPickerConfig();

    public static ImagePickerFragment newInstance() {
        ImagePickerFragment fragment = new ImagePickerFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ivp_fragment_image_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMaxPickCount = mConfig.getMaxPickCount();
        mSpanCount = mConfig.getSpanCount();
        mIsShowCamera = mConfig.isShowCamera();
        mIsShowVideo = mConfig.isShowVideo();
        mIsSingleMode = mMaxPickCount == 1;
        ResultModel.addList(mConfig.getOriginSelectList());

        //?????????
        initView(view);

        mLoaderCallback = new MediaCollectionLoader(getContext(), new ICollectionLoaderCallback() {
            @Override
            public void onLoadFinish(List<FolderBean> folderList) {
                MediaModel.init(folderList);
                mFolderAdapter.setData(folderList);

                ArrayList<MediaBean> allList = MediaModel.getCurChildList();
                mImageAdapter.setData(allList);
            }
        });

        checkPermission();
    }

    private void checkPermission() {
        PermissionsUtil.getInstance().requestPermissions(this, PermissionsUtil.GROUP_STORAGE,
                new PermissionsUtil.OnPermissionsListener() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        /*** ?????????????????? */
                        startLoadImagesTask(false, null);
                    }

                    @Override
                    public void onPermissionShowRationale(int requestCode, String[] permissions) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.ivp_permission_dialog_title)
                                .setMessage(R.string.ivp_permission_rationale_storage_for_photo)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermission(); //??????????????????????????? ????????????????????????
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.ivp_error_no_permission)
                                .setMessage(R.string.ivp_lack_storage_permission)
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

    private void initView(View view) {
        mRvMedia = view.findViewById(R.id.mRvMedia);
        mPopupAnchorView = view.findViewById(R.id.footer);
        mCategoryText = view.findViewById(R.id.category_btn);
        LinearLayout mLlOriginImage = view.findViewById(R.id.ll_origin_image);
        mLlOriginImage.setSelected(ResultModel.isOriginPhoto());
        mLlOriginImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultModel.setIsOriginPhoto(!ResultModel.isOriginPhoto());
                mLlOriginImage.setSelected(ResultModel.isOriginPhoto());
            }
        });
        //????????????
        mImageAdapter = new ImageGridAdapter(getContext());
        mImageAdapter.setOnItemClickListener(new ImageGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImageGridAdapter adapter, View itemView, int position) {
                if (mImageAdapter.isIsShowCamera()) { //????????????
                    if (position == 0) {
                        showCameraAction();
                    } else {
                        AlbumPreviewActivity.start(ImagePickerFragment.this, position - 1);
                    }
                } else { //????????????
                    AlbumPreviewActivity.start(ImagePickerFragment.this, position);
                }
            }

            @Override
            public void onItemViewClick(ImageGridAdapter adapter, View view, int position) {
                if (mImageAdapter.isIsShowCamera() && position == 0) { //????????????
                    showCameraAction();
                } else {
                    MediaBean mediaBean = adapter.getItem(position);
                    selectImageFromGrid(position, mediaBean);
                }
            }
        });
        mRvMedia.setAdapter(mImageAdapter);
        int spacing = getResources().getDimensionPixelSize(R.dimen.ivp_space_size);
        mRvMedia.addItemDecoration(new MediaGridDivider(mSpanCount, spacing, false));
        mRvMedia.setLayoutManager(new GridLayoutManager(getContext(), mSpanCount));
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //????????????????????????
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    ImagePicker.getInstance().getImageEngine().pause(getContext());
                } else {
                    ImagePicker.getInstance().getImageEngine().resume(getContext());
                }
            }
        };
        mRvMedia.addOnScrollListener(mOnScrollListener);

        //?????????
        mFolderAdapter = new FolderAdapter(getActivity());

        if (mConfig.isOnlyVideo()) {
            mCategoryText.setText(R.string.ivp_all_video);
        } else if (mIsShowVideo) {
            mCategoryText.setText(R.string.ivp_all_image_video);
        } else {
            mCategoryText.setText(R.string.ivp_all_image);
        }
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

        updateActivityToolbar();
    }

    /**
     * Create popup ListView
     */
    private void createPopupFolderList() {
        int width = ScreenUtils.getScreenWidth();
        int height = (int) (ScreenUtils.getScreenHeight() * 0.6f);
        mFolderPopupWindow = new ListPopupWindow(getContext());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MediaModel.selectFolder(position);
                mFolderAdapter.setSelectIndex(position);
                mFolderPopupWindow.dismiss();

                //startLoadImagesTask(folder.path);//???????????????????????????????????????????????????
                FolderBean folder = mFolderAdapter.getItem(position);
                if (null != folder) {
                    mImageAdapter.setData(folder.mediaList);
                    mCategoryText.setText(folder.name);
                    if (position == 0) {
                        mImageAdapter.setIsShowCamera(mIsShowCamera);
                    } else {
                        mImageAdapter.setIsShowCamera(false);
                    }
                }
                mRvMedia.smoothScrollToPosition(0);
            }
        });
    }

    /**
     * ????????????????????????
     *
     * @param path ????????????
     */
    private void startLoadImagesTask(boolean isRestart, String path) {
        if (TextUtils.isEmpty(path)) { //????????????
            if (isRestart) {
                LoaderManager.getInstance(this).restartLoader(MediaCollectionLoader.LOADER_ALL, null, mLoaderCallback);
            } else {
                LoaderManager.getInstance(this).initLoader(MediaCollectionLoader.LOADER_ALL, null, mLoaderCallback);
            }
        } else { //????????????????????????
            Bundle bundle = new Bundle();
            bundle.putString(MediaCollectionLoader.KEY_PATH, path);
            LoaderManager.getInstance(this).initLoader(MediaCollectionLoader.LOADER_CATEGORY, bundle, mLoaderCallback);
        }
    }


    /**
     * ????????????
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
        if (resultCode == RESULT_OK) {
            if (requestCode == AlbumPreviewActivity.REQUEST_CODE_PREVIEW) {
                boolean submit = data.getBooleanExtra(AlbumPreviewActivity.KEY_SUBMIT, false);
                boolean notifyData = data.getBooleanExtra(AlbumPreviewActivity.KEY_NOTIFY_DATA, false);
                if (submit) {
                    submitResult(); //????????????
                } else if (notifyData) {
                    mImageAdapter.notifyDataSetChanged(); //????????????
                    updateActivityToolbar();
                }
            } else if (requestCode == CameraUtil.REQUEST_CAMERA) {
                File imageFile = CameraUtil.onActivityResult(requestCode, resultCode, data);
                if (imageFile != null) {
                    Uri uri = Uri.fromFile(imageFile);
                    getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    MediaBean bean = new MediaBean(uri, imageFile.getAbsolutePath());
                    ResultModel.add(bean);
                    submitResult();
                }
            }
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
        CameraUtil.startCameraAction(this);
    }

    /**
     * notify callback
     *
     * @param position
     * @param mediaBean mediaBean data
     */
    private void selectImageFromGrid(int position, MediaBean mediaBean) {
        if (mediaBean != null) {
            if (mIsSingleMode) {
                ResultModel.addSingle(mediaBean);
            } else {
                if (ResultModel.contains(mediaBean)) {
                    ResultModel.remove(mediaBean);
                } else {
                    if (!ResultModel.checkCanAdd()) {
                        Toast.makeText(getActivity(), R.string.ivp_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ResultModel.addMulti(mediaBean);
                }
            }
            mImageAdapter.notifyDataSetChanged();

            updateActivityToolbar();
        }
    }

    private void updateActivityToolbar() {
        if (getActivity() != null && !getActivity().isFinishing() && (getActivity() instanceof ImagePickerActivity)) {
            ((ImagePickerActivity) getActivity()).updateDoneText();
        }
    }

    public void submitResult() {
        if (ResultModel.isEmpty()) {
            LogUtil.e("????????????????????????0????????????");
            return;
        }
        if (ImagePicker.getInstance().getOnPickResultListener() != null) {
            ImagePicker.getInstance().getOnPickResultListener().onPickResult(ResultModel.getList());
        }
        getActivity().finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        if (mRvMedia != null) {
            mRvMedia.removeOnScrollListener(mOnScrollListener);
        }
        PermissionsUtil.getInstance().removeListener();
        super.onDestroyView();
    }

}
