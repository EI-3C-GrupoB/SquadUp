package com.example.squadup.features.events

data class EventsData(
    val featuredEvent: FeaturedEventItem?,
    val upcomingEvents: List<UpcomingEventItem>,
    val browseEvents: List<BrowseEventItem>
) {
    companion object {
        fun empty(): EventsData {
            return EventsData(
                featuredEvent = null,
                upcomingEvents = emptyList(),
                browseEvents = emptyList()
            )
        }
    }
}