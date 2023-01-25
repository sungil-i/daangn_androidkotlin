package kr.sungil.daangn.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kr.sungil.daangn.databinding.ActivityChatBinding
import kr.sungil.daangn.home.ViewPostActivity
import kr.sungil.daangn.models.ChatModel
import kr.sungil.daangn.models.PostModel
import java.text.NumberFormat

class ChatActivity : AppCompatActivity() {
	private lateinit var binding: ActivityChatBinding
	private val userDB: DatabaseReference by lazy { Firebase.database.reference.child(AppConfig.DB_USERS) }
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(AppConfig.DB_POSTS) }
	private val chatDB: DatabaseReference by lazy { Firebase.database.reference.child(AppConfig.DB_CHATS) }
	private lateinit var adapter: ChatAdapter
	private lateinit var listener: Any
	private val chatList = mutableListOf<ChatModel>()
	private lateinit var chatRoomId: String
	private lateinit var postId: String
	private lateinit var sellerId: String
	private lateinit var myId: String

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

		initCardView()
		initCancelButton()
	}

	private fun initCardView() {
		postDB.child(postId).get().addOnSuccessListener {
			val postModel = it.getValue(PostModel::class.java)
			postModel ?: return@addOnSuccessListener

			binding.apply {
				val priceFormat = NumberFormat.getInstance()

				tvCvTitle.text = postModel.title
				tvCvPrice.text = "${priceFormat.format(postModel.price)}원"

				if (postModel.imageUrl.isNotEmpty()) {
					Glide.with(ivCvPhoto.context)
						.load(postModel.imageUrl)
						.into(ivCvPhoto)
				}

				tvCvTitle.setOnClickListener {
					val intent = Intent(applicationContext, ViewPostActivity::class.java)
					intent.putExtra("idx", postId)
					startActivity(intent)
					finish()
				}
			}
		}.addOnFailureListener {
			Log.d(MYDEBUG, "initCardView: $it")
		}
		/*binding.apply {
			val priceFormat = NumberFormat.getInstance()

			tvCvTitle.text = postModel.title
			tvCvPrice.text = "${priceFormat.format(postModel.price)}원"

			if (postModel.imageUrl.isNotEmpty()) {
				Glide.with(ivCvPhoto.context)
					.load(postModel.imageUrl)
					.into(ivCvPhoto)
			}
		}*/
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