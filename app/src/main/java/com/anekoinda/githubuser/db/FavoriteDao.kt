package com.anekoinda.githubuser.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: Favorite)

    @Query("SELECT * FROM favorite_user")
    fun getAll(): LiveData<List<Favorite>>

    @Query("SELECT COUNT(*) FROM favorite_user WHERE favorite_user.id = :id")
    fun check(id: Int): Int

    @Query("DELETE FROM favorite_user WHERE favorite_user.id = :id")
    fun delete(id: Int): Int

}