package com.nznlabs.familymap240.util

import models.Event

class SortingUtil {

    companion object {
        fun sortEvents(events: List<Event>): List<Event> {
            val birthEvents = events.filter { it.eventType == "birth" }
            val otherEvents = events.filter {it.eventType != "birth" && it.eventType != "death"}.sortedBy { it.year }
            var currentYear = 0
            val otherEventsSorted = mutableListOf<Event>()
            val tmpEvents = mutableListOf<Event>()
            for (event in otherEvents) {
                if (currentYear == event.year) {
                    tmpEvents.add(event)
                } else {
                    if (tmpEvents.isNotEmpty()) {
                        tmpEvents.add(event)
                        otherEventsSorted.addAll(tmpEvents.sortedBy { it.eventType.lowercase() })
                        tmpEvents.clear()
                    } else {
                        currentYear = event.year
                        otherEventsSorted.add(event)
                    }
                }
            }
            val deathEvents = events.filter { it.eventType == "death" }

            val sortedEvents = mutableListOf<Event>()
            sortedEvents.addAll(birthEvents)
            sortedEvents.addAll(otherEventsSorted)
            sortedEvents.addAll(deathEvents)
            return sortedEvents
        }
    }
}