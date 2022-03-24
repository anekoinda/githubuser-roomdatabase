package com.anekoinda.githubuser.ui.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anekoinda.githubuser.R
import com.anekoinda.githubuser.databinding.ActivityFavoriteBinding
import com.anekoinda.githubuser.db.Favorite
import com.anekoinda.githubuser.model.User
import com.anekoinda.githubuser.ui.detail.DetailUserActivity
import com.anekoinda.githubuser.ui.main.ListUserAdapter
import com.anekoinda.githubuser.ui.setting.SettingActivity

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: ListUserAdapter
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // change title action bar
        supportActionBar?.title = "Favorite User"

        adapter = ListUserAdapter()

        viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Intent(this@FavoriteActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.USERNAME, data.login)
                    it.putExtra(DetailUserActivity.ID, data.id)
                    it.putExtra(DetailUserActivity.AVATAR, data.avatar_url)
                    startActivity(it)
                }
            }
        })

        binding.apply {
            val layoutManager = LinearLayoutManager(this@FavoriteActivity)
            rvUser.layoutManager = layoutManager
            rvUser.setHasFixedSize(true)
            rvUser.adapter = adapter
        }

        viewModel.getAll()?.observe(this) {
            if (it != null) {
                val list = mapList(it)
                adapter.setList(list)
            }
        }
    }

    private fun mapList(users: List<Favorite>): ArrayList<User> {
        val listUser = ArrayList<User>()
        for (user in users) {
            val userMapped = User(
                user.login,
                user.id,
                user.avatar_url
            )
            listUser.add(userMapped)
        }
        return listUser
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_setting -> {
            Intent(this@FavoriteActivity, SettingActivity::class.java).also {
                startActivity(it)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}