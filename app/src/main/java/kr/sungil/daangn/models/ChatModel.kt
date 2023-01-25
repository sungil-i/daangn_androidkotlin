package kr.sungil.daangn.models

data class ChatModel(
	var idx: String,
	var chatRoomId: String,
	var senderId: String,
	var email: String,
	var nickName: String,
	var message: String,
	var viewType: Int,
	var createAt: Long
) {
	constructor() : this("", "", "", "", "", "", 0, 0)
}