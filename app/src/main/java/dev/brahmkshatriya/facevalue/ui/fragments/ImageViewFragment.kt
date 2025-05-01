package dev.brahmkshatriya.facevalue.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import dev.brahmkshatriya.facevalue.databinding.FragmentImageviewBinding
import dev.brahmkshatriya.facevalue.models.Image
import dev.brahmkshatriya.facevalue.ui.ImageViewModel
import dev.brahmkshatriya.facevalue.ui.adapters.ImageAdapter
import dev.brahmkshatriya.facevalue.utils.AutoClearedValue.Companion.autoCleared
import dev.brahmkshatriya.facevalue.utils.ContextUtils.observe
import dev.brahmkshatriya.facevalue.utils.ContextUtils.setupTransitions
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ImageViewFragment : Fragment() {

    private var binding by autoCleared<FragmentImageviewBinding>()
    private val viewModel by activityViewModel<ImageViewModel>()
    private val adapter by lazy {
        ImageAdapter(
            object : ImageAdapter.Listener {
                override fun onImageSelected(image: Image) {}
                override fun onDetectedFaceSelected(image: Image, index: Int) {
                    val detected = image.faces?.get(index)?.first ?: return
                    FaceDialogFragment.newInstance(detected.id).show(parentFragmentManager, null)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var first = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTransitions()
        binding.toolBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.viewPager.adapter = adapter
        val combined = viewModel.images.combine(viewModel.currentImageIndex) { a, b -> a to b }
        observe(combined) { (state, index) ->
            val list = if (state is ImageViewModel.State.Final) state.images else null
            adapter.submitList(list.orEmpty())
            if (first) binding.viewPager.setCurrentItem(index, false)
            first = false
            val current = list?.getOrNull(index)?.holder?.id ?: ""
            binding.toolBar.title = current
            binding.toolBar.subtitle = "${index + 1}/${list?.size ?: 0}"
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.currentImageIndex.value = position
            }
        })
    }
}