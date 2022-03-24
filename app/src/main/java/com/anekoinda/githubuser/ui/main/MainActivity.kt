package com.anekoinda.githubuser.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anekoinda.githubuser.ui.favorite.FavoriteActivity
import com.anekoinda.githubuser.R
import com.anekoinda.githubuser.model.SettingPreferences
import com.anekoinda.githubuser.ui.setting.ViewModelFactory
import com.anekoinda.githubuser.databinding.ActivityMainBinding
import com.anekoinda.githubuser.databinding.ActivitySettingBinding
import com.anekoinda.githubuser.model.User
import com.anekoinda.githubuser.ui.detail.DetailUserActivity
import com.anekoinda.githubuser.ui.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingsetting: ActivitySettingBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: ListUserAdapter
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingsetting = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // change title action bar
        supportActionBar?.title = "Github Search User's"

        val switchTheme = bindingsetting.switchTheme

        val pref = SettingPreferences.getInstance(dataStore)
        val mainViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]
        mainViewModel.getThemeSettings().observe(
            this
        ) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        adapter = ListUserAdapter()

        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Intent(this@MainActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.USERNAME, data.login)
                    it.putExtra(DetailUserActivity.ID, data.id)
                    it.putExtra(DetailUserActivity.AVATAR, data.avatar_url)
                    startActivity(it)
                }
            }
        })

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[UserViewModel::class.java]

        binding.apply {
            val layoutManager = LinearLayoutManager(this@MainActivity)
            rvUser.layoutManager = layoutManager
            rvUser.setHasFixedSize(true)
            rvUser.adapter = adapter

            inputSearch.setOnClickListener {
                searchUser()
            }

            inputSearch.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchUser()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }

        viewModel.getUser().observe(this) {
            if (it != null) {
                adapter.setList(it)
                showLoading(false)
                if (it.isEmpty()) {
                    binding.notFound.visibility = View.VISIBLE
                } else {
                    binding.notFound.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_setting -> {
            Intent(this@MainActivity, SettingActivity::class.java).also {
                startActivity(it)
            }
            true
        }
        R.id.menu_favorite -> {
            Intent(this@MainActivity, FavoriteActivity::class.java).also {
                startActivity(it)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun searchUser() {
        binding.apply {
            val query = inputSearch.text.toString()
            if (query.isEmpty()) return
            showLoading(true)
            viewModel.setUser(query)
            binding.search.visibility = View.INVISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}