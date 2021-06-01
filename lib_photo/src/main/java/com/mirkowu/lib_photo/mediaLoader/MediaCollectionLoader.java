package com.mirkowu.lib_photo.mediaLoader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.Folder;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.callback.ICollectionLoaderCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by DELL
 * @date on 2018/8/21
 * @describe
 */
public class MediaCollectionLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    // loaders
    public static final int LOADER_ALL = 0;//加载全部
    public static final int LOADER_CATEGORY = 1;//分文件夹加载
    public static final String KEY_PATH = "path";//文件夹路径

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};

    private final String where
            = MediaStore.Video.Media.MIME_TYPE + "=? or "
            + MediaStore.Video.Media.MIME_TYPE + "=? or "
            + MediaStore.Video.Media.MIME_TYPE + "=? or "
            + MediaStore.Video.Media.MIME_TYPE + "=?";

    private final String[] whereArgs = {"image/png", "image/jpg", "image/jpeg"};
    private final String[] whereArgsWithGif = {"image/png", "image/jpg", "image/jpeg", "image/gif"};

    // folder result data set
    private boolean hasFolderGened;//文件夹是否已遍历 生成过
    private boolean mLoadFinished;//是否已经加载完毕 该标识为了避免 onResume时再次调用
    private ArrayList<Folder> mResultFolder;//文件夹列表

    private Context mContext;
    private String[] mSupportMineType;
    private ICollectionLoaderCallback mCallback;


    public MediaCollectionLoader(Context context, ICollectionLoaderCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mResultFolder = new ArrayList<>();
        mSupportMineType = ImagePicker.getInstance().isSupportGif() ? whereArgsWithGif : whereArgs;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == LOADER_ALL) {//加载所有
            cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " + where,
                    mSupportMineType, IMAGE_PROJECTION[2] + " DESC");
        } else if (id == LOADER_CATEGORY) {//加载分类文件夹
            cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" + args.getString(KEY_PATH) + "%'",
                    null, IMAGE_PROJECTION[2] + " DESC");
        }
        mLoadFinished = false;
        hasFolderGened = false;
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (!mLoadFinished && data != null) {
            mLoadFinished = true;
            mResultFolder.clear();

            if (data.getCount() > 0) {
                List<MediaBean> allList = new ArrayList<>();
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    if (!fileExist(path)) {
                        continue;
                    }
                    MediaBean mediaBean = null;
                    if (!TextUtils.isEmpty(name)) {
                        mediaBean = new MediaBean(path, name, dateTime);
                        allList.add(mediaBean);
                    }
                    if (!hasFolderGened) {
                        // get all folder data
                        File folderFile = new File(path).getParentFile();
                        if (folderFile != null && folderFile.exists()) {
                            String fp = folderFile.getAbsolutePath();
                            Folder f = getFolderByPath(fp);
                            if (f == null) {
                                Folder folder = new Folder();
                                folder.name = folderFile.getName();
                                folder.path = fp;
                                folder.cover = mediaBean;
                                List<MediaBean> mediaBeanList = new ArrayList<>();
                                mediaBeanList.add(mediaBean);
                                folder.mediaBeans = mediaBeanList;
                                mResultFolder.add(folder);
                            } else {
                                f.mediaBeans.add(mediaBean);
                            }
                        }
                    }

                } while (data.moveToNext());

                if (!allList.isEmpty()) {
                    //构造所有图片的集合
                    Folder allImagesFolder = new Folder();
                    allImagesFolder.name = mContext.getResources().getString(R.string.ivp_folder_all);
                    allImagesFolder.path = "/sdcard";
                    allImagesFolder.cover = allList.get(0);
                    allImagesFolder.mediaBeans = allList;
                    mResultFolder.add(0, allImagesFolder);
                }
                hasFolderGened = true;
                if (mCallback != null) {
                    mCallback.onLoadFinish(mResultFolder);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    private boolean fileExist(String path) {
        if (!TextUtils.isEmpty(path)) {
            return new File(path).exists();
        }
        return false;
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
}
