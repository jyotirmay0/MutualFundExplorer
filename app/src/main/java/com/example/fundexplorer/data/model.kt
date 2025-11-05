package com.example.fundexplorer.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MutualFund(
    val schemeCode: String,
    val schemeName: String,
    val schemeType: String? = null
) : Parcelable

@Parcelize
data class MutualFundDetail(
    val schemeCode: String,
    val schemeName: String,
    val navHistory: List<NavData>,
    val schemeType: String? = null,
    val fundHouse: String? = null
) : Parcelable

@Parcelize
data class NavData(
    val date: String,
    val nav: String
) : Parcelable

enum class FundCategory(val displayName: String) {
    ALL("All Funds"),
    EQUITY("Equity"),
    DEBT("Debt"),
    HYBRID("Hybrid"),
    SOLUTION("Solution Oriented"),
    OTHER("Other")
}

enum class TimeRange(val displayName: String, val months: Int) {
    ONE_MONTH("1M", 1),
    THREE_MONTHS("3M", 3),
    SIX_MONTHS("6M", 6),
    ONE_YEAR("1Y", 12)
}
