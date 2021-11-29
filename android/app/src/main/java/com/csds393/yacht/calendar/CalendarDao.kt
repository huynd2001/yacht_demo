package com.csds393.yacht.calendar

import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface CalendarDao {

    /* Create */

    @Insert
    fun insertEvent(event: CalendarEvent)

    @Insert
    fun insertEvent(event: RecurringCalendarEvent)

    @Insert(onConflict = ABORT)
    fun _addExceptionForEvent(exception: RecurringCalendarEvent.Exception)


    @Insert
    fun __insertTask(task: Task): Long

    @Insert(onConflict = ABORT)
    fun associateTaskWithEvent(eventAndTask: EventAndTask)

    @Transaction
    fun insertTask(name: String, eventID: Long) {
        val taskID = __insertTask(Task(name))
        associateTaskWithEvent(EventAndTask(eventID, taskID))
    }


    /* Read */

    @Query("SELECT * FROM normal_events where :earliest <= startDate <= :latest")
    fun getEventsStartingInDateWindow(earliest: LocalDate, latest: LocalDate): List<CalendarEvent>

    fun getEventsStartingInDateTimeWindow(earliest: LocalDateTime, latest: LocalDateTime) =
            getEventsStartingInDateWindow(earliest.toLocalDate(), latest.toLocalDate())

    @Query("SELECT * FROM tasks WHERE taskID in (SELECT taskID FROM event_task_table WHERE eventID == :eventID)")
    fun getTasksForEvent(eventID: Long): List<Task>

    @Transaction
    fun getTasksForEventAsMap(eventID: Long): List<Map<String, String>> =
            getTasksForEvent(eventID).map {
                buildMap(capacity = 3) {
                    put("name", it.name)
                    put("completed", it.completed.toString())
                    put("taskID", it.taskID.toString())
                }
            }


    @Query("SELECT * FROM recurring_events")
    fun _getRecurringEvents(): List<RecurringCalendarEvent>

    @Query("SELECT date FROM recurrence_exceptions WHERE event_id = :id")
    fun _getExceptionsForEvent(id: Long): List<LocalDate>

    @Transaction // in newer versions of Room, this can be replaced with multimap return type
    fun getRecurringEventsWithExceptions() =
            // RecCalEvents retrieved from database guaranteed to have non-null rec_id
            _getRecurringEvents().associateWith { _getExceptionsForEvent(it.rec_id!!) }


    /* Update */

    @Update
    fun updateEvent(event: CalendarEvent)

    @Update
    fun updateEvent(event: RecurringCalendarEvent)

    @Update
    fun updateTask(task: Task)


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
    fun deleteAllExceptionsForRecurringEvent(recurringCalendarEventID: Long)

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
