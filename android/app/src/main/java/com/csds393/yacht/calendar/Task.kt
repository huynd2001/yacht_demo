package com.csds393.yacht.calendar

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "tasks",
)
data class Task(
    val name: String,
    val completed: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val taskID: Long = 0,
)

@Entity(
    tableName = "event_task_table",
    primaryKeys = ["eventID", "taskID"],
    foreignKeys = [
        ForeignKey(
            entity = CalendarEvent::class,
            parentColumns = ["id"],
            childColumns = ["eventID"],
            onDelete = CASCADE
            ),
            ForeignKey(
                entity = Task::class,
                parentColumns = ["taskID"],
                childColumns = ["taskID"],
                onDelete = CASCADE
            ),
        ],
        indices = [Index(value=["eventID"]), Index(value=["taskID"])]
)
data class EventAndTask(val eventID: Long, val taskID: Long)

