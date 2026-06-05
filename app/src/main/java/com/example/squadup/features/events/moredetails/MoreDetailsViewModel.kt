package com.example.squadup.features.events.moredetails

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MoreDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MoreDetailsUiState())
    val uiState: StateFlow<MoreDetailsUiState> = _uiState.asStateFlow()
}
