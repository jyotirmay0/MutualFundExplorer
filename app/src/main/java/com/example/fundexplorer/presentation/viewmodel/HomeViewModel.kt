package com.example.fundexplorer.presentation.viewmodel

import androidx.compose.runtime.State

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundexplorer.Reposatory.MutualFundRepository
import com.example.fundexplorer.data.FundCategory
import com.example.fundexplorer.data.MutualFund
import com.example.fundexplorer.data.Resource


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val funds: List<MutualFund> = emptyList(),
    val filteredFunds: List<MutualFund> = emptyList(),
    val error: String = "",
    val searchQuery: String = "",
    val selectedCategory: FundCategory = FundCategory.ALL
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MutualFundRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var searchJob: Job? = null

    init {
        loadTopMutualFunds()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SearchQueryChanged -> {
                _state.value = _state.value.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500) // Debounce
                    if (event.query.isBlank()) {
                        loadTopMutualFunds()
                    } else {
                        searchMutualFunds(event.query)
                    }
                }
            }
            is HomeEvent.CategorySelected -> {
                _state.value = _state.value.copy(selectedCategory = event.category)
                filterFunds()
            }
            HomeEvent.Refresh -> loadTopMutualFunds()
        }
    }

    private fun loadTopMutualFunds() {
        repository.getTopMutualFunds(100).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        funds = result.data ?: emptyList(),
                        isLoading = false,
                        error = ""
                    )
                    filterFunds()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "Unknown error",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun searchMutualFunds(query: String) {
        repository.searchMutualFunds(query).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        funds = result.data ?: emptyList(),
                        isLoading = false,
                        error = ""
                    )
                    filterFunds()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "Unknown error",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun filterFunds() {
        val filtered = if (_state.value.selectedCategory == FundCategory.ALL) {
            _state.value.funds
        } else {
            _state.value.funds.filter {
                it.schemeType == _state.value.selectedCategory.displayName
            }
        }
        _state.value = _state.value.copy(filteredFunds = filtered)
    }
}

sealed class HomeEvent {
    data class SearchQueryChanged(val query: String) : HomeEvent()
    data class CategorySelected(val category: FundCategory) : HomeEvent()
    object Refresh : HomeEvent()
}
