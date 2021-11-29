package com.csds393.yacht.calendar

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "tasks",
    primaryKeys = ["taskListID", "name"],
    foreignKeys = [ForeignKey(
        entity = EventTaskList::class,
        parentColumns = ["taskListID"],
        childColumns = ["taskListID"],
        onDelete = CASCADE
    )]
)
data class Task(
    val taskListID: Int,
    val name: String,
    val completed: Boolean,
)

@Entity(
    tableName = "event_task_list_table",
    foreignKeys = [ForeignKey(
        entity = CalendarEvent::class,
        parentColumns = ["id"],
        childColumns = ["eventID"],
        onDelete = CASCADE
    )],
    indices = [Index(value=["eventID"])]
)
data class EventTaskList(
    val eventID: Int,
    @PrimaryKey(autoGenerate = true)
    val taskListID: Int? = null
)
