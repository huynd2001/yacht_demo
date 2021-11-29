package com.csds393.yacht.calendar

import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT
import java.time.LocalDate

@Dao
interface CalendarDao {

    /* Create */

    @Insert
    fun insertEvent(event: CalendarEvent)

    @Insert
    fun insertEvent(event: RecurringCalendarEvent)

    @Insert(onConflict = ABORT)
    fun _addExceptionForEvent(exception: RecurringCalendarEvent.Exception)

    /* Read */

    @Query("SELECT * FROM normal_events WHERE label = :label LIMIT :limit")
    fun getAllWithLabel(label: String, limit: Int = 1): List<CalendarEvent>

    @Query("SELECT label FROM normal_events WHERE :earliest <= startDate <= :latest")
    fun getLabelsInDateRange(earliest: LocalDate, latest: LocalDate): List<String>

    @Query("SELECT * FROM recurring_events")
    fun _getRecurringEvents(): List<RecurringCalendarEvent>

    @Query("SELECT date FROM recurrence_exceptions WHERE event_id = :id")
    fun _getExceptionsForEvent(id: Int): List<LocalDate>

    @Transaction // in newer versions of Room, this can be replaced with multimap return type
    fun getRecurringEventsWithExceptions() =
        // RecCalEvents retrieved from database guaranteed to have non-null rec_id
        _getRecurringEvents().associateWith { _getExceptionsForEvent(it.rec_id!!) }


    /* Update */

    @Update
    fun updateEvent(event: CalendarEvent)

    @Update
    fun updateEvent(event: RecurringCalendarEvent)


    /* Delete */

    @Delete
    fun _deleteEvent(event: CalendarEvent)

    @Transaction
    fun deleteEvent(event: CalendarEvent) {
        val id = event.id!!
        // negative id -> instance of recurring event
        if (id < 0) _addExceptionForEvent(RecurringCalendarEvent.Exception(event.startDate, -id))
        else _deleteEvent(event)
    }

    @Transaction
    fun deleteEvent(event: RecurringCalendarEvent) {
        deleteAllExceptionsForRecurringEvent(event.rec_id!!)
        _deleteEvent(event)
    }

    @Query("DELETE FROM recurrence_exceptions WHERE event_id == :recurringCalendarEventID")
    fun deleteAllExceptionsForRecurringEvent(recurringCalendarEventID: Int)

    @Delete
    fun _deleteEvent(event: RecurringCalendarEvent)

    @Transaction
    fun clearAllEvents() {
        _clearAllNormalEvents()
        _clearAllRecurringEvents()
        _clearAllRecurrenceExceptions()
    }

    @Query("DELETE FROM normal_events")
    fun _clearAllNormalEvents()

    @Query("DELETE FROM recurring_events")
    fun _clearAllRecurringEvents()

    @Query("DELETE FROM recurrence_exceptions")
    fun _clearAllRecurrenceExceptions()

}