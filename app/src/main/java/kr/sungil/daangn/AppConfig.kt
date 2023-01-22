package kr.sungil.daangn

import com.google.firebase.auth.FirebaseAuth

class AppConfig {
	companion object {
		// Firebase Database 값
		const val DB_POSTS= "SungilPosts"
		const val DB_USERS = "SungilUsers"
		const val DB_CHATS = "SungilChats"
		const val CHILD_CHAT = "chat"
		// 설정값
		const val MYDEBUG = "MY_DEBUG" // Log.d 태그값
		lateinit var AUTH: FirebaseAuth // Firebase 인증 정보
	}
}