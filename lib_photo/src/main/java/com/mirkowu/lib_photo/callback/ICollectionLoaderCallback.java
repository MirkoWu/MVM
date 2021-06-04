package com.mirkowu.lib_photo.callback;


import com.mirkowu.lib_photo.bean.FolderBean;

import java.util.List;

/**
 * @author by DELL
 * @date on 2018/8/21
 * @describe
 */
public interface ICollectionLoaderCallback {

    void onLoadFinish(List<FolderBean> folderList);
}
