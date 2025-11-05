package com.example.fundexplorer.presentation.viewmodel


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundexplorer.Reposatory.MutualFundRepository
import com.example.fundexplorer.data.MutualFundDetail
import com.example.fundexplorer.data.NavData
import com.example.fundexplorer.data.Resource
import com.example.fundexplorer.data.TimeRange

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DetailState(
    val isLoading: Boolean = false,
    val fundDetail: MutualFundDetail? = null,
    val filteredNavData: List<NavData> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.ONE_MONTH,
    val error: String = ""
)


sealed class DetailEvent {
    data class TimeRangeSelected(val timeRange: TimeRange) : DetailEvent()
    object Refresh : DetailEvent()
}


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MutualFundRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(DetailState())
    val state: State<DetailState> = _state

    init {
        savedStateHandle.get<String>("schemeCode")?.let { schemeCode ->
            loadFundDetails(schemeCode)
        }
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.TimeRangeSelected -> {
                _state.value = _state.value.copy(selectedTimeRange = event.timeRange)
                filterNavData(event.timeRange)
            }
            DetailEvent.Refresh -> {
                _state.value.fundDetail?.let { loadFundDetails(it.schemeCode) }
            }
        }
    }

    private fun loadFundDetails(schemeCode: String) {
        repository.getMutualFundDetails(schemeCode).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        fundDetail = result.data,
                        isLoading = false,
                        error = ""
                    )
                    filterNavData(_state.value.selectedTimeRange)
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

    private fun filterNavData(timeRange: TimeRange) {
        val fundDetail = _state.value.fundDetail ?: return
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -timeRange.months)
        val cutoffDate = calendar.time

        val filtered = fundDetail.navHistory.filter { navData ->
            try {
                val navDate = dateFormat.parse(navData.date)
                navDate != null && navDate.after(cutoffDate)
            } catch (e: Exception) {
                false
            }
        }.reversed()

        _state.value = _state.value.copy(filteredNavData = filtered)
    }
}


