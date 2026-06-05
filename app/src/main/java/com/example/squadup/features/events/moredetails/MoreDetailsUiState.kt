package com.example.squadup.features.events.moredetails

data class MoreDetailsUiState(
    val title: String = "Sand Pro Tour: Beach Open",
    val date: String = "Oct 24, 2025",
    val time: String = "6:00 PM - 9:00 PM",
    val entryType: String = "TOURNAMENT ENTRY",
    val teamRequirementTitle: String = "No team needed",
    val teamRequirementDescription: String =
        "In this tournament, participation is individual. The teams will be formed by the event organizers after registration closes.",
    val rules: List<String> = listOf(
        "Round robin format followed by single-elimination playoffs.",
        "Organizers assign teams based on skill level rankings to ensure balanced competition.",
        "Standard pro beach rules apply. Sets played to 21 points, cap at 25.",
        "Fair play is mandatory. Disrespectful conduct leads to immediate disqualification."
    ),
    val venueName: String = "Sunny Coast Beach Arena"
)
