package kr.sungil.daangn.models


data class ChatModel(
	val idx: String = "",
	val chatRoomId: String = "",
	val senderId: String = "",
	var email: String = "",
	var nickName: String = "",
	var message: String = "",
	var viewType: Int = 0,
	val createAt: Long = 0
) {
	constructor() : this("", "", "", "", "", "", 0, 0)
}