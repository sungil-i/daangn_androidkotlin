package kr.sungil.daangn.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.AppConfig
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.CHILD_CHAT
import kr.sungil.daangn.AppConfig.Companion.LEFT_BIASED_CHAT
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.AppConfig.Companion.RIGHT_BIASED_CHAT
import kr.sungil.daangn.adapter.ChatAdapter
import kr.sungil.daangn.adapter.ChatRoomAdapter
import kr.sungil.daangn.databinding.ActivityChatBinding
import kr.sungil.daangn.home.ViewPostActivity
import kr.sungil.daangn.models.ChatModel
import kr.sungil.daangn.models.ChatRoomModel
import kr.sungil.daangn.models.PostModel
import kr.sungil.daangn.models.UserModel
import java.text.NumberFormat

class ChatActivity : AppCompatActivity() {
	private lateinit var binding: ActivityChatBinding
	private val userDB: DatabaseReference by lazy { Firebase.database.reference.child(AppConfig.DB_USERS) }
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(AppConfig.DB_POSTS) }
	private val chatDB: DatabaseReference by lazy { Firebase.database.reference.child(AppConfig.DB_CHATS) }
	private lateinit var adapter: ChatAdapter
	private val chatList = mutableListOf<ChatModel>()
	private lateinit var chatRoomModel: ChatRoomModel
	private lateinit var postModel: PostModel
	private lateinit var sellerModel: UserModel
	private lateinit var myModel: UserModel
	private lateinit var chatRoomId: String
	private lateinit var postId: String
	private lateinit var sellerId: String
	private lateinit var myId: String
	private lateinit var myEmail: String
	private lateinit var myNickname: String
	private lateinit var listener: Any

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityChatBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Intent 를 가져옵니다. 예외처리
		if (intent.hasExtra("chatRoomId").not()) finish()
		if (intent.hasExtra("postId").not()) finish()
		if (intent.hasExtra("sellerId").not()) finish()
		chatRoomId = intent.getStringExtra("chatRoomId") ?: ""
		postId = intent.getStringExtra("postId") ?: ""
		sellerId = intent.getStringExtra("sellerId") ?: ""
		if (chatRoomId.isEmpty()) finish()
		if (postId.isEmpty()) finish()
		if (sellerId.isEmpty()) finish()
		if (AUTH.currentUser == null) finish()
		myId = AUTH.currentUser!!.uid

		// Firebase 에서 데이터 객체 가져오기
		userDB.child(myId).get().addOnSuccessListener {
			val _myModel = it.getValue(UserModel::class.java)!!
			myModel = UserModel(
				idx = _myModel.idx ?: "",
				email = _myModel.email ?: "",
				name = _myModel.name ?: "",
				nickname = _myModel.nickname ?: ""
			)
			myEmail = myModel.email
			myNickname = myModel.nickname
			if (myModel.nickname.isEmpty()) {
				Toast.makeText(
					applicationContext,
					"채팅 닉네임 생성 후 사용해주세요.\n" + "채팅 닉네임은 나의 정보에서\n" + "만들 수 있습니다.",
					Toast.LENGTH_LONG
				).show()
				finish()
			}
		}

		postDB.child(postId).get().addOnSuccessListener {
			val _postModel = it.getValue(PostModel::class.java)!!
			postModel = PostModel(
				idx = _postModel.idx ?: "",
				sellerId = _postModel.sellerId ?: "",
				title = _postModel.title ?: "",
				createdAt = _postModel.createdAt ?: 0,
				price = _postModel.price ?: 0,
				imageUrl = _postModel.imageUrl ?: "",
				detail = _postModel.detail ?: ""
			)

			// RecyclerView 를 연결합니다.
			adapter = ChatAdapter()
			binding.rvChats.adapter = adapter
			listener = object : ChildEventListener {
				override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
					val chatModel = snapshot.getValue(ChatModel::class.java)
					chatModel ?: return

					chatModel.viewType =
						if (chatModel.senderId == myId) RIGHT_BIASED_CHAT else LEFT_BIASED_CHAT
					chatList.add(chatModel)
					adapter.submitList(chatList)
					adapter.notifyDataSetChanged()
					binding.rvChats.scrollToPosition(chatList.size - 1)
				}

				override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

				override fun onChildRemoved(snapshot: DataSnapshot) {}
				override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
				override fun onCancelled(error: DatabaseError) {}
			}
			chatDB.child(chatRoomId).child(CHILD_CHAT)
				.addChildEventListener(listener as ChildEventListener)
			chatList.clear()

			initCardView()
			initSendButton()
			initCancelButton()
		}
	}

	private fun initCardView() {
		binding.apply {
			val priceFormat = NumberFormat.getInstance()

			tvCvTitle.text = postModel.title
			tvCvPrice.text = "${priceFormat.format(postModel.price)}원"

			if (postModel.imageUrl!!.isNotEmpty()) {
				Glide.with(ivCvPhoto.context).load(postModel.imageUrl).into(ivCvPhoto)
			}

			tvCvTitle.setOnClickListener {
				val intent = Intent(applicationContext, ViewPostActivity::class.java)
				intent.putExtra("idx", postId)
				startActivity(intent)
				finish()
			}
		}
	}

	private fun initSendButton() {
		binding.apply {
			btSend.setOnClickListener {
				if (binding.etMessage.text.isEmpty()) return@setOnClickListener
				val chatRef = chatDB.child(chatRoomId).child(CHILD_CHAT).push()
				val chatKey = chatRef.key ?: ""
				val chatModel = ChatModel(
					idx = chatKey,
					chatRoomId = chatRoomId,
					senderId = myId,
					email = myEmail,
					nickName = myNickname,
					message = etMessage.text.toString(),
					viewType = 0,
					createAt = System.currentTimeMillis()
				)
				Log.d(MYDEBUG, "initSendButton: $chatModel")
				chatRef.setValue(chatModel)
				etMessage.text.clear()
//				adapter.submitList(chatList)
//				adapter.notifyDataSetChanged()
//				adapter.submitList(chatList)
//				rvChats.scrollToPosition(rvChats.adapter!!.itemCount - 1)
//				rvChats.scrollToPosition(rvChats.adapter!!.itemCount - 1)
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