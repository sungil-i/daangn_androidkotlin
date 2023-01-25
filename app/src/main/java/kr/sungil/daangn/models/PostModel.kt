package kr.sungil.daangn.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostModel(
	var idx: String?,
	var sellerId: String?,
	var title: String?,
	var createdAt: Long?,
	var price: Int?,
	var imageUrl: String?,
	var detail: String?
) : Parcelable, Cloneable {

	// Firebase 연결을 위해 생성자가 필요합니다.
	constructor() : this("", "", "", 0, 0, "", "")

	// 데이터 수정 Update 를 위해 Json 형태의 Map 으로 변환합니다.
	@Exclude
	fun toMap(): Map<String, Any?> {
		return mapOf(
			"idx" to idx,
			"sellerId" to sellerId,
			"title" to title,
			"createdAt" to createdAt,
			"price" to price,
			"imageUrl" to imageUrl,
			"detail" to detail
		)
	}

	// 객체 깊은 복사
	override fun clone(): PostModel = super.clone() as PostModel
}