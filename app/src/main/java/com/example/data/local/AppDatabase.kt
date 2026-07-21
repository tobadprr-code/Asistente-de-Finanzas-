package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AiInsight
import com.example.data.model.AppNotification
import com.example.data.model.Asset
import com.example.data.model.Goal
import com.example.data.model.TimelineEvent
import com.example.data.model.Transaction
import com.example.data.model.UserProfile

@Database(
    entities = [
        Transaction::class,
        Goal::class,
        UserProfile::class,
        Asset::class,
        TimelineEvent::class,
        AiInsight::class,
        AppNotification::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun assetDao(): AssetDao
    abstract fun timelineEventDao(): TimelineEventDao
    abstract fun aiInsightDao(): AiInsightDao
    abstract fun appNotificationDao(): AppNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "valor_finance_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
