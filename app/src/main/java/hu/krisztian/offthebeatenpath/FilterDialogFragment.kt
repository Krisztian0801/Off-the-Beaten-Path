package hu.krisztian.offthebeatenpath

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import hu.krisztian.offthebeatenpath.databinding.DialogFilterPoiBinding
import hu.krisztian.offthebeatenpath.network.DropdownHelper
import kotlinx.coroutines.launch

class FilterDialogFragment(
    private val onApply: (categoryId: Int?, landmarkId: Int?) -> Unit
) : DialogFragment() {

    private var _binding: DialogFilterPoiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFilterPoiBinding.inflate(inflater, container, false)

        // Populate dropdowns using your helper
        DropdownHelper.populateCategories(requireContext(), binding.categoryDropdown)
        DropdownHelper.populateLandmarks(requireContext(), binding.landmarkDropdown)

        binding.filterPlacesButton.setOnClickListener {
            val selectedCategory = binding.categoryDropdown.text.toString()
            val selectedLandmark = binding.landmarkDropdown.text.toString()

            lifecycleScope.launch {
                val categoryId = if (selectedCategory.isNotBlank()) DropdownHelper.getCategoryId(selectedCategory).takeIf { it > 0 } else null
                val landmarkId = if (selectedLandmark.isNotBlank()) DropdownHelper.getLandmarkId(selectedLandmark).takeIf { it > 0 } else null
                onApply(categoryId, landmarkId)
                dismiss()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
