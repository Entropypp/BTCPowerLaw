package com.entropypp.btcpowerlaw.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.entropypp.btcpowerlaw.data.BtcRepository
import com.entropypp.btcpowerlaw.data.api.RetrofitClient
import com.entropypp.btcpowerlaw.domain.model.BtcMetrics
import com.entropypp.btcpowerlaw.widget.BTCWidgetWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BtcRepository(
        RetrofitClient.coinGeckoApi,
        RetrofitClient.fearAndGreedApi,
        RetrofitClient.mempoolApi,
        application
    )

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private var currentMetrics: BtcMetrics? = null

    init {
        refreshMetrics()
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        refreshMetrics(date)
    }

    private fun updateMetricsForDate(date: LocalDate) {
        // This is now handled by refreshMetrics(date) which calls the repository
    }

    fun refreshMetrics(date: LocalDate = _selectedDate.value) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                val metrics = repository.getBtcMetrics(date)
                _uiState.value = MainUiState.Success(metrics)
                
                // Trigger a widget update whenever we refresh metrics
                updateWidgetData()
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun updateWidgetData() {
        // Enqueue a worker to fetch TODAY'S data and update the widget
        // This ensures the widget always stays on the current day's data
        BTCWidgetWorker.enqueueImmediate(getApplication())
    }
}

sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(val metrics: BtcMetrics) : MainUiState
    data class Error(val message: String) : MainUiState
}

