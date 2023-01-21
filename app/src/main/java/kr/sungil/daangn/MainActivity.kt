package kr.sungil.daangn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
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

		// Firebase AUTH 초기화
		AUTH = FirebaseAuth.getInstance()

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

	override fun onStart() {
		super.onStart()

		// Reload 될때마다 로그인을 체크한다.
		if (AUTH.currentUser == null) {
			Toast.makeText(
				this,
				getString(R.string.check_login),
				Toast.LENGTH_LONG
			).show()
			startActivity(Intent(this, LoginActivity::class.java))
		}
	}

	private fun replaceFragment(fragment: Fragment) {
		// 메뉴 Fragment 를 변경하는 함수
		supportFragmentManager.beginTransaction()
			.replace(R.id.fl_container, fragment)
			.commit()
	}
}