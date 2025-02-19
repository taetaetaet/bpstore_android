package com.example.bpstore_test.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bpstore_test.R
import com.example.bpstore_test.data.model.User_data
import com.example.bpstore_test.network.retrofit.RetrofitClient
import com.example.bpstore_test.ui.adapter.HistoryAdapter
import com.example.bpstore_test.utils.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var noDataTextView: TextView
    private lateinit var dataCountTextView: TextView // 조회된 건수 표시

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 요소 초기화
        progressBar = view.findViewById(R.id.progressBar)
        noDataTextView = view.findViewById(R.id.noDataTextView)
        dataCountTextView = view.findViewById(R.id.dataCountTextView) // 건수 텍스트뷰 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter()
        recyclerView.adapter = historyAdapter

        fetchUserData()

        // 네비게이션 처리
        setupNavigation(view)
    }

    private fun fetchUserData() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val agentId = sharedPreferencesHelper.getUserId()

        if (agentId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<List<User_data>> = RetrofitClient.instance.getUserBusinessByAgentId(agentId)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val businessList = response.body()?.filter {
                            it.name != null || it.b_num != null || it.tel != null || it.r_num != null
                        } ?: emptyList()

                        // created_at을 Date로 변환 후 정렬
                        val sortedList = businessList.sortedByDescending {
                            it.getCreatedAtDate() // getCreatedAtDate() 메서드를 사용하여 날짜로 정렬
                        }

                        if (sortedList.isEmpty()) {
                            noDataTextView.visibility = View.VISIBLE
                            dataCountTextView.visibility = View.GONE
                        } else {
                            noDataTextView.visibility = View.GONE
                            dataCountTextView.visibility = View.VISIBLE
                            dataCountTextView.text = "조회된 건수: ${sortedList.size}"
                        }

                        historyAdapter.submitList(sortedList)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(requireContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("HistoryFragment", "API 요청 실패: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "네트워크 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HistoryFragment", "Error fetching data", e)
                }
            }
        }
    }

    // 네비게이션 이동 처리
    private fun setupNavigation(view: View) {
        val navItems = mapOf(
            R.id.addressID to R.id.action_historyFragment_to_addressFragment,
            R.id.arpuID to R.id.action_historyFragment_to_arpuFragment,
            R.id.lgID to R.id.action_historyFragment_to_lgFragment,
            R.id.etcID to R.id.action_historyFragment_to_etcFragment,
            R.id.dangerID to R.id.action_historyFragment_to_dangerFragment
        )

        // 각 아이템에 대한 클릭 리스너 설정
        for ((id, action) in navItems) {
            view.findViewById<LinearLayout>(id)?.setOnClickListener {
                findNavController().navigate(action)
            }
        }
    }
}
