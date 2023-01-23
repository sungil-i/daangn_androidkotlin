package kr.sungil.daangn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.sungil.daangn.databinding.RvItemPostBinding
import kr.sungil.daangn.models.PostModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
	val onItemClicked: (PostModel) -> Unit
) : ListAdapter<PostModel, PostAdapter.ViewHolder>(diffUtil) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(
			RvItemPostBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(currentList[position])
	}

	// inner class
	inner class ViewHolder(
		private val binding: RvItemPostBinding
	) : RecyclerView.ViewHolder(binding.root) {
		fun bind(postModel: PostModel) {
			val dateFormat = SimpleDateFormat("yyyy. M. d.")
			val date = Date(postModel.createdAt)
			val diffTime = System.currentTimeMillis() - postModel.createdAt
			val priceFormat = NumberFormat.getInstance()

			binding.apply {
				tvTitle.text = postModel.title
				tvDuration.text = diffTime.toString()
				tvDate.text = getDiffTime(diffTime)
				tvDate.text = dateFormat.format(date).toString()
				tvPrice.text = priceFormat.format(postModel.price).toString()

				if (postModel.imageUrl.isNotEmpty()) {
					Glide.with(ivProduct)
						.load(postModel.imageUrl)
						.into(ivProduct)
				}

				root.setOnClickListener {
					onItemClicked(postModel)
				}
			}
		}
	}

	private fun getDiffTime(diffTime: Long): String {
		return ""
	}

	// diffUtil
	companion object {
		val diffUtil = object : DiffUtil.ItemCallback<PostModel>() {
			override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
				return oldItem.idx == newItem.idx
			}

			override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
				return oldItem == newItem
			}
		}
	}
}