package kr.sungil.daangn

import com.google.firebase.auth.FirebaseAuth

class AppConfig {
	companion object {
		const val DB_ARTICLES = "Articles"
		const val DB_USERS = "Users"
		const val DB_CHATS = "Chats"
		const val CHILD_CHAT = "chat"
		const val MYDEBUG = "MY_DEBUG"
		public lateinit var AUTH: FirebaseAuth
	}
}