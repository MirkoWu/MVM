package com.mirkowu.lib_photo.mediaLoader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.PickerConfig;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.FolderBean;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.bean.MineType;
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
    public static final int LOADER_ALL = 0; //加载全部
    public static final int LOADER_CATEGORY = 1; //分文件夹加载
    public static final String KEY_PATH = "path"; //文件夹路径

    private final String[] IMAGE_PROJECTION = {
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns._ID,
    };


    private final String selectionSingle = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)";
    private final String selectionAll = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)";

    private final String[] selectionArgsImage = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
    private final String[] selectionArgsVideo = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
    private final String[] selectionAllArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};

    // folder result data set
    private boolean mLoadFinished; //是否已经加载完毕 该标识为了避免 onResume时再次调用
    private boolean mHasFolderGenerated; //文件夹是否已遍历 生成过
    private ArrayList<FolderBean> mResultFolder; //文件夹列表

    private Context mContext;
    private ICollectionLoaderCallback mCallback;
    private boolean mIsShowGif;
    private boolean mIsShowVideo;
    private boolean mIsOnlyVideo;


    public MediaCollectionLoader(Context context, ICollectionLoaderCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mResultFolder = new ArrayList<>();
        PickerConfig config = ImagePicker.getInstance().getPickerConfig();
        mIsShowGif = config.isShowGif();
        mIsShowVideo = config.isShowVideo();
        mIsOnlyVideo = config.isOnlyVideo();
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader cursorLoader = null;
        Uri contentUri = MediaStore.Files.getContentUri("external");
        if (id == LOADER_ALL) { //加载所有
            String selection;
            String[] selectionArgs;
            if (mIsOnlyVideo || !mIsShowVideo) {
                selection = selectionSingle;
                if (mIsOnlyVideo) {
                    selectionArgs = selectionArgsVideo;
                } else {
                    selectionArgs = selectionArgsImage;
                }
            } else {
                selection = selectionAll;
                selectionArgs = selectionAllArgs;
            }

            cursorLoader = new CursorLoader(mContext,
                    contentUri,
                    IMAGE_PROJECTION,
                    selection,
                    selectionArgs,
                    IMAGE_PROJECTION[2] + " DESC");
        }
        mLoadFinished = false;
        mHasFolderGenerated = false;
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (!mLoadFinished && data != null) {
            mLoadFinished = true;
            mResultFolder.clear();

            if (data.getCount() > 0) {
                List<MediaBean> allList = new ArrayList<>();
                List<MediaBean> allVideoList = new ArrayList<>();
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    String mineType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    long id = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                    if (!fileExist(path) || TextUtils.isEmpty(mineType)) {
                        continue;
                    }

                    //过滤GIF
                    if (!mIsShowGif && (mineType.contains(MineType.GIF) || path.endsWith(MineType.GIF))) {
                        continue;
                    }

                    Uri contentUri = MediaStore.Files.getContentUri("external");
                    Uri uri = ContentUris.withAppendedId(contentUri, id);

                    //创建单个实体类
                    MediaBean mediaBean = null;
                    if (mineType.contains(MineType.VIDEO)) {
                        long duration = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                        mediaBean = new MediaBean(uri, path, name, mineType, dateTime, duration);
                        allVideoList.add(mediaBean);
                    } else {
                        mediaBean = new MediaBean(uri, path, name, mineType, dateTime);
                    }
                    allList.add(mediaBean);

                    //没有创建文件夹，先创建文件夹，已创建则直接加入
                    if (!mHasFolderGenerated) {
                        // get all folder data
                        File folderFile = new File(path).getParentFile();
                        if (folderFile != null && folderFile.exists()) {
                            String fp = folderFile.getAbsolutePath();
                            FolderBean f = getFolderByPath(fp);
                            if (f == null) {
                                FolderBean folder = new FolderBean();
                                folder.name = folderFile.getName();
                                folder.path = fp;
                                folder.cover = mediaBean;
                                List<MediaBean> mediaBeanList = new ArrayList<>();
                                mediaBeanList.add(mediaBean);
                                folder.mediaList = mediaBeanList;
                                mResultFolder.add(folder);
                            } else {
                                f.mediaList.add(mediaBean);
                            }
                        }
                    }

                } while (data.moveToNext());

                //混合类型，添加所有视频集合，如果有的话
                if (!mIsOnlyVideo && mIsShowVideo && !allVideoList.isEmpty()) {
                    FolderBean allVideoFolder = new FolderBean();
                    allVideoFolder.name = mContext.getResources().getString(R.string.ivp_all_video);
                    allVideoFolder.path = "/sdcard";
                    allVideoFolder.cover = allVideoList.get(0);
                    allVideoFolder.mediaList = allVideoList;
                    mResultFolder.add(0, allVideoFolder);
                }

                //添加所有图片(包括视频)集合
                if (!allList.isEmpty()) {
                    //构造所有图片的集合
                    FolderBean allImagesFolder = new FolderBean();

                    if (mIsOnlyVideo) {
                        allImagesFolder.name = mContext.getResources().getString(R.string.ivp_all_video);
                    } else if (mIsShowVideo) {
                        allImagesFolder.name = mContext.getResources().getString(R.string.ivp_all_image_video);
                    } else {
                        allImagesFolder.name = mContext.getResources().getString(R.string.ivp_all_image);
                    }
                    allImagesFolder.path = "/sdcard";
                    allImagesFolder.cover = allList.get(0);
                    allImagesFolder.mediaList = allList;
                    mResultFolder.add(0, allImagesFolder);
                }

                mHasFolderGenerated = true;
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

    private FolderBean getFolderByPath(String path) {
        if (mResultFolder != null) {
            for (FolderBean folder : mResultFolder) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }
}
