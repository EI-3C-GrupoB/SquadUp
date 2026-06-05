package com.example.squadup.features.events.moredetails

data class MoreDetailsUiState(
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val entryType: String = "",
    val teamRequirementTitle: String = "",
    val teamRequirementDescription: String = "",
    val rules: List<String> = emptyList(),
    val venueName: String = ""
)
