package kr.sungil.daangn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.databinding.ActivityLoginBinding
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG

class LoginActivity : AppCompatActivity() {
	private lateinit var binding: ActivityLoginBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)

		initButtons()
		initEditText()
	}

	private fun initButtons() {
		binding.btLoginOut.setOnClickListener {
			binding.let {
				val email = binding.etEmail.text.toString()
				val password = binding.etPassword.text.toString()

				if (AUTH.currentUser == null) {
					AUTH.signInWithEmailAndPassword(email, password)
						.addOnCompleteListener(this) { task ->
							if (task.isSuccessful) {
								successSignIn()
							} else {
								Toast.makeText(
									applicationContext,
									getString(R.string.login_fail),
									Toast.LENGTH_SHORT
								).show()
							}
						}
				} else {
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

		binding.btSignup.setOnClickListener {
			binding.let {
				val email = it.etEmail.text.toString()
				val password = it.etPassword.text.toString()

				AUTH.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(this) { task ->
						if (task.isSuccessful) {
							Toast.makeText(
								applicationContext,
								getString(R.string.signup_ok),
								Toast.LENGTH_SHORT
							)
								.show()
						} else {
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

		Toast.makeText(
			applicationContext,
			getString(R.string.login_ok),
			Toast.LENGTH_SHORT
		).show()
		binding.etEmail.isEnabled = false
		binding.etPassword.isEnabled = false
		binding.btSignup.isEnabled = false
		binding.btLoginOut.text = "로그아웃"
		finish()
	}

	private fun initEditText() {
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