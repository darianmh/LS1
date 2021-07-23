package com.darian.ls1.Services

import com.darian.ls1.Model.ApiResponse
import com.darian.ls1.Model.LinkCreateResponse
import com.darian.ls1.Model.ShowStaticsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ILinkService {

    @GET("/api/Links/{link}")
    fun createLink(@Path("link") link: String): Call<ApiResponse<LinkCreateResponse>>
    @GET("/api/Statics/{link}")
    fun showStatics(@Path("link") link: String): Call<ApiResponse<ShowStaticsResponse>>
}