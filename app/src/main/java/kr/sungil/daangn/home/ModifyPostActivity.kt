package kr.sungil.daangn.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.CalendarCache.URI
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kr.sungil.daangn.AppConfig
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.DB_POSTS
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.AppConfig.Companion.STORAGE_IMAGE
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.ActivityModifyPostBinding
import kr.sungil.daangn.models.PostModel

class ModifyPostActivity : AppCompatActivity() {
	private lateinit var binding: ActivityModifyPostBinding
	private var selectedUri: Uri? = null
	private val storage: FirebaseStorage by lazy { Firebase.storage }
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_POSTS) }
	private var postModel: PostModel? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityModifyPostBinding.inflate(layoutInflater)
		setContentView(binding.root)

		initIntent()
		initEditText()
		initImageUploadButton()
		initSubmitButton()
		initCancelButton()
	}

	private fun initIntent() {
		if (intent.hasExtra("idx")) {
			// PostModel 의 ID를 Intent 로 받는다.
			val idx = intent.getStringExtra("idx")
			/*
			// Android 버전에 따라 PostModel 객체를 Intent 로 받습니다.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				postModel = intent.getParcelableExtra("postModel", PostModel::class.java)!!
			} else {
				postModel = intent.getParcelableExtra<PostModel>("postModel")!!
			}*/
			if (idx!!.isNotEmpty()) {
				// Firebase 데이터베이스에서 Post 값을 받는다.
				postDB.child(idx).get().addOnSuccessListener {
					val postModel = it.getValue(PostModel::class.java)
					binding.apply {
						// 기존 Post 의 데이터를 불러와서 보여준다.
						etTitle.setText(postModel!!.title)
						etPrice.setText(postModel.price.toString())
						if (postModel.imageUrl.isNotEmpty()) {
							Glide.with(ivPhoto.context)
								.load(postModel.imageUrl)
								.into(ivPhoto)
						}
					}
				}.addOnFailureListener {
					Toast.makeText(
						applicationContext,
						getString(R.string.there_is_error),
						Toast.LENGTH_LONG
					).show()
					finish()
				}
			} else {
				Toast.makeText(
					applicationContext,
					getString(R.string.there_is_error),
					Toast.LENGTH_LONG
				).show()
				finish()
			}
		} else {
			Toast.makeText(
				applicationContext,
				getString(R.string.there_is_error),
				Toast.LENGTH_LONG
			).show()
			finish()
		}
	}

	private fun initEditText() {
		// EditText 빈값 체크 이벤트
		binding.apply {
			etTitle.addTextChangedListener {
				val enable = etTitle.text.trim().isNotEmpty() && etPrice.text.trim().isNotEmpty()
				btSubmit.isEnabled = enable
			}
			etPrice.addTextChangedListener {
				val enable = etTitle.text.trim().isNotEmpty() && etPrice.text.trim().isNotEmpty()
				btSubmit.isEnabled = enable
			}
		}
	}

	private fun initImageUploadButton() {
		binding.apply {
			btUploadImage.setOnClickListener {
				// 저장소 읽기 권한 요청
				when {
					ContextCompat.checkSelfPermission(
						applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE
					) == PackageManager.PERMISSION_GRANTED -> { // 권한이 있을 경우
						// ContextProvider 를 실행시킵니다.
						startContentProvider()
					}
					shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> { // 권한 요청을 합니다
						// Popup 창을 띄워 권한을 요청합니다
						showPermissionContextPopup()
					}
					else -> { // 권한 요청
						// 권한을 요청합니다
						requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
					}
				}
			}
		}
	}

	private fun initSubmitButton() {
		binding.apply {
			btSubmit.setOnClickListener {
				// 예외처리
				if (AUTH.currentUser == null) return@setOnClickListener
				if (etTitle.text.isEmpty() || etPrice.text.isEmpty()) return@setOnClickListener
				if (postModel == null) return@setOnClickListener

				// 기존에 저장된 값을 미리 저장한다.
				val idx = postModel!!.idx
				val sellerId = postModel!!.sellerId
				val title = etTitle.text.toString().trim()
				val price = etPrice.text.toString().trim().toInt()
				val createdAt = postModel!!.createdAt
				var imageUrl: String = ""
				if (postModel!!.imageUrl.isNotEmpty()) {
					imageUrl = postModel!!.imageUrl
				}

				// Progress 창을 보여줍니다.
				showProgress()

				// 수정한 이미지가 있으면 업로드 과정을 추가합니다.
				if (selectedUri != null) { // 수정할 이미지가 있을 경우
					val imageUri = selectedUri ?: return@setOnClickListener
					uploadImage(imageUri, successHandler = { uri ->
						val updatePostModel = PostModel(
							idx = idx,
							sellerId = sellerId,
							title = title,
							createdAt = createdAt,
							price = price,
							imageUrl = uri // 수정할 이미지의 새로운 경로
						)
						uploadPost(updatePostModel)
					}, errorHandler = {
						Toast.makeText(
							applicationContext, getString(R.string.image_upload_fail), Toast.LENGTH_LONG
						).show()
						hideProgress()
					})
				} else { // 수정할 이미지가 없을 경우
					val updatePostModel = PostModel(
						idx = idx,
						sellerId = sellerId,
						title = title,
						createdAt = createdAt,
						price = price,
						imageUrl = imageUrl // 기존 이미지의 경로
					)
					uploadPost(updatePostModel)
				}
			}
		}
	}

	private fun uploadImage(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
		// 파일 이름을 임의로 지정합니다.
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

	private fun uploadPost(postModel: PostModel) {
		postDB.child(postModel.idx).updateChildren(postModel.toMap())
		Toast.makeText(
			applicationContext, getString(R.string.post_submit_ok), Toast.LENGTH_LONG
		).show()
		finish()
	}

	private fun initCancelButton() {
		binding.ivClose.setOnClickListener {
			finish()
		}
	}

	private fun showProgress() {
		// 업로드 중에 보여줄 Progress 를 보여줍니다.
		binding.pbUploadProgress.isVisible = true
	}

	private fun hideProgress() {
		// 업로드 중에 보여줄 Progress 를 없앱니다..
		binding.pbUploadProgress.isVisible = false
	}

	private fun startContentProvider() {
		// ContentProvider 로 저장소의 파일을 열람합니다.
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		startActivityForResult(intent, 2020)
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
					// 이미지를 성공적으로 읽었을 경우 미리보기를 합니다.
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

	override fun onStart() {
		super.onStart()

		// 로그인이 되어 있는지 체크합니다
		if (AUTH.currentUser == null) {
			Toast.makeText(
				applicationContext,
				getString(R.string.check_login),
				Toast.LENGTH_LONG
			).show()
			finish()
		}
	}
}