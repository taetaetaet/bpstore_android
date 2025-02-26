package com.example.bpstore_test.ui.fragment

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bpstore_test.R
import com.example.bpstore_test.data.model.UserBusinessRequest
import com.example.bpstore_test.network.retrofit.RetrofitClient
import com.example.bpstore_test.utils.SharedPreferencesHelper
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LgFragment : Fragment(R.layout.fragment_lg) {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        val bsNumber = view.findViewById<TextInputEditText>(R.id.bsNumber)
        val tel = view.findViewById<TextInputEditText>(R.id.tel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        bsNumber.requestFocus()

        // InputFilter: 오직 숫자와 '-'만 허용 (자동 포맷팅 시 '-' 삽입됨)
        val digitAndDashFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!(source[i].isDigit() || source[i] == '-')) {
                    return@InputFilter ""
                }
            }
            null
        }
        bsNumber.filters = arrayOf(digitAndDashFilter)
        tel.filters = arrayOf(digitAndDashFilter)

        // 사업자번호 자동 포맷 (000-00-00000)
        bsNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isEditing) return
                isEditing = true

                val cleaned = s.toString().replace("-", "")
                val formatted = formatBusinessNumber(cleaned)
                bsNumber.setText(formatted)
                bsNumber.setSelection(formatted.length)
                isEditing = false
            }
            override fun afterTextChanged(s: Editable?) { }
        })

        // 전화번호 자동 포맷 (010-0000-0000 또는 00-0000-0000)
        tel.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            private var previousText = ""
            private var previousCursor = 0
            private var isDeleting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousText = s?.toString() ?: ""
                previousCursor = tel.selectionStart
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isEditing) return
                isEditing = true

                val cleaned = s.toString().replace("-", "")
                // cleaned 길이가 7 이상이면 3-4-나머지 형식 적용 (예: 010-0000-0000)
                val formatted = when {
                    cleaned.length > 7 -> "${cleaned.take(3)}-${cleaned.substring(3, 7)}-${cleaned.drop(7)}"
                    cleaned.length == 7 -> "${cleaned.take(3)}-${cleaned.substring(3, 7)}"
                    cleaned.length > 3 -> "${cleaned.take(3)}-${cleaned.drop(3)}"
                    else -> cleaned
                }

                // 삭제 시: 커서 바로 앞에 '-'가 있다면 커서를 한 칸 왼쪽으로 이동
                val newCursor = if (isDeleting && previousCursor > 0 && previousText.getOrNull(previousCursor - 1) == '-') {
                    previousCursor - 1
                } else {
                    formatted.length
                }

                tel.setText(formatted)
                tel.setSelection(newCursor.coerceIn(0, formatted.length))
                isEditing = false
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        btnSave.setOnClickListener {
            val businessNumber = bsNumber.text.toString()
            val phoneNumber = tel.text.toString()

            // 기존 형식 검증 (사업자번호: 000-00-00000, 전화번호: (2~3자리)-(3~4자리)-(4자리))
            val businessNumberPattern = Regex("""^\d{3}-\d{2}-\d{5}$""")
            val phoneNumberPattern = Regex("""^(\d{2,3})-(\d{3,4})-(\d{4})$""")

            if (!businessNumber.matches(businessNumberPattern)) {
                Toast.makeText(requireContext(), "올바른 사업자번호 형식을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!phoneNumber.matches(phoneNumberPattern)) {
                Toast.makeText(requireContext(), "올바른 전화번호 형식을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sharedPreferencesHelper.getUserId()
            if (userId == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userBusinessRequest = UserBusinessRequest(
                agentid = userId,
                name = "",
                b_num = businessNumber,
                c_num = "",
                tel = phoneNumber,
                addr = "",
                detail_addr = "",
                r_num = "",
                etc = "",
                bz_type = 502
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.saveUserBusiness(userBusinessRequest)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "저장 성공!", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(requireContext(), "저장 실패: $errorBody", Toast.LENGTH_SHORT).show()
                            Log.e("LgFragment", "Response not successful: $errorBody")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 네비게이션 코드: 각 레이아웃 클릭 시 지정한 액션으로 이동
        view.findViewById<LinearLayout>(R.id.lgID).setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_self)
        }
        view.findViewById<LinearLayout>(R.id.arpuID).setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_arpuFragment)
        }
        view.findViewById<LinearLayout>(R.id.addressID).setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_addressFragment)
        }
        view.findViewById<LinearLayout>(R.id.etcID).setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_etcFragment)
        }
        view.findViewById<LinearLayout>(R.id.dangerID).setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_dangerFragment)
        }
        view.findViewById<LinearLayout>(R.id.historyID).setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_historyFragment)
        }
    }

    // 사업자번호 포맷 함수 (000-00-00000)
    private fun formatBusinessNumber(number: String): String {
        return when {
            number.length > 5 -> "${number.take(3)}-${number.substring(3, 5)}-${number.drop(5)}"
            number.length > 3 -> "${number.take(3)}-${number.drop(3)}"
            else -> number
        }
    }

    // 전화번호 포맷 함수: cleaned number 길이가 7 이상이면 3-4-나머지 형식 적용
    private fun formatPhoneNumber(number: String): String {
        return when {
            number.length > 7 -> "${number.take(3)}-${number.substring(3, 7)}-${number.drop(7)}"
            number.length == 7 -> "${number.take(3)}-${number.substring(3, 7)}"
            number.length > 3 -> "${number.take(3)}-${number.drop(3)}"
            else -> number
        }
    }
}
