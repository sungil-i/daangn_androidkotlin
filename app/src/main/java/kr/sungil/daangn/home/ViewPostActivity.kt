package kr.sungil.daangn.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.ActivityViewPostBinding
import kr.sungil.daangn.models.PostModel

class ViewPostActivity : AppCompatActivity() {
	private lateinit var binding: ActivityViewPostBinding
	private lateinit var postModel: PostModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityViewPostBinding.inflate(layoutInflater)
		setContentView(binding.root)

		if(intent.hasExtra("idx").not()) finish()
		val idx = intent.getStringExtra("idx") ?: ""
		if(idx.isEmpty()) finish()

		initCancelButton()
		initViews()
	}

	private fun initViews() {
	}

	private fun initCancelButton() {
		// 취소 버튼 이벤트
		binding.ivClose.setOnClickListener {
			finish()
		}
	}

	override fun onStart() {
		super.onStart()

		// 로그인이 되어 있는지 체크합니다
		if (AUTH.currentUser == null) finish()
	}
}