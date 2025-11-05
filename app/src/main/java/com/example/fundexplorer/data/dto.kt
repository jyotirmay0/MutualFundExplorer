package com.example.fundexplorer.data
import com.google.gson.annotations.SerializedName

data class MutualFundListDto(
    @SerializedName("schemeCode")
    val schemeCode: String,
    @SerializedName("schemeName")
    val schemeName: String
)

data class MutualFundDetailDto(
    @SerializedName("meta")
    val meta: MetaDto,
    @SerializedName("data")
    val data: List<NavDataDto>,
    @SerializedName("status")
    val status: String
)

data class MetaDto(
    @SerializedName("scheme_code")
    val schemeCode: String,
    @SerializedName("scheme_name")
    val schemeName: String,
    @SerializedName("scheme_category")
    val schemeCategory: String?,
    @SerializedName("scheme_type")
    val schemeType: String?,
    @SerializedName("fund_house")
    val fundHouse: String?
)

data class NavDataDto(
    @SerializedName("date")
    val date: String,
    @SerializedName("nav")
    val nav: String
)