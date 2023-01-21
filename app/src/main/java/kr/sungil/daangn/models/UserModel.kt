package kr.sungil.daangn.models

import com.google.firebase.database.Exclude

data class UserModel(
	var email: String?,
	var name: String?,
	var nickname: String?,
) {
	@Exclude
	fun toMap(): Map<String, Any?> {
		return mapOf(
			"email" to email,
			"name" to name,
			"nickname" to nickname,
		)
	}
}