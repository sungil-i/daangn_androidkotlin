package kr.sungil.daangn.models

data class ChatModel(
	val idx: String,
	val chatRoomIdx: String,
	val senderId: String,
	val email: String,
	val nickName: String,
	val message: String,
	var viewType: Int,
	val createAt: Long
) {
	constructor() : this("", "", "", "", "", "", 0, 0)
}