package kr.sungil.daangn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.sungil.daangn.AppConfig.Companion.LEFT_BIASED_CHAT
import kr.sungil.daangn.AppConfig.Companion.RIGHT_BIASED_CHAT
import kr.sungil.daangn.databinding.RvItemLeftBiasedChatBinding
import kr.sungil.daangn.databinding.RvItemRightBiasedChatBinding
import kr.sungil.daangn.models.ChatModel
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<ChatModel, RecyclerView.ViewHolder>(diffUtil) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		if (viewType == LEFT_BIASED_CHAT) {
			return ViewHolderLeftBiased(
				RvItemLeftBiasedChatBinding.inflate(
					LayoutInflater.from(parent.context),
					parent,
					false
				)
			)
		} else {
			return ViewHolderRightBiased(
				RvItemRightBiasedChatBinding.inflate(
					LayoutInflater.from(parent.context),
					parent,
					false
				)
			)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (currentList[position].viewType) {
			LEFT_BIASED_CHAT -> {
				(holder as ViewHolderLeftBiased).bind(currentList[position])
			}
			RIGHT_BIASED_CHAT -> {
				(holder as ViewHolderRightBiased).bind(currentList[position])
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		return currentList[position].viewType!!
	}

	// inner class Left Biased View Holder
	inner class ViewHolderLeftBiased(
		private val binding: RvItemLeftBiasedChatBinding
	) : RecyclerView.ViewHolder(binding.root) {
		fun bind(chatModel: ChatModel) {
			binding.apply {
				val dateFormat = SimpleDateFormat("yyyy. M. d.\na hh:mm", Locale.KOREA)
				val date = Date(chatModel.createAt!!)

				tvChatNickname.text = chatModel.nickName
				tvChatEmail.text = chatModel.email
				tvChatMessage.text = chatModel.message
				tvChatTimestamp.text = "${dateFormat.format(date)}"
			}
		}
	}

	// inner class Right Biased View Holder
	inner class ViewHolderRightBiased(
		private val binding: RvItemRightBiasedChatBinding
	) : RecyclerView.ViewHolder(binding.root) {
		fun bind(chatModel: ChatModel) {
			binding.apply {
				val dateFormat = SimpleDateFormat("yyyy. M. d.\na hh:mm", Locale.KOREA)
				val date = Date(chatModel.createAt!!)

				tvChatMessage.text = chatModel.message
				tvChatTimestamp.text = "${dateFormat.format(date)}"
			}
		}
	}

	// DiffUtil
	companion object {
		val diffUtil = object : DiffUtil.ItemCallback<ChatModel>() {
			override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
				return oldItem.idx == newItem.idx
			}

			override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
				return oldItem == newItem
			}
		}
	}
}