package kr.sungil.daangn.models

data class ChatRoomModel(
	val idx: String,
	val buyerId: String,
	val sellerId: String,
	val postId: String,
	val createdAt: Long
) {
	constructor() : this("", "", "", "", 0)
}