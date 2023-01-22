package kr.sungil.daangn.models

import com.google.firebase.database.Exclude

data class UserModel(
	var email: String?,
	var name: String?,
	var nickname: String?,
) {

	// Firebase 연결을 위해 생성자가 필요합니다.
	constructor() : this("", "", "")

	// 데이터 수정 Update 를 위해 Json 형태의 Map 으로 변환합니다.
	@Exclude
	fun toMap(): Map<String, Any?> {
		return mapOf(
			"email" to email,
			"name" to name,
			"nickname" to nickname,
		)
	}
}