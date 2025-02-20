package hu.krisztian.offthebeatenpath

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import hu.krisztian.offthebeatenpath.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var materialToolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        materialToolbar = findViewById(R.id.topAppBar)
        replaceFragment(HomeFragment(), getString(R.string.home))
        setToolbarSettingsIcon()

        materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    replaceFragment(SettingsFragment(), getString(R.string.settings))
                    menuItem.setIcon(R.drawable.setting_filled)
                    resetBottomNavigationViewIcons()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.footer.setOnItemSelectedListener { menuItem ->
            resetBottomNavigationViewIcons()
            val fragment = when (menuItem.itemId) {
                R.id.home -> HomeFragment().also { setBottomNavigationViewIcon(menuItem, R.drawable.home_filled) }
                R.id.map -> MapFragment().also { setBottomNavigationViewIcon(menuItem, R.drawable.map_filled) }
                R.id.profile -> ProfileFragment().also { setBottomNavigationViewIcon(menuItem, R.drawable.profile_filled) }
                else -> null
            }
            fragment?.let {
                replaceFragment(it, menuItem.title.toString())
            }
            setToolbarSettingsIcon()
            fragment != null
        }
    }

    private fun setToolbarSettingsIcon() {
        val settingsIcon = if (isDarkMode()) R.drawable.settings_white else R.drawable.settings_black
        materialToolbar.menu.findItem(R.id.settings).setIcon(settingsIcon)
    }

    private fun resetBottomNavigationViewIcons() {
        val defaultColor = ContextCompat.getColorStateList(this, R.color.icon_tint)
        binding.footer.menu.findItem(R.id.home).setIcon(R.drawable.home_black)
        binding.footer.menu.findItem(R.id.map).setIcon(R.drawable.map_black)
        binding.footer.menu.findItem(R.id.profile).setIcon(R.drawable.profile_black)
        binding.footer.menu.findItem(R.id.home).iconTintList = defaultColor
        binding.footer.menu.findItem(R.id.map).iconTintList = defaultColor
        binding.footer.menu.findItem(R.id.profile).iconTintList = defaultColor
    }

    private fun setBottomNavigationViewIcon(menuItem: MenuItem, iconResId: Int) {
        menuItem.setIcon(iconResId)
        menuItem.iconTintList = ContextCompat.getColorStateList(this, R.color.icon_tint_selected)
    }

    private fun isDarkMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun replaceFragment(fragment: Fragment, screen: String) {
        materialToolbar = findViewById(R.id.topAppBar)
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
        materialToolbar.title = screen
    }
}
