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

class HistoryAdapter : ListAdapter<User_data, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User_data>() {
            override fun areItemsTheSame(oldItem: User_data, newItem: User_data): Boolean {
                return oldItem.agentid == newItem.agentid
            }

            override fun areContentsTheSame(oldItem: User_data, newItem: User_data): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val createdAt: TextView = view.findViewById(R.id.created_at)
        val divider: View = view.findViewById(R.id.divider)
        val headline: TextView = view.findViewById(R.id.headline)
        val name: TextView = view.findViewById(R.id.name)
        val businessNumber: TextView = view.findViewById(R.id.b_num)
        val cheong_gu: TextView = view.findViewById(R.id.c_num)
        val phone: TextView = view.findViewById(R.id.tel)
        val corporateNumber: TextView = view.findViewById(R.id.r_num)
        val addr: TextView = view.findViewById(R.id.addr)
        val detailAddr: TextView = view.findViewById(R.id.detail_addr)
        val etc: TextView = view.findViewById(R.id.etc)
    }

    // 데이터를 최신순으로 정렬하여 보여주는 함수 추가
    fun submitSortedList(data: List<User_data>) {
        val sortedList = data.sortedByDescending { it.getCreatedAtDate() }
        submitList(sortedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_business, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = getItem(position)

        // bz_type에 따라 헤드라인 설정
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

        // 각 항목에 대한 조건 체크 후 텍스트 설정
        if (!userData.name.isNullOrEmpty()) {
            holder.name.text = "상호명 : ${userData.name}"
            holder.name.visibility = View.VISIBLE
        } else {
            holder.name.visibility = View.GONE
        }

        if (!userData.b_num.isNullOrEmpty()) {
            holder.businessNumber.text = "사업자번호 : ${userData.b_num}"
            holder.businessNumber.visibility = View.VISIBLE
        } else {
            holder.businessNumber.visibility = View.GONE
        }

        if (!userData.c_num.isNullOrEmpty()) {
            holder.cheong_gu.text = "청구계정번호 : ${userData.c_num}"
            holder.cheong_gu.visibility = View.VISIBLE
        } else {
            holder.cheong_gu.visibility = View.GONE
        }

        if (!userData.r_num.isNullOrEmpty()) {
            holder.corporateNumber.text = "법인번호 : ${userData.r_num}"
            holder.corporateNumber.visibility = View.VISIBLE
        } else {
            holder.corporateNumber.visibility = View.GONE
        }

        if (!userData.tel.isNullOrEmpty()) {
            holder.phone.text = "휴대폰번호 : ${userData.tel}"
            holder.phone.visibility = View.VISIBLE
        } else {
            holder.phone.visibility = View.GONE
        }

        if (!userData.addr.isNullOrEmpty()) {
            holder.addr.text = "주소 : ${userData.addr}"
            holder.addr.visibility = View.VISIBLE
        } else {
            holder.addr.visibility = View.GONE
        }

        if (!userData.detail_addr.isNullOrEmpty()) {
            holder.detailAddr.text = "상세주소 : ${userData.detail_addr}"
            holder.detailAddr.visibility = View.VISIBLE
        } else {
            holder.detailAddr.visibility = View.GONE
        }

        if (!userData.etc.isNullOrEmpty()) {
            holder.etc.text = "기타사항 : ${userData.etc}"
            holder.etc.visibility = View.VISIBLE
        } else {
            holder.etc.visibility = View.GONE
        }

        if (!userData.created_at.isNullOrEmpty()) {
            val date = userData.getCreatedAtDate()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(date)
            holder.createdAt.text = "${formattedDate}"
            holder.createdAt.visibility = View.VISIBLE
        } else {
            holder.createdAt.visibility = View.GONE
        }

        // 경계선 추가
        if (position == itemCount - 1) {
            holder.divider.visibility = View.GONE // 마지막 항목에서 경계선 숨기기
        }
    }
}


