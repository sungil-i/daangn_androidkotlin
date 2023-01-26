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
		initCancelButton()
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

	private fun initEditText() {
		// EditText 값이 있을 때만 버튼을 활성화 합니다.
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
						idx = AUTH.currentUser!!.uid,
						email = AUTH.currentUser!!.email!!,
						name = name,
						nickname = nickname,
					)
					// Firebase Database 에 UserModel 객체를 저장합니다.
					userDB.child(AUTH.currentUser!!.uid).updateChildren(userModel.toMap())
					tvMessage.text = "회원정보를 업데이트 했습니다!"
				}
			}
		}
	}

	private fun initCancelButton() {
		binding!!.apply {
			btCancel.setOnClickListener {
				// 취소 버튼을 누르면 원래 DB에 저장되어 있던 값으로 초기화 합니다.
				getDataFromDatabase()
				tvMessage.text = "회원정보 수정을 취소합니다."
			}
		}
	}

	private fun getDataFromDatabase() {
		// Firebase Database 에서 저장된 값을 가져오는 함수
		binding!!.apply {
			if (AUTH.currentUser != null) {
				userDB.child(AUTH.currentUser!!.uid).get().addOnSuccessListener {
					// Firebase 에서 데이터 가져오기 성공
					val userModel = it.getValue(UserModel::class.java)
					if (userModel!!.nickname.isNotEmpty()) {
						etInfoNickname.setText(userModel.nickname)
					} else {
						etInfoNickname.setText("")
					}
					if (userModel.name.isNotEmpty()) {
						etInfoName.setText(userModel.name)
					} else {
						etInfoName.setText("")
					}
				}.addOnFailureListener {
					// Firebase 에서 데이터 가져오기 실패
//				Log.d(MYDEBUG, "initEditText: FAIL}")
				}
			}
		}
	}

	override fun onStart() {
		// 앱이 Reload 했을 때 로그인 인증을 다시 확인한다.
		super.onStart()

		binding!!.apply {
			if (AUTH.currentUser == null) { // 인증 값이 없을 경우
				// Firebase 로그인 인증값이 없다.
				Toast.makeText(
					context, getString(R.string.check_login), Toast.LENGTH_LONG
				).show()

				// 상단 Appbar 로그인 정보를 안보이게 한다.
				ivLogin.isVisible = true
				tvMyid.isVisible = false
				tvEmail.isVisible = false
				ivLogout.isVisible = false
				// mypage info 정보를 안보이게 한다.
				clMypage.isVisible = false
				tvMessage.text = ""
			} else { // 인증 값이 있을 경우
				// 상단 Appbar 로그인 정보를 보이게 설정한다.
				ivLogin.isVisible = false
				tvMyid.text = AUTH.currentUser!!.email?.split("@")?.get(0) ?: ""
				val strEmail = AUTH.currentUser!!.email?.split("@")?.get(1) ?: ""
				"@$strEmail".also { tvEmail.text = it }
				tvMyid.isVisible = true
				tvEmail.isVisible = true
				ivLogout.isVisible = true
				// mypage info 정보를 보이게 설정한다.
				clMypage.isVisible = true
				val myEmail = AUTH.currentUser!!.email
				etInfoEmail.setText(myEmail)

				// Firebase Database 에서 저장된 값을 가져옵니다.
				getDataFromDatabase()
			}
		}
	}
}