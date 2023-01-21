package kr.sungil.daangn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.databinding.ActivityLogoutBinding
import kotlin.math.log

class LogoutActivity : AppCompatActivity() {
	private lateinit var binding: ActivityLogoutBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLogoutBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.btLogout.setOnClickListener {
			logout()
		}
	}

	private fun logout() {
		if (AUTH.currentUser != null) {
			AUTH.signOut()
			Toast.makeText(
				this,
				getString(R.string.logout_ok),
				Toast.LENGTH_LONG
			).show()
			finish()
		} else {
			finish()
		}
	}
}