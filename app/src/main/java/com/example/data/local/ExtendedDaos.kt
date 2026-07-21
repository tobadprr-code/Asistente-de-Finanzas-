package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AiInsight
import com.example.data.model.AppNotification
import com.example.data.model.TimelineEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineEventDao {
    @Query("SELECT * FROM timeline_events ORDER BY timestampMillis DESC")
    fun getAllEvents(): Flow<List<TimelineEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TimelineEvent)
}

@Dao
interface AiInsightDao {
    @Query("SELECT * FROM ai_insights ORDER BY dateMillis DESC")
    fun getAllInsights(): Flow<List<AiInsight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: AiInsight)
}

@Dao
interface AppNotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY timestampMillis DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllAsRead()
}
