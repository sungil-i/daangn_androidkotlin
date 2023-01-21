package kr.sungil.daangn.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.FragmentMypageBinding
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.LoginActivity
import kr.sungil.daangn.LogoutActivity

class MypageFragment : Fragment(R.layout.fragment_mypage) {
	private var binding: FragmentMypageBinding? = null
	private val auth: FirebaseAuth by lazy { Firebase.auth }
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Fragment 기본함수: Activity 의 onCreate 함수와 동일함
		super.onViewCreated(view, savedInstanceState)
		val _binding = FragmentMypageBinding.bind(view)
		binding = _binding

		initLogInOutButton()
	}

	private fun initLogInOutButton() {
		binding!!.ivLogin.setOnClickListener {
			startActivity(Intent(context, LoginActivity::class.java))
		}

		binding!!.ivLogout.setOnClickListener {
			if (AUTH.currentUser != null) {
				startActivity(Intent(context, LogoutActivity::class.java))
			}
		}
	}

	override fun onStart() {
		// 앱이 Reload 했을 때 로그인 인증을 다시 확인한다.
		super.onStart()

		if (AUTH.currentUser == null) {
			Toast.makeText(
				context,
				getString(R.string.check_login),
				Toast.LENGTH_LONG
			).show()
			binding!!.ivLogin.isVisible = true
			binding!!.tvMyid.isVisible = false
			binding!!.tvEmail.isVisible = false
			binding!!.ivLogout.isVisible = false
		} else {
			Log.d(MYDEBUG, "MypageFragment onStart: ${AUTH.currentUser}")
			binding!!.ivLogin.isVisible = false
			binding!!.tvMyid.text = AUTH.currentUser!!.email?.split("@")?.get(0) ?: ""
			val strEmail = AUTH.currentUser!!.email?.split("@")?.get(1) ?: ""
			"@$strEmail".also { binding!!.tvEmail.text = it }
			binding!!.tvMyid.isVisible = true
			binding!!.tvEmail.isVisible = true
			binding!!.ivLogout.isVisible = true
		}
	}
}