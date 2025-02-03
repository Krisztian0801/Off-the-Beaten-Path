package hu.krisztian.offthebeatenpath

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import hu.krisztian.offthebeatenpath.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var materialToolbar: MaterialToolbar


    private var settingsString = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var homeString = resources.getString(R.string.home)
        var mapString = resources.getString(R.string.map)
        var profileString = resources.getString(R.string.profile)
        replaceFragment(HomeFragment(), homeString)
        binding.footer.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment(), homeString)
                R.id.map -> replaceFragment(MapFragment(), mapString)

                R.id.profile -> replaceFragment(ProfileFragment(), profileString)

                else -> {

                }
            }
            true
        }
        val callback = object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menuInflater.inflate(R.menu.top_bar, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                settingsString = resources.getString(R.string.settings)
                return when (item?.itemId) {
                    R.id.settings -> {
                        replaceFragment(SettingsFragment(), settingsString)
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        }
        val actionMode = startSupportActionMode(callback)
    }


    private fun replaceFragment(fragment: Fragment, screen: String) {
        materialToolbar = findViewById(R.id.topAppBar)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        materialToolbar.setTitle(screen)
        fragmentTransaction.commit()

    }
}