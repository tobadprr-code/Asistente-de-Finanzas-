package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_insights")
data class AiInsight(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val insightType: String = "INSIGHT", // INSIGHT, RECOMMENDATION, DIGEST
    val metricImpact: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String = "INFO", // GOAL, WARNING, INSIGHT, SYSTEM
    val timestampMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
