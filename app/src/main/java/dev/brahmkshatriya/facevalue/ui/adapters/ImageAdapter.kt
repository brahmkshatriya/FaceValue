package dev.brahmkshatriya.facevalue.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.brahmkshatriya.facevalue.databinding.ItemImageWithOverlaysBinding
import dev.brahmkshatriya.facevalue.databinding.ItemOverlayBinding
import dev.brahmkshatriya.facevalue.models.Image
import dev.brahmkshatriya.facevalue.utils.ImageUtils.loadInto

class ImageAdapter(
    val listener: Listener
) : ListAdapter<Image, ImageAdapter.ViewHolder>(Image.DiffCallback) {

    interface Listener {
        fun onImageSelected(image: Image)
        fun onDetectedFaceSelected(image: Image, index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemImageWithOverlaysBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemImageWithOverlaysBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = getItem(bindingAdapterPosition) ?: return@setOnClickListener
                listener.onImageSelected(item)
            }
        }

        fun bind(image: Image) {
            binding.progress.isVisible = image.faces == null
            image.holder.loadInto(binding.imageView) {
                val bitmap = it?.toBitmap() ?: return@loadInto
                binding.imageView.setImageBitmap(bitmap)
                binding.overlay.removeAllViews()
                if (image.faces == null) return@loadInto
                val width = bitmap.width
                val height = bitmap.height
                binding.overlay.doOnLayout { view ->
                    val scale = if (width > height) binding.overlay.width.toFloat() / width
                    else binding.overlay.height.toFloat() / height
                    val leftExtra = ((binding.overlay.width - width * scale) / 2).toInt()
                    val topExtra = ((binding.overlay.height - height * scale) / 2).toInt()
                    view.post {
                        image.faces.forEachIndexed { index, _ ->
                            addOverlay(image, index, scale, leftExtra, topExtra, binding.overlay)
                        }
                    }
                }
            }
        }
    }

    fun addOverlay(
        image: Image,
        index: Int,
        scale: Float,
        leftExtra: Int,
        topExtra: Int,
        parent: ViewGroup
    ) {
        println("burh")
        val pair = image.faces?.get(index) ?: return
        println("$index $leftExtra $topExtra $scale")
        val detectedFace = pair.first

        val left = (detectedFace.x * scale) + leftExtra
        val top = (detectedFace.y * scale) + topExtra
        println("x,y $left $top")

        val faceWidth = (detectedFace.width * scale).toInt()
        val faceHeight = (detectedFace.height * scale).toInt()

        val face = pair.second

        val overlay = ItemOverlayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        overlay.card.updateLayoutParams {
            width = faceWidth
            height = faceHeight
        }
        overlay.root.translationX = left
        overlay.root.translationY = top
        overlay.root.setOnClickListener {
            listener.onDetectedFaceSelected(image, index)
        }
        overlay.text.updateLayoutParams {
            width = faceWidth
        }
        overlay.text.text = face?.name ?: "???"
        parent.addView(overlay.root)
    }
}