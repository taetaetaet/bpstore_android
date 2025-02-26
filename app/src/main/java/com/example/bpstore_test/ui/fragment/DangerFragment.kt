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

class DangerFragment : Fragment(R.layout.fragment_danger) {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        val dangerName = view.findViewById<TextInputEditText>(R.id.dangerName)
        val bsNumber = view.findViewById<TextInputEditText>(R.id.bsNumber)
        val rNumber = view.findViewById<TextInputEditText>(R.id.rNumber)
        val tel = view.findViewById<TextInputEditText>(R.id.tel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        dangerName.requestFocus()

        // InputFilter: 숫자와 '-'만 허용하는 필터
        val digitAndDashFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!(source[i].isDigit() || source[i] == '-')) {
                    return@InputFilter ""
                }
            }
            null
        }

        // 사업자번호와 전화번호에 필터 적용
        bsNumber.filters = arrayOf(digitAndDashFilter)
        tel.filters = arrayOf(digitAndDashFilter)

        // 법인번호(주민번호)에도 필터 적용
        rNumber.filters = arrayOf(digitAndDashFilter)

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
                val formatted = formatPhoneNumber(cleaned)

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

        // 법인번호/주민번호 자동 포맷 (000000-0000000)
        rNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isEditing) return
                isEditing = true

                val cleaned = s.toString().replace("-", "")
                val formatted = formatRegisterNumber(cleaned)
                rNumber.setText(formatted)
                rNumber.setSelection(formatted.length)
                isEditing = false
            }
            override fun afterTextChanged(s: Editable?) { }
        })

        btnSave.setOnClickListener {
            val dangerNameText = dangerName.text.toString()
            val businessNumber = bsNumber.text.toString()
            val registerNumber = rNumber.text.toString()
            val phoneNumber = tel.text.toString()

            if (dangerNameText.isEmpty() || businessNumber.isEmpty() || registerNumber.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 형식 검증
            val businessNumberPattern = Regex("""^\d{3}-\d{2}-\d{5}$""")
            val registerNumberPattern = Regex("""^\d{6}-\d{7}$""")
            val phoneNumberPattern = Regex("""^(\d{2,3})-(\d{3,4})-(\d{4})$""")

            if (!businessNumber.matches(businessNumberPattern)) {
                Toast.makeText(requireContext(), "올바른 사업자번호 형식을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!registerNumber.matches(registerNumberPattern)) {
                Toast.makeText(requireContext(), "올바른 법인/주민번호 형식을 입력하세요.", Toast.LENGTH_SHORT).show()
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

            // UserBusinessRequest 객체 생성
            val userBusinessRequest = UserBusinessRequest(
                agentid = userId,
                name = dangerNameText,
                b_num = businessNumber,
                c_num = "",
                tel = phoneNumber,
                addr = "",
                detail_addr = "",
                r_num = registerNumber,
                etc = "",
                bz_type = 504
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Retrofit을 통해 서버에 요청
                    val response = RetrofitClient.instance.saveUserBusiness(userBusinessRequest)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "저장 성공!", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(requireContext(), "저장 실패: $errorBody", Toast.LENGTH_SHORT).show()
                            Log.e("DangerFragment", "Response not successful: $errorBody")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 네비게이션 코드
        view.findViewById<LinearLayout>(R.id.dangerID).setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_self)
        }
        view.findViewById<LinearLayout>(R.id.arpuID).setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_arpuFragment)
        }
        view.findViewById<LinearLayout>(R.id.lgID).setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_lgFragment)
        }
        view.findViewById<LinearLayout>(R.id.addressID).setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_addressFragment)
        }
        view.findViewById<LinearLayout>(R.id.etcID).setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_etcFragment)
        }
        view.findViewById<LinearLayout>(R.id.historyID).setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_historyFragment)
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

    // 전화번호 포맷 함수 (010-0000-0000 또는 00-0000-0000)
    private fun formatPhoneNumber(number: String): String {
        return when {
            number.length > 7 -> "${number.take(3)}-${number.substring(3, 7)}-${number.drop(7)}"
            number.length == 7 -> "${number.take(3)}-${number.substring(3, 7)}"
            number.length > 3 -> "${number.take(3)}-${number.drop(3)}"
            else -> number
        }
    }

    // 법인번호/주민번호 포맷 함수 (000000-0000000)
    private fun formatRegisterNumber(number: String): String {
        return when {
            number.length > 6 -> "${number.take(6)}-${number.drop(6)}"
            else -> number
        }
    }
}