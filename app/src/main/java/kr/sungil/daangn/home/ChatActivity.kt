package kr.sungil.daangn.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.CHILD_CHAT
import kr.sungil.daangn.AppConfig.Companion.DB_CHATS
import kr.sungil.daangn.AppConfig.Companion.DB_POSTS
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
import kr.sungil.daangn.AppConfig.Companion.LEFT_BIASED_CHAT
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.AppConfig.Companion.RIGHT_BIASED_CHAT
import kr.sungil.daangn.R
import kr.sungil.daangn.adapter.ChatAdapter
import kr.sungil.daangn.databinding.ActivityChatBinding
import kr.sungil.daangn.models.ChatModel
import kr.sungil.daangn.models.ChatRoomModel
import kr.sungil.daangn.models.PostModel
import kr.sungil.daangn.models.UserModel
import java.text.NumberFormat

class ChatActivity : AppCompatActivity() {
	private lateinit var binding: ActivityChatBinding
	private val userDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_USERS) }
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_POSTS) }
	private val chatDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_CHATS) }
	private lateinit var postModel: PostModel
	private lateinit var seller: UserModel
	private lateinit var my: UserModel
	private lateinit var chatRoomModel: ChatRoomModel
	private var isDatabaseOk: Boolean = false
	private lateinit var adapter: ChatAdapter
	private lateinit var listener: Any
	private val chatList = mutableListOf<ChatModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityChatBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Firebase Database 연결 Listener
		listener = object : ChildEventListener {
			override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
				val chatModel = snapshot.getValue(ChatModel::class.java)
				chatModel ?: return

				chatModel.viewType =
					if (chatModel.senderId == AUTH.currentUser!!.uid) RIGHT_BIASED_CHAT else LEFT_BIASED_CHAT
				chatList.add(chatModel)
				adapter.submitList(chatList)
				adapter.notifyDataSetChanged()
			}

			override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
			override fun onChildRemoved(snapshot: DataSnapshot) {}
			override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
			override fun onCancelled(error: DatabaseError) {}
		}

		// RecyclerView 를 연결합니다.
		adapter = ChatAdapter()
		binding.rvChats.adapter = adapter

		initIntent()
		initSendButton()
		initCancelButton()
	}

	private fun initIntent() {
		if (AUTH.currentUser == null) {
			Toast.makeText(
				applicationContext, getString(R.string.check_login), Toast.LENGTH_LONG
			).show()
			finish()
		}
		if (intent.hasExtra("idx")) {
			val idx = intent.getStringExtra("idx")
//			Log.d(MYDEBUG, "initIntent: $idx")
			if (idx!!.isNotEmpty()) {
				postDB.child(idx).get().addOnSuccessListener { post ->
					val _postModel = post.getValue(PostModel::class.java)!!
//					Log.d(MYDEBUG, "initIntent: $_postModel")
					userDB.child(_postModel.sellerId).get().addOnSuccessListener { user ->
						val _seller = user.getValue(UserModel::class.java)!!
//						Log.d(MYDEBUG, "initIntent: $_seller")
						userDB.child(AUTH.currentUser!!.uid).get().addOnSuccessListener { user ->
							val _buyer = user.getValue(UserModel::class.java)!!
//							Log.d(MYDEBUG, "initIntent: $_buyer")
							val chatRoomRef = chatDB.push()
							val chatRoomKey = chatRoomRef.key ?: ""
//							Log.d(MYDEBUG, "initIntent: $chatRoomKey")
							val _chatRoomModel = ChatRoomModel(
								idx = chatRoomKey,
								buyerId = _buyer.idx,
								sellerId = _seller.idx,
								postId = _postModel.idx,
								createdAt = System.currentTimeMillis()
							)
							if (_postModel != null && _buyer != null && _chatRoomModel != null) {
								postModel = _postModel
								seller = _seller
								my = _buyer
								chatRoomModel = _chatRoomModel
								isDatabaseOk = true

								userDB.child(seller.idx).child(CHILD_CHAT).child(chatRoomKey)
									.setValue(chatRoomModel)
								userDB.child(my.idx).child(CHILD_CHAT).child(chatRoomKey)
									.setValue(chatRoomModel)

								// Firebase 데이터베이스를 연결합니다.
								chatDB.child(chatRoomKey).addChildEventListener(listener as ChildEventListener)
								chatList.clear()
								initCardView()
							} else {
								isDatabaseOk = false
								Toast.makeText(
									applicationContext, getString(R.string.there_is_error), Toast.LENGTH_LONG
								).show()
								finish()
							}
						}.addOnFailureListener {
							Toast.makeText(
								applicationContext, getString(R.string.there_is_error), Toast.LENGTH_LONG
							).show()
							Log.d(MYDEBUG, "initIntent: $it")
						}
					}.addOnFailureListener {
						Toast.makeText(
							applicationContext, getString(R.string.there_is_error), Toast.LENGTH_LONG
						).show()
						Log.d(MYDEBUG, "initIntent: $it")
					}
				}.addOnFailureListener {
					Toast.makeText(
						applicationContext, getString(R.string.there_is_error), Toast.LENGTH_LONG
					).show()
					finish()
				}
			}
		} else {
			Toast.makeText(
				applicationContext, getString(R.string.there_is_error), Toast.LENGTH_LONG
			).show()
			finish()
		}
	}

	private fun initCardView() {
		if (postModel != null) {
			binding.apply {
				val priceFormat = NumberFormat.getInstance()

				tvCvTitle.text = postModel.title
				tvCvPrice.text = "${priceFormat.format(postModel.price)}원"

				if (postModel.imageUrl.isNotEmpty()) {
					Glide.with(ivCvPhoto.context)
						.load(postModel.imageUrl)
						.into(ivCvPhoto)
				}
			}
		}
	}

	private fun initSendButton() {
		binding.apply {
			btSend.setOnClickListener {
				if (etMessage.text.isNotEmpty()) {
					val chatRef = chatDB.child(chatRoomModel.idx).push()
					val chatKey = chatRef.key ?: ""
					val chatModel = ChatModel(
						idx = chatKey,
						chatRoomIdx = chatRoomModel.idx,
						senderId = my.idx,
						email = my.email,
						nickName = my.name,
						message = etMessage.text.toString(),
						viewType = 0,
						createAt = System.currentTimeMillis()
					)

					chatRef.setValue(chatModel)
					etMessage.text.clear()
				}
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

		// Reload 할 때 RecyclerView 를 다시 읽습니다.
		adapter.notifyDataSetChanged()

		// 로그인이 되어 있는지 체크합니다
		if (AUTH.currentUser == null) {
			Toast.makeText(
				applicationContext, getString(R.string.check_login), Toast.LENGTH_LONG
			).show()
			finish()
		}
	}

	override fun onResume() {
		super.onResume()

		// Reload 할 때 RecyclerView 를 다시 읽습니다.
		adapter.notifyDataSetChanged()
	}
}