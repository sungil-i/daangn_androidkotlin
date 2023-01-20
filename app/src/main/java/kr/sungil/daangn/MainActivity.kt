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
		// view binding 연결
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// fragment 생성
		val homeFragment = HomeFragment()
		val chatFragment = ChatFragment()
		val mypageFragment = MypageFragment()

		// 기본 화면을 HomeFragment 로 설정
		replaceFragment(homeFragment)

		// Bottom Navigation 메뉴를 클릭 했을 때 해당 Fragment 로 연결
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
		// 메뉴 Fragment 를 변경하는 함수
		supportFragmentManager.beginTransaction()
			.replace(R.id.fl_container, fragment)
			.commit()
	}
}