package kr.sungil.daangn.models

import com.google.firebase.database.Exclude

data class ChatRoomModel(
	val idx: String = "",
	val buyerId: String = "",
	val sellerId: String = "",
	val postId: String = "",
	var createdAt: Long = 0,
	var title: String = "",
	var sellerNickname: String = "",
	var lastMessage: String = "",
	var imageUrl: String = "",
	var updatedAt: Long = 0
) {
	constructor() : this("", "", "", "", 0, "", "", "", "", 0)

	@Exclude
	fun toMap(): Map<String, Any> {
		return mapOf(
			"idx" to idx,
			"buyerId" to buyerId,
			"sellerId" to sellerId,
			"postId" to postId,
			"createdAt" to createdAt,
			"title" to title,
			"sellerNickname" to sellerNickname,
			"lastMessage" to lastMessage,
			"imageUrl" to imageUrl,
			"updatedAt" to updatedAt,
		)
	}
}