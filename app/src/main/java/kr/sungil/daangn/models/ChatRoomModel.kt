package kr.sungil.daangn.models

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
}