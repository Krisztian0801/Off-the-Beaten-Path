package hu.krisztian.offthebeatenpath

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton

class SettingsFragment : Fragment() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        setupClickListeners(view)
        loadSettings(view)
        return view
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<TextView>(R.id.textViewAccount).setOnClickListener {
            replaceFragment(AccountFragment())
        }
        view.findViewById<TextView>(R.id.textViewLanguage).setOnClickListener {
            replaceFragment(LanguageFragment())
        }
        view.findViewById<TextView>(R.id.textViewAbout).setOnClickListener {
            replaceFragment(AboutFragment())
        }
        view.findViewById<TextView>(R.id.textViewLegal).setOnClickListener {
            replaceFragment(LegalFragment())
        }

//        view.findViewById<MaterialButton>(R.id.buttonChangeProfilePicture).setOnClickListener {
//            Log.INFO
//        }
//        view.findViewById<MaterialButton>(R.id.buttonChangePassword).setOnClickListener {
//            Log.INFO
//        }
//        view.findViewById<MaterialButton>(R.id.buttonChangeLanguage).setOnClickListener {
//            Log.INFO
//        }
//        view.findViewById<MaterialButton>(R.id.buttonPrivacySettings).setOnClickListener {
//            Log.INFO
//        }
//        view.findViewById<MaterialButton>(R.id.buttonDeleteAccount).setOnClickListener {
//            Log.INFO
//        }

//        editTextName = view.findViewById(R.id.editTextName)
//        editTextEmail = view.findViewById(R.id.editTextEmail)
//
//        editTextName.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) saveSettings("name", editTextName.text.toString())
//        }
//        editTextEmail.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) saveSettings("email", editTextEmail.text.toString())
//        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun onAccountClick() {
        replaceFragment(AccountFragment())
    }

    private fun onLanguageClick() {
        replaceFragment(LanguageFragment())
    }

    private fun onAboutClick() {
        replaceFragment(AboutFragment())
    }

    private fun onLegalClick() {
        replaceFragment(LegalFragment())
    }


    private fun saveSettings(key: String, value: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun loadSettings(view: View) {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
//        view.findViewById<TextInputEditText>(R.id.editTextName).setText(sharedPreferences.getString("name", ""))
//         view.findViewById<TextInputEditText>(R.id.editTextEmail).setText(sharedPreferences.getString("email", ""))
    }

}
