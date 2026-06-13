package com.example.squadup.features.events.moredetails

data class MoreDetailsUiState(
    val isLoading: Boolean = false,
    val isJoining: Boolean = false,
    val joiningRegistrationType: String? = null,
    val isTeamPickerVisible: Boolean = false,
    val isLoadingAvailableTeams: Boolean = false,
    val availableTeams: List<MoreDetailsAvailableTeam> = emptyList(),
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

    val creatorId: Int? = null,

    val participationType: String = "individual",
    val isPrivate: Boolean = false,
    val accessCode: String? = null,
    val codeVerified: Boolean = false,
    val registrationStatusLabel: String = "",
    val userEventRegistrationStatus: String? = null,
    val userEventRegistrationType: String? = null,

    val canManageEvent: Boolean = false,
    val canParticipate: Boolean = false,
    val canParticipateIndividually: Boolean = false,
    val canParticipateWithTeam: Boolean = false,

    val eventPrice: Double? = null,
    val userInscricaoId: Int? = null,
    val paymentStatus: String? = null,
    val userTicketId: Int? = null
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

data class MoreDetailsAvailableTeam(
    val id: Int,
    val name: String
)
