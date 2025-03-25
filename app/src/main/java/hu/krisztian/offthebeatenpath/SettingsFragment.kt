package hu.krisztian.offthebeatenpath


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        setupClickListeners(view)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutTextView: TextView = view.findViewById(R.id.textViewLogout)
        logoutTextView.setOnClickListener {
            onLogoutClick()
        }
    }

    private fun onLogoutClick() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("user_id")
            apply()
        }

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)

        requireActivity().finish()
    }
}




