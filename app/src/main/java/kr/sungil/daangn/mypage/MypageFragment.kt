package kr.sungil.daangn.mypage

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.FragmentMypageBinding
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.LoginActivity
import kr.sungil.daangn.LogoutActivity
import kr.sungil.daangn.models.UserModel

class MypageFragment : Fragment(R.layout.fragment_mypage) {
	private var binding: FragmentMypageBinding? = null
	private val auth: FirebaseAuth by lazy { Firebase.auth }
	private lateinit var userDB: DatabaseReference
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Fragment 기본함수: Activity 의 onCreate 함수와 동일함
		super.onViewCreated(view, savedInstanceState)
		val _binding = FragmentMypageBinding.bind(view)
		binding = _binding

		// Firebase Database
		userDB = Firebase.database.reference.child(DB_USERS)

		initLogInOutButton()
		initEditText()
		initSaveButton()
	}

	private fun initLogInOutButton() {
		// 로그인 버튼 이벤트
		binding!!.ivLogin.setOnClickListener {
			startActivity(Intent(context, LoginActivity::class.java))
		}

		// 로그아웃 버튼 이벤트
		binding!!.ivLogout.setOnClickListener {
			if (AUTH.currentUser != null) {
				startActivity(Intent(context, LogoutActivity::class.java))
			}
		}
	}

	private fun initEditText() {
		binding!!.apply {
			etInfoNickname.addTextChangedListener {
				val enable = etInfoNickname.text.isNotEmpty() && etInfoName.text.isNotEmpty()
				btSave.isEnabled = enable
			}

			etInfoName.addTextChangedListener {
				val enable = etInfoNickname.text.isNotEmpty() && etInfoName.text.isNotEmpty()
				btSave.isEnabled = enable
			}
		}
	}

	private fun initSaveButton() {
		binding!!.apply {
			btSave.setOnClickListener {
				if (AUTH.currentUser != null) {
					val nickname = etInfoNickname.text.toString().trim()
					val name = etInfoName.text.toString().trim()
					val userModel = UserModel(
						email = AUTH.currentUser!!.email,
						name = name,
						nickname = nickname,
					)
					userDB.child(AUTH.currentUser!!.uid).updateChildren(userModel.toMap())
					Log.d(MYDEBUG, "initSaveButton: ${userModel.toString()}")
					Log.d(MYDEBUG, "initSaveButton: ${userModel.toMap()}")
				}
			}
		}
	}

	override fun onStart() {
		// 앱이 Reload 했을 때 로그인 인증을 다시 확인한다.
		super.onStart()

		if (AUTH.currentUser == null) {
			// Firebase 로그인 인증값이 없다.
			Toast.makeText(
				context,
				getString(R.string.check_login),
				Toast.LENGTH_LONG
			).show()

			// 상단 Appbar 로그인 정보를 안보이게 한다.
			binding!!.ivLogin.isVisible = true
			binding!!.tvMyid.isVisible = false
			binding!!.tvEmail.isVisible = false
			binding!!.ivLogout.isVisible = false
			// mypage info 정보를 안보이게 한다.
			binding!!.clMypage.isVisible = false
		} else {
			// 상단 Appbar 로그인 정보를 보이게 설정한다.
			binding!!.ivLogin.isVisible = false
			binding!!.tvMyid.text = AUTH.currentUser!!.email?.split("@")?.get(0) ?: ""
			val strEmail = AUTH.currentUser!!.email?.split("@")?.get(1) ?: ""
			"@$strEmail".also { binding!!.tvEmail.text = it }
			binding!!.tvMyid.isVisible = true
			binding!!.tvEmail.isVisible = true
			binding!!.ivLogout.isVisible = true
			// mypage info 정보를 보이게 설정한다.
			binding!!.clMypage.isVisible = true
			val myEmail = AUTH.currentUser!!.email
			binding!!.etInfoEmail.setText(myEmail)
		}
	}
}