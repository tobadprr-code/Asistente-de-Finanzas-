package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeline_events")
data class TimelineEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val eventType: String, // TRANSACTION, ASSET_BUY, ASSET_SALE, GOAL_PROGRESS, INSIGHT, SYSTEM
    val timestampMillis: Long = System.currentTimeMillis(),
    val tags: String = ""
)
