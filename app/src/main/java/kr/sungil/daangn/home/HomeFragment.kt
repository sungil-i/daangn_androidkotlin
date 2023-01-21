package kr.sungil.daangn.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.LoginActivity
import kr.sungil.daangn.LogoutActivity
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
	private var binding: FragmentHomeBinding? = null
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Fragment 기본함수: Activity 의 onCreate 함수와 동일함
		super.onViewCreated(view, savedInstanceState)
		val _binding = FragmentHomeBinding.bind(view)
		binding = _binding

		initLogInOutButton()
	}

	private fun initLogInOutButton() {
		binding!!.apply {
			// 로그인 버튼 이벤트
			ivLogin.setOnClickListener {
				startActivity(Intent(context, LoginActivity::class.java))
			}

			// 로그아웃 버튼 이벤트
			ivLogout.setOnClickListener {
				if (AUTH.currentUser != null) {
					startActivity(Intent(context, LogoutActivity::class.java))
				}
			}
		}
	}

	override fun onStart() {
		// 앱이 Reload 했을 때 로그인 인증을 다시 확인한다.
		super.onStart()

		binding!!.apply {
			if (AUTH.currentUser == null) {
				// Firebase 로그인 인증값이 없다.
				Toast.makeText(
					context,
					getString(R.string.check_login),
					Toast.LENGTH_LONG
				).show()

				// 상단 Appbar 로그인 정보를 안보이게 한다.
				ivLogin.isVisible = true
				tvMyid.isVisible = false
				tvEmail.isVisible = false
				ivLogout.isVisible = false
			} else {
				// 상단 Appbar 로그인 정보를 보이게 설정한다.
				ivLogin.isVisible = false
				tvMyid.text = AUTH.currentUser!!.email?.split("@")?.get(0) ?: ""
				val strEmail = AUTH.currentUser!!.email?.split("@")?.get(1) ?: ""
				"@$strEmail".also { tvEmail.text = it }
				tvMyid.isVisible = true
				tvEmail.isVisible = true
				ivLogout.isVisible = true
			}
		}
	}
}