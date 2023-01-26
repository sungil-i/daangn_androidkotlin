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
import kr.sungil.daangn.databinding.RvItemChatroomBinding
import kr.sungil.daangn.models.ChatRoomModel
import kr.sungil.daangn.models.PostModel
import java.util.concurrent.TimeUnit

class ChatRoomAdapter(
	val onItemClicked: (ChatRoomModel) -> Unit
) : ListAdapter<ChatRoomModel, ChatRoomAdapter.ViewHolder>(diffUtil) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(
			RvItemChatroomBinding.inflate(
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
		private val binding: RvItemChatroomBinding
	) : RecyclerView.ViewHolder(binding.root) {
		fun bind(chatRoomModel: ChatRoomModel) {
			binding.apply {
				tvTitle.text = chatRoomModel.title
				tvNickname.text = chatRoomModel.sellerNickname
				if (chatRoomModel.updatedAt != 0L) {
					tvChat.text = "TODO 마지막 대화 출력하기"
					tvDuration.text = getDiffTime(chatRoomModel.updatedAt)
				}

				if(chatRoomModel.imageUrl.isNotEmpty()) {
					Glide.with(ivProduct.context)
						.load(chatRoomModel.imageUrl)
						.into(ivProduct)
				}
			}
		}

		private fun getDiffTime(diffTime: Long): String {
			// 몇시간 전에 올린 글인지를 알기 위해 단위에 따라 계산을 합니다.
			var strDiffTime = ""
			if (diffTime >= ONE_YEAR) {
				val dr = (diffTime / ONE_YEAR).toInt()
				strDiffTime = "${dr}년 전"
			} else if (diffTime >= ONE_MONTH) {
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
	}

	// diffUtil
	companion object {
		// RecyclerView 의 성능을 높여줍니다.
		val diffUtil = object : DiffUtil.ItemCallback<ChatRoomModel>() {
			override fun areItemsTheSame(oldItem: ChatRoomModel, newItem: ChatRoomModel): Boolean {
				return oldItem.idx == newItem.idx
			}

			override fun areContentsTheSame(oldItem: ChatRoomModel, newItem: ChatRoomModel): Boolean {
				return oldItem == newItem
			}
		}
	}
}