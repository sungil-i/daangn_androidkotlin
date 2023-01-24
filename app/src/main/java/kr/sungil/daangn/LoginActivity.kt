package kr.sungil.daangn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.databinding.ActivityLoginBinding
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.models.UserModel

class LoginActivity : AppCompatActivity() {
	private lateinit var binding: ActivityLoginBinding
	private lateinit var userDB: DatabaseReference
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Firebase Database
		userDB = Firebase.database.reference.child(DB_USERS)

		initButtons()
		initEditText()
	}

	private fun initButtons() {
		binding.apply {
			// 로그인, 로그아웃 버튼 이벤트
			btLoginOut.setOnClickListener {
				val email = etEmail.text.toString().trim()
				val password = etPassword.text.toString().trim()

				if (btLoginOut.text.equals("로그인")) { // 로그인 버튼일 경우
					// Firebase 에 email 과 paassword 를 사용하여 로그인합니다.
					AUTH.signInWithEmailAndPassword(email, password)
						.addOnCompleteListener() { task ->
							if (task.isSuccessful) { // 로그인 성공
								successSignIn()
							} else { // 로그인 실패
								Toast.makeText(
									applicationContext,
									getString(R.string.login_fail),
									Toast.LENGTH_SHORT
								).show()
							}
						}
				} else if (btLoginOut.text.equals("로그아웃")) { // 로그아웃 버튼일 경우
					AUTH.signOut()
					etEmail.text.clear()
					etEmail.isEnabled = true
					etPassword.text.clear()
					etPassword.isEnabled = true

					btLoginOut.text = "로그인"
					btLoginOut.isEnabled = false
					btSignup.isEnabled = false
				}
			}

			// 회원가입 버튼 이벤트
			btSignup.setOnClickListener {
				val email = etEmail.text.toString().trim()
				val password = etPassword.text.toString().trim()

				// Firebase 에 email, password 로 사용자를 생성합니다.
				AUTH.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener() { task ->
						if (task.isSuccessful) { // 회원가입 성공
							// Firebase Database 에 UserModel 객체를 저장합니다.
							val userModel = UserModel(
								idx = AUTH.currentUser!!.uid,
								email = AUTH.currentUser!!.email,
								name = "",
								nickname = ""
							)
							// 객체 저장
							userDB.child(AUTH.currentUser!!.uid).setValue(userModel)

							Toast.makeText(
								applicationContext,
								getString(R.string.signup_ok),
								Toast.LENGTH_SHORT
							).show()
						} else { // 회원가입 실패
							Toast.makeText(
								applicationContext,
								getString(R.string.signup_fail),
								Toast.LENGTH_SHORT
							).show()
						}
					}
			}

			// 창 닫기 버튼 이벤트
			ivClose.setOnClickListener {
				finish()
			}
		}
	}

	private fun successSignIn() {
		if (AUTH.currentUser == null) {
			Toast.makeText(
				applicationContext,
				getString(R.string.login_fail),
				Toast.LENGTH_SHORT
			).show()
			return
		}

		// 로그인 성공하면 Activity 를 닫습니다.
		Toast.makeText(
			applicationContext,
			getString(R.string.login_ok),
			Toast.LENGTH_SHORT
		).show()
		finish()
	}

	private fun initEditText() {
		binding.apply {
			// 이메일, 암호값이 있을 때만 버튼을 활성화 합니다
			etEmail.addTextChangedListener {
				val enable = etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()
				btLoginOut.isEnabled = enable
				btSignup.isEnabled = enable
			}
			etPassword.addTextChangedListener {
				val enable = etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()
				btLoginOut.isEnabled = enable
				btSignup.isEnabled = enable
			}
		}
	}
}