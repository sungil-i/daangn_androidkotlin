package kr.sungil.daangn

import com.google.firebase.auth.FirebaseAuth

class AppConfig {
	companion object {
		// Firebase Database 값
		const val DB_POSTS = "SungilPosts"
		const val DB_USERS = "SungilUsers"
		const val DB_CHATS = "SungilChats"
		const val STORAGE_IMAGE = "SungilPostsImages"
		const val CHILD_CHAT = "chat"

		// 설정값
		const val MYDEBUG = "MY_DEBUG" // Log.d 태그값
		lateinit var AUTH: FirebaseAuth // Firebase 인증 정보

		// 시간 DiffTime
		const val ONE_SECOND: Long = 1000
		const val ONE_MINUTE: Long = ONE_SECOND * 60
		const val ONE_HOUR: Long = ONE_MINUTE * 60
		const val ONE_DAY: Long = ONE_HOUR * 24
		const val ONE_MONTH: Long = ONE_DAY * 30
		const val ONE_YEAR: Long = 31557600000

		// Chat ViewHolder
		const val LEFT_BIASED_CHAT = 1
		const val RIGHT_BIASED_CHAT = 2
	}
}