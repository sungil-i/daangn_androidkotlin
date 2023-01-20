package kr.sungil.daangn.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.FragmentMypageBinding

class MypageFragment : Fragment(R.layout.fragment_mypage) {
	private var binding: FragmentMypageBinding? = null
	private val auth: FirebaseAuth by lazy { Firebase.auth }
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Fragment 기본함수: Activity 의 onCreate 함수와 동일함
		super.onViewCreated(view, savedInstanceState)
		val _binding = FragmentMypageBinding.bind(view)
		binding = _binding

		initButtons()
		initEditText()
	}

	private fun initButtons() {
		// 로그인/로그아웃 버튼 이벤트
		binding!!.btLoginOut.setOnClickListener {
			binding!!.let { binding ->
				val email = binding.etEmail.text.toString()
				val password = binding.etPassword.text.toString()

				// 인증값이 있는지 체크
				if (auth.currentUser == null) { // 로그인 버튼을 눌렀을 때 인증값이 없으면 로그인을 시도한다.
					// Firebase 로그인
					auth.signInWithEmailAndPassword(email, password)
						.addOnCompleteListener(requireActivity()) { task ->
							if (task.isSuccessful) { // Firebase 로그인 성공
								successSignIn()
							} else { // Firebase 로그인 실패
								Toast.makeText(context, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
							}
						}
				} else { // 로그아웃 버튼을 눌렀을 때 로그아웃 하고 화면을 초기화 한다.
					auth.signOut()
					Toast.makeText(context, "로그아웃 했습니다.", Toast.LENGTH_SHORT).show()
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
		binding!!.btSignup.setOnClickListener {
			binding!!.let {
				val email = it.etEmail.text.toString()
				val password = it.etPassword.text.toString()

				// Firebase 회원가입 작업
				auth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(requireActivity()) { task ->
						if (task.isSuccessful) { // Firebase 회원가입 성공
							Toast.makeText(context, "회원가입에 성공했습니다. 로그인 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
						} else { // Firebase 회원가입 실패
							Toast.makeText(context, "회원가입에 실패했습니다. 이미 가입한 이메일일 수 있습니다.", Toast.LENGTH_SHORT).show()
						}
					}
			}
		}
	}

	private fun initEditText() {
		// 이메일 암호 값이 모두 있을 때만 로그인, 회원가입 버튼을 활성화 한다
		binding!!.etEmail.addTextChangedListener {
			binding!!.let {
				val enable = it.etEmail.text.isNotEmpty() && it.etPassword.text.isNotEmpty()
				it.btLoginOut.isEnabled = enable
				it.btSignup.isEnabled = enable
			}
		}

		// 이메일 암호 값이 모두 있을 때만 로그인, 회원가입 버튼을 활성화 한다
		binding!!.etPassword.addTextChangedListener {
			binding!!.let {
				val enable = it.etEmail.text.isNotEmpty() && it.etPassword.text.isNotEmpty()
				it.btLoginOut.isEnabled = enable
				it.btSignup.isEnabled = enable
			}
		}
	}

	private fun successSignIn() {
		// 인증값이 없다면 경고메시지를 띄운다.
		if (auth.currentUser == null) {
			Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
			return
		}

		// 로그인에 성공하고 인증값이 있다면 성공메시지를 띄우고 Button 과 EditText 를 비활성화 한다.
		Toast.makeText(context, "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show()
		binding?.etEmail?.isEnabled = false
		binding?.etPassword?.isEnabled = false
		binding?.btSignup?.isEnabled = false
		binding?.btLoginOut?.text = "로그아웃"
	}

	override fun onStart() {
		// 앱이 Reload 했을 때 로그인 인증을 다시 확인한다.
		super.onStart()

		if (auth.currentUser == null) {
			// 만약에 인증값이 없다면 Button 과 EditText 를 초기화 한다.
			binding?.let {
				it.etEmail.text.clear()
				it.etEmail.isEnabled = true
				it.etPassword.text.clear()
				it.etPassword.isEnabled = true

				it.btLoginOut.text = "로그인"
				it.btLoginOut.isEnabled = false
				it.btSignup.isEnabled = false
			}
		} else {
			// 만약에 인증값이 있다면 Button 과 EditText 를 비활성화 한다.
			binding?.let {
				it.etEmail.setText(auth.currentUser!!.email)
				it.etPassword.setText("************")
				it.etEmail.isEnabled = false
				it.etPassword.isEnabled = false

				it.btLoginOut.text = "로그아웃"
				it.btLoginOut.isEnabled = true
				it.btSignup.isEnabled = false
			}
		}
	}
}