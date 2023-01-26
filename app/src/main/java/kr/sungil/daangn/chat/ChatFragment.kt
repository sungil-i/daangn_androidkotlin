package kr.sungil.daangn.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.R
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.CHILD_CHAT
import kr.sungil.daangn.AppConfig.Companion.DB_CHATS
import kr.sungil.daangn.AppConfig.Companion.DB_POSTS
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.LoginActivity
import kr.sungil.daangn.LogoutActivity
import kr.sungil.daangn.adapter.ChatRoomAdapter
import kr.sungil.daangn.databinding.FragmentChatBinding
import kr.sungil.daangn.models.ChatRoomModel

class ChatFragment : Fragment(R.layout.fragment_chat) {
	private var binding: FragmentChatBinding? = null
	private val userDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_USERS) }

	//	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_POSTS) }
	//	private val chatDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_CHATS) }
	private lateinit var adapter: ChatRoomAdapter
	private lateinit var listener: Any
	private val chatRoomList = mutableListOf<ChatRoomModel>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Fragment 기본함수: Activity 의 onCreate 함수와 동일함
		super.onViewCreated(view, savedInstanceState)
		val _binding = FragmentChatBinding.bind(view)
		binding = _binding

		initRecyclerView()
		initLogInOutButton()
	}

	private fun initRecyclerView() {
		if (AUTH.currentUser != null) {
			listener = object : ChildEventListener {
				override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
					val chatRoomModel = snapshot.getValue(ChatRoomModel::class.java)
					chatRoomModel ?: return

					// recyclerView 에 추가할 조건을 정해서 추가할 수 있다.
					if (chatRoomModel.sellerNickname.isNotEmpty()) {
						chatRoomList.add(chatRoomModel)
					}
					adapter.submitList(chatRoomList)
					adapter.notifyDataSetChanged()
				}

				override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
					adapter.notifyDataSetChanged()
				}

				override fun onChildRemoved(snapshot: DataSnapshot) {
					adapter.notifyDataSetChanged()
				}

				override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

				override fun onCancelled(error: DatabaseError) {}
			}

			// Firebase 데이터베이스를 연결합니다.
			userDB.child(AUTH.currentUser!!.uid).child(CHILD_CHAT)
				.addChildEventListener(listener as ChildEventListener)
			chatRoomList.clear()
			adapter = ChatRoomAdapter(onItemClicked = {
				val intent = Intent(context, ChatActivity::class.java)
				intent.putExtra("chatRoomId", it.idx)
				intent.putExtra("postId", it.postId)
				intent.putExtra("sellerId", it.sellerId)
				startActivity(intent)
			})
			binding!!.rvChatrooms.adapter = adapter
		}
	}

	private fun initLogInOutButton() {
		// 로그인 버튼 이벤트
		binding!!.ivLogin.setOnClickListener {
			startActivity(Intent(context, LoginActivity::class.java))
		}

		// 로그아웃 버튼 이벤트
		binding!!.ivLogout.setOnClickListener {
			if (AUTH.currentUser != null) {
				startActivity(Intent(context, LogoutActivity::class.java))
			}
		}
	}

	override fun onStart() {
		// 앱이 Reload 했을 때 로그인 인증을 다시 확인한다.
		super.onStart()

		if (AUTH.currentUser == null) {
			// Firebase 로그인 인증값이 없다.
			Toast.makeText(
				context,
				getString(R.string.check_login),
				Toast.LENGTH_LONG
			).show()

			// 상단 Appbar 로그인 정보를 안보이게 한다.
			binding!!.ivLogin.isVisible = true
			binding!!.tvMyid.isVisible = false
			binding!!.tvEmail.isVisible = false
			binding!!.ivLogout.isVisible = false
		} else {
			// 상단 Appbar 로그인 정보를 보이게 설정한다.
			binding!!.ivLogin.isVisible = false
			binding!!.tvMyid.text = AUTH.currentUser!!.email?.split("@")?.get(0) ?: ""
			val strEmail = AUTH.currentUser!!.email?.split("@")?.get(1) ?: ""
			"@$strEmail".also { binding!!.tvEmail.text = it }
			binding!!.tvMyid.isVisible = true
			binding!!.tvEmail.isVisible = true
			binding!!.ivLogout.isVisible = true
		}
	}
}