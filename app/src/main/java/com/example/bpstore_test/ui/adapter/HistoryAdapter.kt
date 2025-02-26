package com.example.bpstore_test.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bpstore_test.R
import com.example.bpstore_test.data.model.User_data
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * RecyclerView의 데이터를 관리하는 어댑터.
 * ListAdapter를 확장하여 데이터 변경 시 자동으로 비교 후 UI 갱신 처리.
 */
class HistoryAdapter : ListAdapter<User_data, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        /**
         * DiffUtil을 사용하여 데이터 변경을 감지하는 콜백.
         * - areItemsTheSame(): 같은 객체인지 비교 (agentid 기준)
         * - areContentsTheSame(): 객체의 내용이 같은지 비교
         */
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User_data>() {
            override fun areItemsTheSame(oldItem: User_data, newItem: User_data): Boolean {
                return oldItem.agentid == newItem.agentid
            }

            override fun areContentsTheSame(oldItem: User_data, newItem: User_data): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * ViewHolder: 개별 리스트 아이템의 UI 요소를 바인딩하는 클래스
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val createdAt: TextView = view.findViewById(R.id.created_at) // 생성 날짜
        val divider: View = view.findViewById(R.id.divider) // 아이템 구분선
        val headline: TextView = view.findViewById(R.id.headline) // 조회 유형
        val name: TextView = view.findViewById(R.id.name) // 상호명
        val businessNumber: TextView = view.findViewById(R.id.b_num) // 사업자번호
        val cheong_gu: TextView = view.findViewById(R.id.c_num) // 청구계정번호
        val phone: TextView = view.findViewById(R.id.tel) // 휴대폰번호
        val corporateNumber: TextView = view.findViewById(R.id.r_num) // 법인번호
        val addr: TextView = view.findViewById(R.id.addr) // 주소
        val detailAddr: TextView = view.findViewById(R.id.detail_addr) // 상세주소
        val etc: TextView = view.findViewById(R.id.etc) // 기타사항
    }

    /**
     * 데이터를 최신순으로 정렬하여 UI에 업데이트하는 함수
     */
    fun submitSortedList(data: List<User_data>) {
        val sortedList = data.sortedByDescending { it.getCreatedAtDate() } // 최신 데이터가 상단에 오도록 정렬
        submitList(sortedList) // RecyclerView에 반영
    }

    /**
     * 아이템을 위한 ViewHolder 생성 (RecyclerView의 한 줄을 어떻게 그릴지 설정)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_business, parent, false) // 아이템 레이아웃 적용
        return ViewHolder(view)
    }

    /**
     * ViewHolder와 데이터를 바인딩하여 UI를 업데이트하는 함수
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = getItem(position) // 현재 아이템 데이터 가져오기

        // bz_type에 따라 조회 유형(헤드라인) 설정
        when (userData.bz_type) {
            501 -> {
                holder.headline.text = "ARPU 재약정 조회"
                holder.headline.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.color_arpu))
            }
            502 -> {
                holder.headline.text = "LG 사용여부 확인"
                holder.headline.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.color_lg))
            }
            503 -> {
                holder.headline.text = "가용조회"
                holder.headline.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.color_utility))
            }
            504 -> {
                holder.headline.text = "고위험군 조회"
                holder.headline.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.color_high_risk))
            }
            505 -> {
                holder.headline.text = "기타문의"
                holder.headline.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.color_etc))
            }
            else -> {
                holder.headline.text = "기타"
                holder.headline.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.color_default))
            }
        }

        /**
         * 개별 데이터가 존재하는 경우 UI에 표시, 없으면 해당 항목 숨김 처리
         */
        holder.name.text = "상호명 : ${userData.name}"
        holder.name.visibility = if (!userData.name.isNullOrEmpty()) View.VISIBLE else View.GONE

        holder.businessNumber.text = "사업자번호 : ${userData.b_num}"
        holder.businessNumber.visibility = if (!userData.b_num.isNullOrEmpty()) View.VISIBLE else View.GONE

        holder.cheong_gu.text = "청구계정번호 : ${userData.c_num}"
        holder.cheong_gu.visibility = if (!userData.c_num.isNullOrEmpty()) View.VISIBLE else View.GONE

        holder.corporateNumber.text = "법인번호 : ${userData.r_num}"
        holder.corporateNumber.visibility = if (!userData.r_num.isNullOrEmpty()) View.VISIBLE else View.GONE

        holder.phone.text = "휴대폰번호 : ${userData.tel}"
        holder.phone.visibility = if (!userData.tel.isNullOrEmpty()) View.VISIBLE else View.GONE

        holder.addr.text = "주소 : ${userData.addr}"
        holder.addr.visibility = if (!userData.addr.isNullOrEmpty()) View.VISIBLE else View.GONE

        holder.detailAddr.text = "상세주소 : ${userData.detail_addr}"
        holder.detailAddr.visibility = if (!userData.detail_addr.isNullOrEmpty()) View.VISIBLE else View.GONE

        holder.etc.text = "기타사항 : ${userData.etc}"
        holder.etc.visibility = if (!userData.etc.isNullOrEmpty()) View.VISIBLE else View.GONE

        /**
         * created_at을 사용하여 날짜 포맷 후 표시
         */
        if (!userData.created_at.isNullOrEmpty()) {
            val date = userData.getCreatedAtDate() // 문자열을 Date 타입으로 변환
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(date) // 날짜 포맷 적용
            holder.createdAt.text = formattedDate
            holder.createdAt.visibility = View.VISIBLE
        } else {
            holder.createdAt.visibility = View.GONE
        }

        /**
         * 마지막 아이템이면 구분선 숨기기 (마지막 줄은 구분선 없음)
         */
        if (position == itemCount - 1) {
            holder.divider.visibility = View.GONE
        }
    }
}
