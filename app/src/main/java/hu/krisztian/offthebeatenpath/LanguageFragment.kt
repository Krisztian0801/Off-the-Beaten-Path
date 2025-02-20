package hu.krisztian.offthebeatenpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import hu.krisztian.offthebeatenpath.helpers.LocaleHelper

class LanguageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_language, container, false)

        val btnEnglish: Button = view.findViewById(R.id.btn_english)
        val btnHungarian: Button = view.findViewById(R.id.btn_hungarian)

        btnEnglish.setOnClickListener {
            LocaleHelper.setLocale(requireActivity(), "en")
            requireActivity().recreate()
        }

        btnHungarian.setOnClickListener {
            LocaleHelper.setLocale(requireActivity(), "hu")
            requireActivity().recreate()
        }

        return view
    }
}
