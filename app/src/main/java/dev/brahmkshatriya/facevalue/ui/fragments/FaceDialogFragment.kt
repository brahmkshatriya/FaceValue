package dev.brahmkshatriya.facevalue.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.brahmkshatriya.facevalue.R
import dev.brahmkshatriya.facevalue.databinding.DialogFaceBinding
import dev.brahmkshatriya.facevalue.models.Face
import dev.brahmkshatriya.facevalue.ui.ImageViewModel
import dev.brahmkshatriya.facevalue.ui.adapters.FaceAdapter
import dev.brahmkshatriya.facevalue.utils.AutoClearedValue.Companion.autoCleared
import dev.brahmkshatriya.facevalue.utils.ContextUtils.observe
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FaceDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(detectedFaceId: Long): FaceDialogFragment {
            return FaceDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong("detectedFaceId", detectedFaceId)
                }
            }
        }
    }

    private val args by lazy { requireArguments() }
    private val detectedFaceId by lazy { args.getLong("detectedFaceId") }

    private var binding by autoCleared<DialogFaceBinding>()
    private val viewModel by activityViewModel<ImageViewModel>()

    private val adapter by lazy {
        FaceAdapter(
            object : FaceAdapter.Listener {
                override fun onAdd() {
                    editFace(null)
                }

                override fun onClose() {
                    dismiss()
                }

                override fun onEdit(face: Face) {
                    editFace(face)
                }

                override fun onDelete(face: Face) {
                    viewModel.deleteFace(face)
                }

                override fun onSelected(face: Face) {
                    viewModel.selectFace(detectedFaceId, face)
                    dismiss()
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogFaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter.withHeader()
        observe(viewModel.faces) { list ->
            val selectedId = viewModel.getDetectedFace(detectedFaceId)?.faceId
            adapter.submitList(list.map { (it.id == selectedId) to it })
        }
    }

    fun editFace(face: Face?) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.item_edit_text)
            .setPositiveButton(R.string.okay, null)
            .setNegativeButton(R.string.cancel, null)
            .setTitle(if (face == null) R.string.add_face else R.string.edit_face)
            .create()

        dialog.setOnShowListener {
            val editText = dialog.findViewById<EditText>(R.id.edit_text)!!
            editText.setText(face?.name)
            editText.setHint(R.string.name)
            editText.doOnTextChanged { _, _, _, _ -> editText.error = null }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newText = editText.text.toString()
                if (newText.isEmpty()) {
                    editText.error = getString(R.string.name_required)
                    return@setOnClickListener
                }
                if (face == null) {
                    viewModel.addFace(newText)
                } else {
                    viewModel.updateFace(face.copy(name = newText))
                }
                dialog.dismiss()
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}