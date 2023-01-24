package kr.sungil.daangn.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.DB_POSTS
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.R
import kr.sungil.daangn.databinding.ActivityViewPostBinding
import kr.sungil.daangn.models.PostModel
import kr.sungil.daangn.models.UserModel
import java.text.NumberFormat

class ViewPostActivity : AppCompatActivity() {
	private lateinit var binding: ActivityViewPostBinding
	private val userDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_USERS) }
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_POSTS) }
	private lateinit var postModel: PostModel
	private lateinit var seller: UserModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityViewPostBinding.inflate(layoutInflater)
		setContentView(binding.root)

		if (intent.hasExtra("idx").not()) finish()
		val idx = intent.getStringExtra("idx") ?: ""
		if (idx.isEmpty()) finish()

		// Firebase 에서 PostModel 을 가져옵니다.
		postDB.child(idx).get().addOnSuccessListener { post ->
			val _postModel = post.getValue(PostModel::class.java)!! as PostModel
			postModel = PostModel(
				idx = _postModel.idx,
				sellerId = _postModel.sellerId,
				title = _postModel.title,
				createdAt = _postModel.createdAt,
				price = _postModel.price,
				imageUrl = _postModel.imageUrl,
				detail = _postModel.detail
			)
//			Log.d(MYDEBUG, "onCreate: $postModel")
			userDB.child(postModel.sellerId).get().addOnSuccessListener { user ->
				val _seller = user.getValue(UserModel::class.java)!!
				seller = UserModel(
					idx = _seller.idx,
					email = _seller.email,
					name = _seller.name,
					nickname = _seller.nickname
				)
//				Log.d(MYDEBUG, "onCreate: $seller")
				initViews()
				initCancelButton()
			}.addOnFailureListener {
//				Log.d(MYDEBUG, "onCreate: $it")
			}
		}.addOnFailureListener {
//			Log.d(MYDEBUG, "onCreate: $it")
		}
	}

	private fun initViews() {
		binding.apply {
			val priceFormat = NumberFormat.getInstance()

			tvTitle.text = postModel!!.title
			tvPrice.text = "${priceFormat.format(postModel!!.price)}원"
			tvEmail.text = seller!!.email
			tvNickname.text = seller!!.nickname
			tvDetail.text = postModel!!.detail

			if (postModel!!.imageUrl.isNotEmpty()) {
				Glide.with(ivPhoto.context)
					.load(postModel!!.imageUrl)
					.into(ivPhoto)
			}
		}
	}

	private fun initCancelButton() {
		// 취소 버튼 이벤트
		binding.ivClose.setOnClickListener {
			finish()
		}
	}

	override fun onStart() {
		super.onStart()

		// 로그인이 되어 있는지 체크합니다
		if (AUTH.currentUser == null) finish()
	}
}