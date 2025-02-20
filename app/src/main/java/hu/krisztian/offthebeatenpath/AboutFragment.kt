package hu.krisztian.offthebeatenpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val versionTextView: TextView = view.findViewById(R.id.versionTextView)
        val developerTextView: TextView = view.findViewById(R.id.developerTextView)

        val versionString = getString(R.string.version)
        val versionName = BuildConfig.VERSION_NAME
        val versionText = getString(R.string.app_version_format, versionName)
        val developerString = getString(R.string.developer)
        val developerName = "Bordács Krisztián"

        val versionFullText = "$versionString\n$versionText"
        val developerFullText = "$developerString\n$developerName"

        // Create SpannableStrings for version and developer sections
        val versionSpannableString = SpannableString(versionFullText)
        val developerSpannableString = SpannableString(developerFullText)

        // Apply styles to the version section
        val versionFirstLineEndIndex = versionString.length
        versionSpannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), android.R.color.black)),
            0,
            versionFirstLineEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        versionSpannableString.setSpan(
            RelativeSizeSpan(1.2f),
            0,
            versionFirstLineEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val versionSecondLineStartIndex = versionFirstLineEndIndex + 1
        val versionSecondLineEndIndex = versionSecondLineStartIndex + versionText.length
        versionSpannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.darker_gray
                )
            ),
            versionSecondLineStartIndex,
            versionSecondLineEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        versionSpannableString.setSpan(
            RelativeSizeSpan(0.8f),
            versionSecondLineStartIndex,
            versionSecondLineEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply styles to the developer section
        val developerFirstLineEndIndex = developerString.length
        developerSpannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), android.R.color.black)),
            0,
            developerFirstLineEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        developerSpannableString.setSpan(
            RelativeSizeSpan(1.2f),
            0,
            developerFirstLineEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val developerSecondLineStartIndex = developerFirstLineEndIndex + 1
        developerSpannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.darker_gray
                )
            ),
            developerSecondLineStartIndex,
            developerFullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        developerSpannableString.setSpan(
            RelativeSizeSpan(0.8f),
            developerSecondLineStartIndex,
            developerFullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        versionTextView.text = versionSpannableString
        developerTextView.text = developerSpannableString

        return view
    }
}
