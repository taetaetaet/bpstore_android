package com.example.bpstore_test.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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
    private lateinit var dataCountTextView: TextView

    // UI 요소
    private lateinit var startDateTextView: TextView
    private lateinit var endDateTextView: TextView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageView
    private lateinit var todayButton: TextView
    private lateinit var thisMonthButton: TextView
    private lateinit var sortSpinner: Spinner

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null
    private var searchQuery: String = ""
    private var sortOption: String = "최신순"
    private var allUserData: List<User_data> = emptyList()

    // 날짜 포맷
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        noDataTextView = view.findViewById(R.id.noDataTextView)
        dataCountTextView = view.findViewById(R.id.dataCountTextView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        // UI 요소 초기화
        startDateTextView = view.findViewById(R.id.startDateTextView)
        endDateTextView = view.findViewById(R.id.endDateTextView)
        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        todayButton = view.findViewById(R.id.todayButton)
        thisMonthButton = view.findViewById(R.id.thisMonthButton)
        sortSpinner = view.findViewById(R.id.sortSpinner)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter()
        recyclerView.adapter = historyAdapter

        // 정렬 옵션 (최신순 / 오래된순) 설정
        val sortOptions = arrayOf("최신순", "오래된순")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sortOptions)
        sortSpinner.adapter = adapter

        // 이전 상태 복원
        restoreSearchState()

        // 날짜 선택 리스너 등록
        startDateTextView.setOnClickListener { openDatePicker(isStartDate = true) }
        endDateTextView.setOnClickListener { openDatePicker(isStartDate = false) }

        // 검색 버튼 클릭 시 필터 적용
        searchButton.setOnClickListener { applyFilters() }

        // "당일" 버튼 클릭 시 오늘 날짜 설정
        todayButton.setOnClickListener { setTodayDate() }

        // "당월" 버튼 클릭 시 이번 달 범위 설정
        thisMonthButton.setOnClickListener { setThisMonthRange() }

        // 정렬 기준 변경 시 필터 적용
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sortOption = sortOptions[position] // 선택된 정렬 방식 저장
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // API 데이터 가져오기
        fetchUserData()
    }

    private fun restoreSearchState() {
        val sharedPreferences = requireContext().getSharedPreferences("HistoryFragmentPrefs", 0)
        val savedStartDate = sharedPreferences.getString("startDate", dateFormat.format(Date()))
        val savedEndDate = sharedPreferences.getString("endDate", dateFormat.format(Date()))
        searchQuery = sharedPreferences.getString("searchQuery", "") ?: ""
        sortOption = sharedPreferences.getString("sortOption", "최신순") ?: "최신순"

        startDateTextView.text = savedStartDate
        endDateTextView.text = savedEndDate
        searchEditText.setText(searchQuery)
        sortSpinner.setSelection(if (sortOption == "최신순") 0 else 1)
    }

    /**
     * 날짜 선택 다이얼로그
     */
    private fun openDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day, 0, 0, 0)
            val selectedDate = cal.time

            if (isStartDate) {
                selectedStartDate = selectedDate
                startDateTextView.text = dateFormat.format(selectedDate)
            } else {
                selectedEndDate = selectedDate
                endDateTextView.text = dateFormat.format(selectedDate)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    /**
     * 검색 상태 저장 (페이지 이동 후 돌아와도 유지)
     */
    private fun saveSearchState() {
        val sharedPreferences = requireContext().getSharedPreferences("HistoryFragmentPrefs", 0)
        with(sharedPreferences.edit()) {
            putString("startDate", startDateTextView.text.toString())
            putString("endDate", endDateTextView.text.toString())
            putString("searchQuery", searchQuery)
            putString("sortOption", sortOption)
            apply()
        }
    }

    /**
     * 검색 필터 적용 및 정렬
     */
    private fun applyFilters() {
        searchQuery = searchEditText.text.toString().trim()

        val filteredData = allUserData.filter {
            val createdAtDate = it.getCreatedAtDate()
            val matchesDate = (selectedStartDate == null || !createdAtDate?.before(selectedStartDate)!!) &&
                    (selectedEndDate == null || !createdAtDate?.after(selectedEndDate)!!)
            val matchesQuery = searchQuery.isEmpty() || it.name?.contains(searchQuery, ignoreCase = true) == true

            matchesDate && matchesQuery
        }.let { list ->
            if (sortOption == "최신순") list.sortedByDescending { it.getCreatedAtDate() }
            else list.sortedBy { it.getCreatedAtDate() }
        }

        historyAdapter.submitList(filteredData)
        dataCountTextView.text = "조회된 건수: ${filteredData.size}"
        saveSearchState()
    }

    /**
     * "당일" 버튼 클릭 시 오늘 날짜 설정
     */
    private fun setTodayDate() {
        val today = Date()
        selectedStartDate = today
        selectedEndDate = today
        startDateTextView.text = dateFormat.format(today)
        endDateTextView.text = dateFormat.format(today)
    }

    /**
     * "당월" 버튼 클릭 시 이번 달 범위 설정
     */
    private fun setThisMonthRange() {
        val calendar = Calendar.getInstance()

        // 이번 달 1일
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        selectedStartDate = calendar.time

        // 이번 달 마지막 날
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        selectedEndDate = calendar.time

        startDateTextView.text = dateFormat.format(selectedStartDate)
        endDateTextView.text = dateFormat.format(selectedEndDate)
    }

    /**
     * API 데이터 가져오기
     */
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
                        allUserData = response.body()?.sortedByDescending { it.getCreatedAtDate() } ?: emptyList()
                        applyFilters()
                    } else {
                        Toast.makeText(requireContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "네트워크 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 네비게이션 설정
     */
    private fun setupNavigation(view: View) {
        val navItems = mapOf(
            R.id.addressID to R.id.action_historyFragment_to_addressFragment,
            R.id.arpuID to R.id.action_historyFragment_to_arpuFragment,
            R.id.lgID to R.id.action_historyFragment_to_lgFragment,
            R.id.etcID to R.id.action_historyFragment_to_etcFragment,
            R.id.dangerID to R.id.action_historyFragment_to_dangerFragment
        )

        for ((id, action) in navItems) {
            view.findViewById<LinearLayout>(id)?.setOnClickListener {
                findNavController().navigate(action)
            }
        }
    }
}
