package kr.sungil.daangn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kr.sungil.daangn.chat.ChatFragment
import kr.sungil.daangn.databinding.ActivityMainBinding
import kr.sungil.daangn.home.HomeFragment
import kr.sungil.daangn.mypage.MypageFragment

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val homeFragment = HomeFragment()
		val chatFragment = ChatFragment()
		val mypageFragment = MypageFragment()

		replaceFragment(homeFragment)

		binding.btnvMenu.setOnItemSelectedListener {
			when (it.itemId) {
				R.id.home -> {
					replaceFragment(homeFragment)
				}
				R.id.chat -> {
					replaceFragment(chatFragment)
				}
				R.id.mypage -> {
					replaceFragment(mypageFragment)
				}
			}
			true
		}
	}

	private fun replaceFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.fl_container, fragment)
			.commit()
	}
}