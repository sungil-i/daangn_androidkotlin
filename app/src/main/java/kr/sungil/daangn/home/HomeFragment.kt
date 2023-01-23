package kr.sungil.daangn.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.sungil.daangn.AppConfig
import kr.sungil.daangn.AppConfig.Companion.AUTH
import kr.sungil.daangn.AppConfig.Companion.DB_POSTS
import kr.sungil.daangn.AppConfig.Companion.DB_USERS
import kr.sungil.daangn.AppConfig.Companion.MYDEBUG
import kr.sungil.daangn.LoginActivity
import kr.sungil.daangn.LogoutActivity
import kr.sungil.daangn.R
import kr.sungil.daangn.adapter.PostAdapter
import kr.sungil.daangn.databinding.FragmentHomeBinding
import kr.sungil.daangn.models.PostModel

class HomeFragment : Fragment(R.layout.fragment_home) {
	private var binding: FragmentHomeBinding? = null
	private val postDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_POSTS) }
	private val userDB: DatabaseReference by lazy { Firebase.database.reference.child(DB_USERS) }
	private lateinit var adapter: PostAdapter
	private lateinit var listener: Any
	private val postList = mutableListOf<PostModel>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Fragment 기본함수: Activity 의 onCreate 함수와 동일함
		super.onViewCreated(view, savedInstanceState)
		val _binding = FragmentHomeBinding.bind(view)
		binding = _binding

		initLogInOutButton()
		initFloatingActionButton()
		initRecyclerView()
	}

	private fun initLogInOutButton() {
		binding!!.apply {
			// 로그인 버튼 이벤트
			ivLogin.setOnClickListener {
				startActivity(Intent(context, LoginActivity::class.java))
			}

			// 로그아웃 버튼 이벤트
			ivLogout.setOnClickListener {
				if (AUTH.currentUser != null) {
					startActivity(Intent(context, LogoutActivity::class.java))
				}
			}
		}
	}

	private fun initFloatingActionButton() {
		// FloatingActionButton 클릭 이벤트
		binding!!.apply {
			fabAdd.setOnClickListener {
				if (AUTH.currentUser != null) {
					// 로그인을 했을 경우만 사용할 수 있다.
					val intent = Intent(context, AddPostActivity::class.java)
					startActivity(intent)
				} else {
					// Firebase 로그인 인증값이 없다.
					Toast.makeText(
						context,
						getString(R.string.check_login),
						Toast.LENGTH_LONG
					).show()
				}
			}
		}
	}

	private fun initRecyclerView() {
		listener = object : ChildEventListener {
			override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
				val postModel = snapshot.getValue(PostModel::class.java)
				postModel ?: return

				postList.add(postModel)
				adapter.submitList(postList)
			}

			override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
			override fun onChildRemoved(snapshot: DataSnapshot) {}
			override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
			override fun onCancelled(error: DatabaseError) {}
		}

		postDB.addChildEventListener(listener as ChildEventListener)
		postList.clear()
		adapter = PostAdapter(onItemClicked = {
			if (AUTH.currentUser != null) { // 로그인을 한 경우
				if (it.sellerId == AUTH.currentUser?.uid) { // 내가 올린 글
					// 내 글 수정하기 창을 띄웁니다.
					val intent = Intent(context, ModifyPostActivity::class.java)
					intent.putExtra("postModel", it)
					intent.putExtra("idx", it.idx)
					startActivity(intent)
				} else { // 다른 사람이 올린 글
					// 글을 보는 창을 띄웁니다.
					Toast.makeText(
						context,
						"다른 사람의 글입니다. 보기창 띄우기",
						Toast.LENGTH_LONG
					).show()
				}
			} else { // 로그인을 하지 않은 경우
				Toast.makeText(
					context,
					getString(R.string.check_login),
					Toast.LENGTH_LONG
				).show()
			}
		})
		binding?.rvPosts?.adapter = adapter
	}

	override fun onStart() {
		// 앱이 Reload 했을 때 로그인 인증을 다시 확인한다.
		super.onStart()
		adapter.notifyDataSetChanged()

		binding!!.apply {
			if (AUTH.currentUser == null) {
				// Firebase 로그인 인증값이 없다.
				Toast.makeText(
					context,
					getString(R.string.check_login),
					Toast.LENGTH_LONG
				).show()

				// 상단 Appbar 로그인 정보를 안보이게 한다.
				ivLogin.isVisible = true
				tvMyid.isVisible = false
				tvEmail.isVisible = false
				ivLogout.isVisible = false
			} else {
				// 상단 Appbar 로그인 정보를 보이게 설정한다.
				ivLogin.isVisible = false
				tvMyid.text = AUTH.currentUser!!.email?.split("@")?.get(0) ?: ""
				val strEmail = AUTH.currentUser!!.email?.split("@")?.get(1) ?: ""
				"@$strEmail".also { tvEmail.text = it }
				tvMyid.isVisible = true
				tvEmail.isVisible = true
				ivLogout.isVisible = true
			}
		}
	}

	override fun onResume() {
		super.onResume()
		// Reload 할 때 RecyclerView 를 다시 읽습니다.
		adapter.notifyDataSetChanged()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		// 창을 닫을 때 Firebase 연결을 삭제합니다
		postDB.removeEventListener(listener as ChildEventListener)
	}
}