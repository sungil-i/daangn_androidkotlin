package kr.sungil.daangn.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.CHILD_CHAT
import kr.sungil.daangn.AppConfig.Companion.DB_CHATS
import kr.sungil.daangn.AppConfig.Companion.DB_POSTS
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.chat.ChatActivity
import kr.sungil.daangn.databinding.ActivityViewPostBinding
import kr.sungil.daangn.models.ChatRoomModel
import kr.sungil.daangn.models.PostModel
import kr.sungil.daangn.models.UserModel
import java.text.NumberFormat

class ViewPostActivity : AppCompatActivity() {
	private lateinit var binding: ActivityViewPostBinding
	private val userDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_USERS) }
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_POSTS) }
	private val chatDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_CHATS) }
	private lateinit var postModel: PostModel
	private lateinit var seller: UserModel
	private var postId = ""
	private var sellerId = ""
	private var chatRoomId = ""

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityViewPostBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Intent 를 가져옵니다. 예외처리
		if (intent.hasExtra("idx").not()) finish()
		val idx = intent.getStringExtra("idx") ?: ""
		if (idx.isEmpty()) finish()

		// Firebase 에서 PostModel 을 가져옵니다.
		postDB.child(idx).get().addOnSuccessListener { post ->
			val _postModel = post.getValue(PostModel::class.java)!!
			postId = _postModel.idx!!
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
			userDB.child(_postModel.sellerId!!).get().addOnSuccessListener { user ->
				// Firebase 에서 판매자 UserModel 을 가져옵니다.
				val _seller = user.getValue(UserModel::class.java)!!
				seller = UserModel(
					idx = _seller.idx,
					email = _seller.email,
					name = _seller.name,
					nickname = _seller.nickname
				)
//				Log.d(MYDEBUG, "onCreate: $seller")

				// 채팅방을 만들고 seller, buyer 모두 접속할 수 있게 합니다.
				val myId = AUTH.currentUser!!.uid
				sellerId = _seller.idx!!


				postDB.child(postId).child(CHILD_CHAT).child(myId).get().addOnSuccessListener {
					if (it.exists()) {
//						Log.d(MYDEBUG, "onCreate: 채팅방이 존재합니다.")
						val chatRoomModel = it.getValue(ChatRoomModel::class.java)!!
						chatRoomId = chatRoomModel.idx
						val updateChatRoomModel = ChatRoomModel(
							idx = chatRoomModel.idx,
							buyerId = chatRoomModel.buyerId,
							sellerId = chatRoomModel.sellerId,
							postId = chatRoomModel.postId,
							createdAt = chatRoomModel.createdAt,
							title = postModel.title,
							sellerNickname = seller.nickname,
							lastMessage = "",
							imageUrl = postModel.imageUrl,
							updatedAt = 0
						)
//						Log.d(MYDEBUG, "onCreate: $chatRoomId")
						// 채팅방 정보를 업데이트 합니다.
						// post DB 업데이트
						postDB.child(postId).child(CHILD_CHAT).child(myId).updateChildren(updateChatRoomModel.toMap())
						userDB.child(sellerId).child(CHILD_CHAT).child(chatRoomId).updateChildren(updateChatRoomModel.toMap())
						// 나의 DB 업데이트
						userDB.child(myId).child(CHILD_CHAT).child(chatRoomId).updateChildren(updateChatRoomModel.toMap())
						// 채팅 DB 업데이트
						chatDB.child(chatRoomId).updateChildren(updateChatRoomModel.toMap())
					} else {
//						Log.d(MYDEBUG, "onCreate: 채팅방이 없습니다.")
						val chatRoomRef = chatDB.push()
						chatRoomId = chatRoomRef.key ?: ""
						val chatRoomModel = ChatRoomModel(
							idx = chatRoomId,
							buyerId = myId,
							sellerId = sellerId,
							postId = postId,
							createdAt = System.currentTimeMillis(),
							title = postModel.title,
							sellerNickname = seller.nickname,
							lastMessage = "",
							imageUrl = postModel.imageUrl,
							updatedAt = 0
						)
						// post DB 추가
						postDB.child(postId).child(CHILD_CHAT).child(myId).setValue(chatRoomModel)
						userDB.child(sellerId).child(CHILD_CHAT).child(chatRoomId).setValue(chatRoomModel)
						// 나의 DB 추가
						userDB.child(myId).child(CHILD_CHAT).child(chatRoomId).setValue(chatRoomModel)
						// 채팅 DB 추가
						chatRoomRef.setValue(chatRoomModel)
					}
				}

				/*
				*/

				initViews()
				initCancelButton()
				initChatButton()
			}.addOnFailureListener {
//				Log.d(MYDEBUG, "onCreate: $it")
			}
		}.addOnFailureListener {
//			Log.d(MYDEBUG, "onCreate: $it")
		}
	}

	private fun initViews() {
		// 상세 정보 보기 창의 View 를 초기화 합니다.
		binding.apply {
			val priceFormat = NumberFormat.getInstance()

			tvTitle.text = postModel.title
			tvPrice.text = "${priceFormat.format(postModel.price)}원"
			tvEmail.text = seller.email
			tvNickname.text = seller.nickname
			tvDetail.text = postModel.detail

			if (postModel.imageUrl!!.isNotEmpty()) {
				Glide.with(ivPhoto.context)
					.load(postModel.imageUrl)
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

	private fun initChatButton() {
		// Chat 버튼 클릭 이벤트
		binding.ivChat.setOnClickListener {
			// 채팅 Activity 를 엽니다.
			val intent = Intent(applicationContext, ChatActivity::class.java)
			intent.putExtra("chatRoomId", chatRoomId)
			intent.putExtra("postId", postId)
			intent.putExtra("sellerId", sellerId)
			startActivity(intent)
			finish()
		}
	}

	override fun onStart() {
		super.onStart()

		// 로그인이 되어 있는지 체크합니다
		if (AUTH.currentUser == null) finish()
	}
}