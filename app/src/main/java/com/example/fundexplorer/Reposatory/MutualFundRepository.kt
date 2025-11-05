package com.example.fundexplorer.Reposatory


import com.example.fundexplorer.data.MutualFund
import com.example.fundexplorer.data.MutualFundDetail
import com.example.fundexplorer.data.Resource
import kotlinx.coroutines.flow.Flow

interface MutualFundRepository {
    fun getAllMutualFunds(): Flow<Resource<List<MutualFund>>>
    fun getMutualFundDetails(schemeCode: String): Flow<Resource<MutualFundDetail>>
    fun searchMutualFunds(query: String): Flow<Resource<List<MutualFund>>>
    fun getTopMutualFunds(limit: Int = 50): Flow<Resource<List<MutualFund>>>
}
