package com.example.fundexplorer.data

import retrofit2.http.GET
import retrofit2.http.Path


interface MutualFundApi {

    @GET("mf")
    suspend fun getAllMutualFunds(): List<MutualFundListDto>

    @GET("mf/{schemeCode}")
    suspend fun getMutualFundDetails(
        @Path("schemeCode") schemeCode: String
    ): MutualFundDetailDto
}