package com.example.squadup.features.events.moredetails

data class MoreDetailsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val eventId: Int? = null,
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,

    val date: String = "",
    val time: String = "",
    val registrationPeriod: String = "",

    val entryType: String = "",
    val eventStatus: String = "",
    val modalityName: String = "",
    val formatName: String = "",

    val teamRequirementTitle: String = "",
    val teamRequirementDescription: String = "",

    val priceLabel: String = "",
    val entryFeeLabel: String = "",

    val registeredTeams: Int = 0,
    val maxTeams: Int = 0,
    val spotsLeft: Int = 0,

    val rules: List<String> = emptyList(),

    val venueName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,

    val creatorId: Int? = null
) {
    val hasImage: Boolean
        get() = !imageUrl.isNullOrBlank()

    val hasDescription: Boolean
        get() = description.isNotBlank()

    val hasRules: Boolean
        get() = rules.isNotEmpty()

    val hasLocation: Boolean
        get() = latitude != null && longitude != null
}
