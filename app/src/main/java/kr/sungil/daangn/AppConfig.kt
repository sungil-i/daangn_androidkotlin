package kr.sungil.daangn

import com.google.firebase.auth.FirebaseAuth

class AppConfig {
	companion object {
		// Firebase Database 값
		const val DB_ARTICLES = "Articles"
		const val DB_USERS = "Users"
		const val DB_CHATS = "Chats"
		const val CHILD_CHAT = "chat"
		const val MYDEBUG = "MY_DEBUG" // Log.d 태그값
		lateinit var AUTH: FirebaseAuth // Firebase 인증 정보
	}
}