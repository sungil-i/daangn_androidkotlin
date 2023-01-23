package kr.sungil.daangn.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kr.sungil.daangn.databinding.ActivityAddPostBinding
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.DB_POSTS
import kr.sungil.daangn.AppConfig.Companion.STORAGE_IMAGE
import kr.sungil.daangn.R
import kr.sungil.daangn.models.PostModel

class AddPostActivity : AppCompatActivity() {
	private lateinit var binding: ActivityAddPostBinding
	private var selectedUri: Uri? = null
	private val storage: FirebaseStorage by lazy { Firebase.storage }
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_POSTS) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityAddPostBinding.inflate(layoutInflater)
		setContentView(binding.root)

		initEditText()
		initPhotoUploadButton()
		initSubmitButton()
		initCancelButton()
	}

	private fun initEditText() {
		binding.apply {
			etTitle.addTextChangedListener {
				val enable = etTitle.text.isNotEmpty() && etPrice.text.isNotEmpty()
				btSubmit.isEnabled = enable
			}
			etPrice.addTextChangedListener {
				val enable = etTitle.text.isNotEmpty() && etPrice.text.isNotEmpty()
				btSubmit.isEnabled = enable
			}
		}
	}

	private fun initPhotoUploadButton() {
		binding.apply {
			btUploadImage.setOnClickListener {
				when {
					ContextCompat.checkSelfPermission(
						applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE
					) == PackageManager.PERMISSION_GRANTED -> {
						startContentProvider()
					}
					shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
						showPermissionContextPopup()
					}
					else -> {
						requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
					}
				}
			}
		}
	}

	private fun initSubmitButton() {
		binding.apply {
			btSubmit.setOnClickListener {
				val sellerId = AUTH.currentUser?.uid.orEmpty()
				val title = etTitle.text.toString()
				val price = etPrice.text.toString().toInt()

				showProgress()

				// 이미지가 있으면 업로드 과정을 추가합니다.
				if (selectedUri != null) { // 이미지가 있을 경우
					val imageUri = selectedUri ?: return@setOnClickListener
					uploadImage(imageUri, successHandler = { uri ->
						uploadPost(sellerId, title, price, uri)
					}, errorHandler = {
						Toast.makeText(
							applicationContext, getString(R.string.image_upload_fail), Toast.LENGTH_LONG
						).show()
						hideProgress()
					})
				} else { // 이미지가 없을 경우
					uploadPost(sellerId, title, price, "")
					Toast.makeText(
						applicationContext, getString(R.string.post_submit_ok), Toast.LENGTH_LONG
					).show()
				}
			}
		}
	}

	private fun initCancelButton() {
		binding.ivClose.setOnClickListener {
			finish()
		}
	}

	private fun uploadPost(sellerId: String, title: String, price: Int, imageUrl: String) {
		val postModel = PostModel(sellerId, title, System.currentTimeMillis(), price, imageUrl)
		postDB.push().setValue(postModel)

		Toast.makeText(
			applicationContext, getString(R.string.post_submit_ok), Toast.LENGTH_LONG
		).show()
		hideProgress()
//		finish()
	}

	private fun uploadImage(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
		val fileName = "${System.currentTimeMillis()}.png"
		storage.reference.child(STORAGE_IMAGE).child(fileName).putFile(uri).addOnCompleteListener {
			if (it.isSuccessful) { // 이미지 업로드 성공
				storage.reference.child(STORAGE_IMAGE)
					.child(fileName).downloadUrl.addOnSuccessListener { uri ->
						successHandler(uri.toString())
						Toast.makeText(
							applicationContext, getString(R.string.image_upload_ok), Toast.LENGTH_LONG
						).show()
					}.addOnFailureListener {
						errorHandler()
					}
			} else { // 이미지 업로드 실패
				errorHandler()
			}
		}
	}

	private fun showProgress() {
		binding.pbUploadProgress.isVisible = true
	}

	private fun hideProgress() {
		binding.pbUploadProgress.isVisible = false
	}

	private fun startContentProvider() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		startActivityForResult(intent, 2020)
	}

	private fun showPermissionContextPopup() {
		AlertDialog.Builder(this).setTitle("권한이 필요합니다.").setMessage("사진을 가져오기 위해 권한이 필요합니다.")
			.setPositiveButton("동의") { _, _ ->
				requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
			}.create().show()
	}

	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<out String>, grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			1010 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startContentProvider()
			} else {
				Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode != Activity.RESULT_OK) {
			return
		}

		when (requestCode) {
			2020 -> {
				val uri = data?.data
				if (uri != null) {
					binding.ivPhoto.setImageURI(uri)
					selectedUri = uri
				} else {
					Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
				}

			}
			else -> {
				Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
			}
		}
	}
}