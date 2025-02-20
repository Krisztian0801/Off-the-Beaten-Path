package hu.krisztian.offthebeatenpath

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AccountFragment : Fragment() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        loadSettings(view)

        view.findViewById<MaterialButton>(R.id.buttonChangeProfilePicture).setOnClickListener {

        }
        view.findViewById<MaterialButton>(R.id.buttonChangePassword).setOnClickListener {

        }
        view.findViewById<MaterialButton>(R.id.buttonDeleteAccount).setOnClickListener {

        }


        editTextName = view.findViewById(R.id.editTextName)
        editTextEmail = view.findViewById(R.id.editTextEmail)

        editTextName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveSettings("name", editTextName.text.toString())
        }
        editTextEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveSettings("email", editTextEmail.text.toString())
        }

        return view
    }

    private fun saveSettings(key: String, value: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun loadSettings(view: View) {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        view.findViewById<TextInputEditText>(R.id.editTextName).setText(sharedPreferences.getString("name", ""))
        view.findViewById<TextInputEditText>(R.id.editTextEmail).setText(sharedPreferences.getString("email", ""))
    }
}
