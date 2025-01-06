package com.mirkowu.mvm.network

import com.mirkowu.mvm.bean.GankBaseBean
import com.mirkowu.mvm.bean.ImageListBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {
    @GET("api/getImages")
    fun loadImage(
        @Query("type") type: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Flow<GankBaseBean<ImageListBean>>
}