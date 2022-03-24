package com.anekoinda.githubuser.ui.detail

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.anekoinda.githubuser.api.ApiConfig
import com.anekoinda.githubuser.db.Favorite
import com.anekoinda.githubuser.db.FavoriteDao
import com.anekoinda.githubuser.db.UserRoomDatabase
import com.anekoinda.githubuser.model.UserDetailResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailViewModel(application: Application) : AndroidViewModel(application) {
    val user = MutableLiveData<UserDetailResponse>()

    private var userDao: FavoriteDao?
    private var userDatabase: UserRoomDatabase? = UserRoomDatabase.getDatabase(application)

    init {
        userDao = userDatabase?.favoriteDao()
    }

    fun setUserDetail(query: String) {
        val client = ApiConfig.getApiService().getUserDetail(query)
        client.enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {
                if (response.isSuccessful) {
                    user.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getUserDetail(): LiveData<UserDetailResponse> {
        return user
    }

    fun insert(id: Int, username: String?, avatar_url: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = Favorite(
                id,
                username,
                avatar_url
            )
            userDao?.insert(user)
        }
    }

    fun check(id: Int) = userDao?.check(id)
    fun delete(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.delete(id)
        }
    }
}