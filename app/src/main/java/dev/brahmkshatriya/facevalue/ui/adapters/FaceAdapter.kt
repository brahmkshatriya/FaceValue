package dev.brahmkshatriya.facevalue.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.brahmkshatriya.facevalue.R
import dev.brahmkshatriya.facevalue.databinding.ItemFaceBinding
import dev.brahmkshatriya.facevalue.databinding.ItemFaceHeaderBinding
import dev.brahmkshatriya.facevalue.models.Face

class FaceAdapter(
    val listener: Listener
) : ListAdapter<Pair<Boolean, Face>, FaceAdapter.ViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Pair<Boolean, Face>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Boolean, Face>, newItem: Pair<Boolean, Face>
        ): Boolean {
            return oldItem.second.id == newItem.second.id
        }

        override fun areContentsTheSame(
            oldItem: Pair<Boolean, Face>, newItem: Pair<Boolean, Face>
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onAdd()
        fun onClose()
        fun onSelected(face: Face)
        fun onEdit(face: Face)
        fun onDelete(face: Face)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemFaceBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemFaceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = getItem(bindingAdapterPosition) ?: return@setOnClickListener
                listener.onSelected(item.second)
            }
            binding.root.setOnLongClickListener {
                val item = getItem(bindingAdapterPosition) ?: return@setOnLongClickListener false
                listener.onDelete(item.second)
                true
            }
            binding.edit.setOnClickListener {
                val item = getItem(bindingAdapterPosition) ?: return@setOnClickListener
                listener.onEdit(item.second)
            }
        }

        fun bind(face: Pair<Boolean, Face>) {
            binding.face.text = face.second.name
            if (!face.first) binding.root.background = null
            else binding.root.setBackgroundResource(R.drawable.shape_rounded_4dp)
        }
    }

    class Header(
        val listener: Listener
    ) : RecyclerView.Adapter<Header.ViewHolder>() {
        inner class ViewHolder(binding: ItemFaceHeaderBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnMenuItemClickListener {
                    listener.onAdd()
                    true
                }
                binding.root.setNavigationOnClickListener {
                    listener.onClose()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Header.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ViewHolder(ItemFaceHeaderBinding.inflate(inflater, parent, false))
        }

        override fun onBindViewHolder(holder: Header.ViewHolder, position: Int) {}

        override fun getItemCount() = 1
    }

    fun withHeader(): RecyclerView.Adapter<*> {
        return ConcatAdapter(Header(listener), this)
    }
}