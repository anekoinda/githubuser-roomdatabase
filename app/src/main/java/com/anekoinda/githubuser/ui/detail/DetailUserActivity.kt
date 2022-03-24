package com.anekoinda.githubuser.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.anekoinda.githubuser.R
import com.anekoinda.githubuser.databinding.ActivityDetailUserBinding
import com.anekoinda.githubuser.ui.setting.SettingActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailUserActivity : AppCompatActivity() {
    companion object {
        const val USERNAME = "username"
        const val ID = "id"
        const val AVATAR = "avatar_url"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.user_following,
            R.string.user_followers
        )
    }

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var viewModel: UserDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(USERNAME)
        val id = intent.getIntExtra(ID, 0)
        val avatar = intent.getStringExtra(USERNAME)

        val bundle = Bundle()
        bundle.putString(USERNAME, username)
        bundle.putInt(ID, id)
        bundle.putString(AVATAR, avatar)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, bundle)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        viewModel = ViewModelProvider(this)[UserDetailViewModel::class.java]
        if (username != null) {
            viewModel.setUserDetail(username)
            showLoading(true)
        }
        viewModel.getUserDetail().observe(this) {
            if (it != null) {
                binding.apply {
                    Glide.with(this@DetailUserActivity).load(it.avatar_url)
                        .transition(DrawableTransitionOptions.withCrossFade()).centerCrop()
                        .into(imgItemAvatar)
                    tvItemName.text = it.name
                    tvItemUsername.text = it.login
                    tvItemRepository.text = it.public_repos
                    tvItemCompany.text = it.company
                    tvItemLocation.text = it.location
                    tvItemFollowers.text = it.followers
                    tvItemFollowing.text = it.following

                    tvItemFollowers.append(" Followers")
                    tvItemFollowing.append(" Following")
                    tvItemRepository.append(" Repositories")

                    if (it.company == null) {
                        tvItemCompany.text = "-"
                    }
                    if (it.location == null) {
                        tvItemLocation.text = "-"
                    }
                    if (it.name == null) {
                        tvItemName.text = "[Name not set]"
                    }

                    iconCompany.visibility = View.VISIBLE
                    iconLocation.visibility = View.VISIBLE
                    iconFollowers.visibility = View.VISIBLE
                    iconFollowing.visibility = View.VISIBLE
                    showLoading(false)
                }
            }
        }

        var isFavorite = false
        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.check(id)
            withContext(Dispatchers.Main) {
                if (count != null) {
                    if (count > 0) {
                        binding.fab.isChecked = true
                        isFavorite = true
                    } else {
                        binding.fab.isChecked = false
                        isFavorite = false
                    }
                }
            }
        }

        binding.fab.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                viewModel.insert(id, username, avatar)
            } else {
                viewModel.delete(id)
            }
            binding.fab.isChecked = isFavorite
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_setting -> {
            Intent(this@DetailUserActivity, SettingActivity::class.java).also {
                startActivity(it)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}