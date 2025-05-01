package dev.brahmkshatriya.facevalue.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.brahmkshatriya.facevalue.R
import dev.brahmkshatriya.facevalue.databinding.ItemSmallImageBinding
import dev.brahmkshatriya.facevalue.models.Image
import dev.brahmkshatriya.facevalue.utils.ImageUtils.loadInto
import dev.brahmkshatriya.facevalue.utils.UiUtils.dpToPx
import kotlin.math.roundToInt

class SmallImageAdapter(
    val listener: Listener
) : ListAdapter<Image, SmallImageAdapter.ViewHolder>(Image.DiffCallback) {

    fun interface Listener {
        fun onImageSelected(index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSmallImageBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemSmallImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onImageSelected(bindingAdapterPosition)
            }
        }

        fun bind(image: Image) {
            binding.progressCard.isVisible = if (image.faces == null) true
            else if (image.faces.isEmpty()) false else true

            binding.progress.isVisible = image.faces == null
            binding.facesCount.isVisible = image.faces != null
            binding.facesCount.text = image.faces?.size?.toString()

            image.holder.loadInto(binding.imageView, null, R.drawable.ic_launcher_foreground)
        }
    }

    companion object {
        private fun View.getCount(horizontalPadding: Int = 4 * 2) = run {
            val itemWidth = 128.dpToPx(context)
            val newWidth = width - horizontalPadding.dpToPx(context)
            (newWidth.toFloat() / (itemWidth + 24.dpToPx(context))).roundToInt()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun RecyclerView.applyGridLayoutManager(after: () -> Unit) {
            val manager = GridLayoutManager(context, 1)
            layoutManager = manager
            doOnLayout {
                manager.spanCount = getCount()
                after()
            }
        }
    }

}