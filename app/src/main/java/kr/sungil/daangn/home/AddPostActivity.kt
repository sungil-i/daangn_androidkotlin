package kr.sungil.daangn.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.ActivityAddPostBinding

class AddPostActivity : AppCompatActivity() {
	private lateinit var binding: ActivityAddPostBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityAddPostBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}
}