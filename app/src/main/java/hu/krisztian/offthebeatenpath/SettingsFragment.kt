package hu.krisztian.offthebeatenpath


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

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



}
