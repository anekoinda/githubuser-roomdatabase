package com.anekoinda.githubuser.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anekoinda.githubuser.db.FavoriteDao as FavoriteDao

@Database(entities = [Favorite::class], version = 1, exportSchema = false)

abstract class UserRoomDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: UserRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): UserRoomDatabase {
            if (INSTANCE == null) {
                synchronized(UserRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        UserRoomDatabase::class.java, "db_fav"
                    )
                        .build()
                }
            }
            return INSTANCE as UserRoomDatabase
        }
    }
}