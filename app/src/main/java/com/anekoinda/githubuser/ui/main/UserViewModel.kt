package com.anekoinda.githubuser.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.anekoinda.githubuser.model.SettingPreferences
import com.anekoinda.githubuser.model.User
import com.anekoinda.githubuser.model.UserResponse
import com.anekoinda.githubuser.api.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(private val pref: SettingPreferences) : ViewModel() {
    val listUser = MutableLiveData<ArrayList<User>>()

    fun setUser(query: String) {
        val client = ApiConfig.getApiService().getUser(query)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: retrofit2.Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    listUser.postValue(response.body()?.items)
                }
            }

            override fun onFailure(call: retrofit2.Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    fun getUser(): LiveData<ArrayList<User>> {
        return listUser
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}