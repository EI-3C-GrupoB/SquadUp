package com.example.squadup.core.permissions

import com.example.squadup.core.enums.EventParticipationType
import com.example.squadup.core.enums.UserRole

object EventPermissions {

    fun canCreateEvent(userRole: UserRole): Boolean {
        return userRole == UserRole.ORGANIZER ||
                userRole == UserRole.PLAYER_ORGANIZER
    }

    fun canManageEvent(
        currentUserId: Int?,
        eventCreatorId: Int?,
        userRole: UserRole
    ): Boolean {
        if (currentUserId == null || eventCreatorId == null) {
            return false
        }

        return canCreateEvent(userRole) && currentUserId == eventCreatorId
    }

    fun canParticipateInEvent(
        currentUserId: Int?,
        eventCreatorId: Int?,
        userRole: UserRole
    ): Boolean {
        if (currentUserId == null) {
            return false
        }

        val isPlayer = userRole == UserRole.PLAYER ||
                userRole == UserRole.PLAYER_ORGANIZER

        val isOwnEvent = eventCreatorId != null && currentUserId == eventCreatorId

        return isPlayer && !isOwnEvent
    }

    fun canParticipateIndividually(
        currentUserId: Int?,
        eventCreatorId: Int?,
        userRole: UserRole,
        participationType: EventParticipationType
    ): Boolean {
        val canParticipate = canParticipateInEvent(
            currentUserId = currentUserId,
            eventCreatorId = eventCreatorId,
            userRole = userRole
        )

        val acceptsIndividualParticipation =
            participationType == EventParticipationType.INDIVIDUAL ||
                    participationType == EventParticipationType.INDIVIDUAL_AND_TEAM

        return canParticipate && acceptsIndividualParticipation
    }

    fun canParticipateWithTeam(
        currentUserId: Int?,
        eventCreatorId: Int?,
        userRole: UserRole,
        participationType: EventParticipationType
    ): Boolean {
        val canParticipate = canParticipateInEvent(
            currentUserId = currentUserId,
            eventCreatorId = eventCreatorId,
            userRole = userRole
        )

        val acceptsTeamParticipation =
            participationType == EventParticipationType.TEAM ||
                    participationType == EventParticipationType.INDIVIDUAL_AND_TEAM

        return canParticipate && acceptsTeamParticipation
    }

    fun shouldShowManageEventAction(
        currentUserId: Int?,
        eventCreatorId: Int?,
        userRole: UserRole
    ): Boolean {
        return canManageEvent(
            currentUserId = currentUserId,
            eventCreatorId = eventCreatorId,
            userRole = userRole
        )
    }

    fun shouldShowParticipationActions(
        currentUserId: Int?,
        eventCreatorId: Int?,
        userRole: UserRole
    ): Boolean {
        return canParticipateInEvent(
            currentUserId = currentUserId,
            eventCreatorId = eventCreatorId,
            userRole = userRole
        )
    }

    fun shouldShowCreateEventAction(userRole: UserRole): Boolean {
        return canCreateEvent(userRole)
    }
}