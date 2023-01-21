package kr.sungil.daangn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.databinding.ActivityLoginBinding
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
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
		// 로그인, 로그아웃 버튼 이벤트
		binding.btLoginOut.setOnClickListener {
			binding.let {
				val email = binding.etEmail.text.toString().trim()
				val password = binding.etPassword.text.toString().trim()

				if (binding.btLoginOut.text.equals("로그인")) { // 로그인 버튼일 경우
					// Firebase 에 email 과 paassword 를 사용하여 로그인합니다.
					AUTH.signInWithEmailAndPassword(email, password)
						.addOnCompleteListener(this) { task ->
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
				} else if (binding.btLoginOut.text.equals("로그아웃")) { // 로그아웃 버튼일 경우
					AUTH.signOut()
					binding.etEmail.text.clear()
					binding.etEmail.isEnabled = true
					binding.etPassword.text.clear()
					binding.etPassword.isEnabled = true

					binding.btLoginOut.text = "로그인"
					binding.btLoginOut.isEnabled = false
					binding.btSignup.isEnabled = false
				}
			}
		}

		// 회원가입 버튼 이벤트
		binding.btSignup.setOnClickListener {
			binding.let {
				val email = it.etEmail.text.toString().trim()
				val password = it.etPassword.text.toString().trim()

				// Firebase 에 email, password 로 사용자를 생성합니다.
				AUTH.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(this) { task ->
						if (task.isSuccessful) { // 회원가입 성공
							// Firebase Database 저장
							val userModel = UserModel(
								email = AUTH.currentUser!!.email,
								name = "",
								nickname = ""
							)
//							userDB.push().setValue(userModel)
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

		// 로그인 성공
		Toast.makeText(
			applicationContext,
			getString(R.string.login_ok),
			Toast.LENGTH_SHORT
		).show()
//		binding.etEmail.isEnabled = false
//		binding.etPassword.isEnabled = false
//		binding.btSignup.isEnabled = false
//		binding.btLoginOut.text = "로그아웃"
		finish()
	}

	private fun initEditText() {
		// 이메일, 암호값이 있을 때만 버튼을 활성화 합니다
		binding.etEmail.addTextChangedListener {
			binding.let {
				val enable = it.etEmail.text.isNotEmpty() && it.etPassword.text.isNotEmpty()
				it.btLoginOut.isEnabled = enable
				it.btSignup.isEnabled = enable
			}
		}
		binding.etPassword.addTextChangedListener {
			binding.let {
				val enable = it.etEmail.text.isNotEmpty() && it.etPassword.text.isNotEmpty()
				it.btLoginOut.isEnabled = enable
				it.btSignup.isEnabled = enable
			}
		}
	}
}