package kr.sungil.daangn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.sungil.daangn.AppConfig.Companion.ONE_DAY
import kr.sungil.daangn.AppConfig.Companion.ONE_HOUR
import kr.sungil.daangn.AppConfig.Companion.ONE_MINUTE
import kr.sungil.daangn.AppConfig.Companion.ONE_MONTH
import kr.sungil.daangn.AppConfig.Companion.ONE_YEAR
import kr.sungil.daangn.databinding.RvItemPostBinding
import kr.sungil.daangn.models.PostModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
			val diffTime: Long = System.currentTimeMillis() - postModel.createdAt
			val priceFormat = NumberFormat.getInstance()

			binding.apply {
				tvTitle.text = postModel.title
				tvDuration.text = getDiffTime(diffTime)
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
		var strDiffTime = ""
		if(diffTime >= ONE_YEAR) {
			val dr = (diffTime / ONE_YEAR).toInt()
			strDiffTime = "${dr}년 전"
		}else if (diffTime >= ONE_MONTH) {
			val dr = (TimeUnit.MILLISECONDS.toDays(diffTime) / 30).toInt()
			strDiffTime = "${dr}달 전"
		} else if (diffTime >= ONE_DAY) {
			val dr = TimeUnit.MILLISECONDS.toDays(diffTime)
			strDiffTime = "${dr}일 전"
		} else if (diffTime >= ONE_HOUR) {
			val dr = TimeUnit.MILLISECONDS.toHours(diffTime)
			strDiffTime = "${dr}시간 전"
		} else if (diffTime >= ONE_MINUTE) {
			val dr = TimeUnit.MILLISECONDS.toMinutes(diffTime)
			strDiffTime = "${dr}분 전"
		} else {
			val dr = TimeUnit.MILLISECONDS.toSeconds(diffTime)
			strDiffTime = "${dr}초 전"
		}
		return strDiffTime
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