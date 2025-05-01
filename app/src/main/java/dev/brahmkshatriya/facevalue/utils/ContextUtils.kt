package dev.brahmkshatriya.facevalue.utils

import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object ContextUtils {
    fun <T> LifecycleOwner.observe(flow: Flow<T>, block: suspend (T) -> Unit) =
        lifecycleScope.launch {
            flow.flowWithLifecycle(lifecycle).collectLatest(block)
        }

    fun Fragment.setupTransitions() {
        (view as? ViewGroup)?.isTransitionGroup = true
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        postponeEnterTransition()
        view?.doOnPreDraw { startPostponedEnterTransition() }
    }
}
