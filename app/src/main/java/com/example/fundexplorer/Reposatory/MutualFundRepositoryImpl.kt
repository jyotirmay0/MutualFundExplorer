package com.example.fundexplorer.Reposatory


import com.example.fundexplorer.data.FundCategory
import com.example.fundexplorer.data.MutualFund
import com.example.fundexplorer.data.MutualFundApi
import com.example.fundexplorer.data.MutualFundDetail
import com.example.fundexplorer.data.NavData
import com.example.fundexplorer.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MutualFundRepositoryImpl @Inject constructor(
    private val api: MutualFundApi
) : MutualFundRepository {

    override fun getAllMutualFunds(): Flow<Resource<List<MutualFund>>> = flow {
        try {
            emit(Resource.Loading())
            val funds = api.getAllMutualFunds().map { dto ->
                MutualFund(
                    schemeCode = dto.schemeCode,
                    schemeName = dto.schemeName,
                    schemeType = categorizeScheme(dto.schemeName)
                )
            }
            emit(Resource.Success(funds))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(message = "An unexpected error occurred"))
        }
    }

    override fun getMutualFundDetails(schemeCode: String): Flow<Resource<MutualFundDetail>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.getMutualFundDetails(schemeCode)

            val detail = MutualFundDetail(
                schemeCode = response.meta.schemeCode,
                schemeName = response.meta.schemeName,
                navHistory = response.data.map { navDto ->
                    NavData(date = navDto.date, nav = navDto.nav)
                },
                schemeType = response.meta.schemeType ?: categorizeScheme(response.meta.schemeName),
                fundHouse = response.meta.fundHouse
            )
            emit(Resource.Success(detail))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(message = "An unexpected error occurred"))
        }
    }

    override fun searchMutualFunds(query: String): Flow<Resource<List<MutualFund>>> = flow {
        try {
            emit(Resource.Loading())
            val allFunds = api.getAllMutualFunds()
            val filtered = allFunds.filter {
                it.schemeName.contains(query, ignoreCase = true)
            }.map { dto ->
                MutualFund(
                    schemeCode = dto.schemeCode,
                    schemeName = dto.schemeName,
                    schemeType = categorizeScheme(dto.schemeName)
                )
            }
            emit(Resource.Success(filtered))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(message = "An unexpected error occurred"))
        }
    }

    override fun getTopMutualFunds(limit: Int): Flow<Resource<List<MutualFund>>> = flow {
        try {
            emit(Resource.Loading())
            val allFunds = api.getAllMutualFunds()

            // Logic for "Top Funds": Popular fund houses
            val topFunds = allFunds.filter { dto ->
                val name = dto.schemeName.lowercase()
                name.contains("sbi") ||
                        name.contains("hdfc") ||
                        name.contains("icici") ||
                        name.contains("axis") ||
                        name.contains("kotak") ||
                        name.contains("birla") ||
                        name.contains("reliance") ||
                        name.contains("nippon") ||
                        name.contains("uti")
            }.take(limit).map { dto ->
                MutualFund(
                    schemeCode = dto.schemeCode,
                    schemeName = dto.schemeName,
                    schemeType = categorizeScheme(dto.schemeName)
                )
            }

            emit(Resource.Success(topFunds))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(message = "An unexpected error occurred"))
        }
    }

    private fun categorizeScheme(schemeName: String): String {
        val name = schemeName.lowercase()
        return when {
            name.contains("equity") || name.contains("growth") ||
                    name.contains("midcap") || name.contains("largecap") ||
                    name.contains("smallcap") || name.contains("multicap") -> FundCategory.EQUITY.displayName

            name.contains("debt") || name.contains("income") ||
                    name.contains("bond") || name.contains("gilt") ||
                    name.contains("liquid") || name.contains("money market") -> FundCategory.DEBT.displayName

            name.contains("hybrid") || name.contains("balanced") ||
                    name.contains("aggressive") || name.contains("conservative") -> FundCategory.HYBRID.displayName

            name.contains("retirement") || name.contains("children") ||
                    name.contains("pension") -> FundCategory.SOLUTION.displayName

            else -> FundCategory.OTHER.displayName
        }
    }
}