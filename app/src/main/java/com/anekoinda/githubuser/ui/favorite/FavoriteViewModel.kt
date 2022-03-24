package com.anekoinda.githubuser.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anekoinda.githubuser.db.Favorite
import com.anekoinda.githubuser.db.FavoriteDao
import com.anekoinda.githubuser.db.UserRoomDatabase

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private var userDao: FavoriteDao?
    private var userDatabase: UserRoomDatabase? = UserRoomDatabase.getDatabase(application)

    init {
        userDao = userDatabase?.favoriteDao()
    }

    fun getAll(): LiveData<List<Favorite>>? {
        return userDao?.getAll()
    }
}