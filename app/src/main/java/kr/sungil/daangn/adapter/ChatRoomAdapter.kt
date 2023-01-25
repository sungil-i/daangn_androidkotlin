package kr.sungil.daangn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.sungil.daangn.databinding.RvItemChatroomBinding
import kr.sungil.daangn.models.ChatRoomModel
import kr.sungil.daangn.models.PostModel

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