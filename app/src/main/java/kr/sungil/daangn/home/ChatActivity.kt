package kr.sungil.daangn.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
	private lateinit var binding: ActivityChatBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityChatBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}
}