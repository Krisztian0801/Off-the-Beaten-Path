package hu.krisztian.offthebeatenpath

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class LegalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_legal, container, false)
        val listView: ListView = view.findViewById(R.id.legalListView)

        val items = arrayOf("Terms and Conditions", "Privacy Policy", "Copyright")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val url = when (position) {
                0 -> "https://drive.google.com/file/d/1EFEgVRr1INZ93KPefOLMIGw3issQfU5Z/view" // Terms and Conditions
                1 -> "https://drive.google.com/file/d/1XZWJ6GgoNPspwAy9SNGSQhZka4C5V7MI/view" // Privacy Policy
                2 -> "https://drive.google.com/file/d/10t56U2YpdmWDQk-EORSrXpg-AXBKkYb8/view" // Copyright
                else -> null
            }

            url?.let {
                val intent = Intent(requireContext(), WebViewActivity::class.java)
                intent.putExtra("pdf_url", it)
                startActivity(intent)
            }
        }

        return view
    }
}
