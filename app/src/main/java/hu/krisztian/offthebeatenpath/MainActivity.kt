package hu.krisztian.offthebeatenpath

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val homeString = resources.getString(R.string.home)
        val mapString = resources.getString(R.string.map)
        val profileString = resources.getString(R.string.profile)
        val settingsString = resources.getString(R.string.settings)
        materialToolbar = findViewById(R.id.topAppBar)
        replaceFragment(HomeFragment(), homeString)
        if (isDarkMode()) {
            materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_white)
        } else {
            materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_black)
        }
        materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    replaceFragment(SettingsFragment(), settingsString)
                    menuItem.setIcon(R.drawable.setting_filled)
                    resetBottomNavigationViewIcons()
                    true
                }
                else -> false
            }
        }

        binding.footer.setOnItemSelectedListener { menuItem ->
            resetBottomNavigationViewIcons() // Reset icons before handling any new selection
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment(), homeString)
                    menuItem.setIcon(R.drawable.home_filled)
                    setBottomNavigationViewIconColor(menuItem.itemId, R.color.icon_tint_selected)
                    if (isDarkMode()) {
                        materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_white)
                    } else {
                        materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_black)
                    }
                    true
                }
                R.id.map -> {
                    replaceFragment(MapFragment(), mapString)
                    menuItem.setIcon(R.drawable.map_filled)
                    setBottomNavigationViewIconColor(menuItem.itemId, R.color.icon_tint_selected)
                    if (isDarkMode()) {
                        materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_white)
                    } else {
                        materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_black)
                    }

                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment(), profileString)
                    menuItem.setIcon(R.drawable.profile_filled)
                    setBottomNavigationViewIconColor(menuItem.itemId, R.color.icon_tint_selected)
                    if (isDarkMode()) {
                        materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_white)
                    } else {
                        materialToolbar.menu.findItem(R.id.settings).setIcon(R.drawable.settings_black)
                    }
                    true
                }
                else -> false
            }
        }
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

    private fun setBottomNavigationViewIconColor(itemId: Int, color: Int) {
        val selectedColor = ContextCompat.getColorStateList(this, color)
        binding.footer.menu.findItem(itemId).iconTintList = selectedColor
    }
    private fun isDarkMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun replaceFragment(fragment: Fragment, screen: String) {
        materialToolbar = findViewById(R.id.topAppBar)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        materialToolbar.title = screen
        fragmentTransaction.commit()
    }
}
