package kr.sungil.daangn.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import kr.sungil.daangn.R
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.LoginActivity
import kr.sungil.daangn.LogoutActivity
import kr.sungil.daangn.databinding.FragmentChatBinding

class ChatFragment : Fragment(R.layout.fragment_chat) {
	private var binding: FragmentChatBinding? = null
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Fragment 기본함수: Activity 의 onCreate 함수와 동일함
		super.onViewCreated(view, savedInstanceState)
		val _binding = FragmentChatBinding.bind(view)
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