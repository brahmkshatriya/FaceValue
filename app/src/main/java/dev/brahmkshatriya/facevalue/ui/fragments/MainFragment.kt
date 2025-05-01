package dev.brahmkshatriya.facevalue.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.tabs.TabLayout
import dev.brahmkshatriya.facevalue.databinding.FragmentMainBinding
import dev.brahmkshatriya.facevalue.ui.ImageViewModel
import dev.brahmkshatriya.facevalue.ui.adapters.SmallImageAdapter
import dev.brahmkshatriya.facevalue.ui.adapters.SmallImageAdapter.Companion.applyGridLayoutManager
import dev.brahmkshatriya.facevalue.utils.AutoClearedValue.Companion.autoCleared
import dev.brahmkshatriya.facevalue.utils.ContextUtils.observe
import dev.brahmkshatriya.facevalue.utils.ContextUtils.setupTransitions
import dev.brahmkshatriya.facevalue.utils.UiUtils.configure
import dev.brahmkshatriya.facevalue.utils.UiUtils.dpToPx
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MainFragment : Fragment() {

    private var binding by autoCleared<FragmentMainBinding>()
    private val viewModel by activityViewModel<ImageViewModel>()
    private val adapter by lazy {
        SmallImageAdapter {
            viewModel.currentImageIndex.value = it
            parentFragmentManager.commit {
                add<ImageViewFragment>(id)
                hide(this@MainFragment)
                addToBackStack(null)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTransitions()
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            val system = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val padding = 4.dpToPx(v.context)
            v.updatePadding(bottom = padding + system.bottom)
            insets
        }

        binding.swipeRefresh.configure { viewModel.refresh(true) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.applyGridLayoutManager { adapter.submitList(adapter.currentList) }

        observe(viewModel.images) { state ->
            binding.swipeRefresh.isRefreshing = state is ImageViewModel.State.Loading
            val items = if (state is ImageViewModel.State.Final) state.images else null
            adapter.submitList(items.orEmpty())
        }

        val tabListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val repo = viewModel.imageRepository[tab.position]
                if (viewModel.selectedRepo.value == repo.name) return
                viewModel.refresh(true, repo.name)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        }
        binding.tabLayout.apply {
            viewModel.imageRepository.forEach {
                addTab(newTab().apply { text = it.name })
            }
            addOnTabSelectedListener(tabListener)
            val names = viewModel.imageRepository.map { it.name }
            observe(viewModel.selectedRepo) {
                val tab = binding.tabLayout.getTabAt(names.indexOf(it))
                binding.tabLayout.selectTab(tab)
            }
        }
    }
}