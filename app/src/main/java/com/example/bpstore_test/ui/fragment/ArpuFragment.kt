package com.example.bpstore_test.ui.fragment

import android.os.Bundle
import android.text.InputFilter
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

class ArpuFragment : Fragment(R.layout.fragment_arpu) {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        val arpuName = view.findViewById<TextInputEditText>(R.id.arpuName)
        val bsNumber = view.findViewById<TextInputEditText>(R.id.bsNumber)
        val cNumber = view.findViewById<TextInputEditText>(R.id.cNumber)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        arpuName.requestFocus()

        // 숫자와 '-'만 허용하는 입력 필터
        val digitAndDashFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!(source[i].isDigit() || source[i] == '-')) {
                    return@InputFilter ""
                }
            }
            null
        }

        // 사업자번호: '-' 제외하고 최대 10자리까지만 입력 허용
        val maxBusinessNumberDigitsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val newVal = dest.substring(0, dstart) + source.subSequence(start, end) + dest.substring(dend)
            if (newVal.filter { it.isDigit() }.length > 10) {
                ""
            } else {
                null
            }
        }

        // 청구계정번호: '-' 제외하고 최대 15자리까지만 입력 허용
        val maxBillingAccountDigitsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val newVal = dest.substring(0, dstart) + source.subSequence(start, end) + dest.substring(dend)
            if (newVal.filter { it.isDigit() }.length > 15) {
                ""
            } else {
                null
            }
        }

        // 필터 적용: 사업자번호는 두 필터, 청구계정번호는 두 필터 적용
        bsNumber.filters = arrayOf(digitAndDashFilter, maxBusinessNumberDigitsFilter)
        cNumber.filters = arrayOf(digitAndDashFilter, maxBillingAccountDigitsFilter)

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

        btnSave.setOnClickListener {
            val customerName = arpuName.text.toString()
            val businessNumber = bsNumber.text.toString()
            val billingAccount = cNumber.text.toString()

            if (customerName.isEmpty() || businessNumber.isEmpty() || billingAccount.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sharedPreferencesHelper.getUserId()
            if (userId == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // UserBusinessRequest 객체 생성 (bz_type 501은 Arpu를 의미)
            val userBusinessRequest = UserBusinessRequest(
                agentid = userId,
                name = customerName,
                b_num = businessNumber,
                c_num = billingAccount,
                tel = "",
                addr = "",
                detail_addr = "",
                r_num = "",
                etc = "",
                bz_type = 501
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
                            Log.e("ArpuFragment", "Response not successful: $errorBody")
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
        val arpuLayout = view.findViewById<LinearLayout>(R.id.arpuID)
        arpuLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_self)
        }
        val lgLayout = view.findViewById<LinearLayout>(R.id.lgID)
        lgLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_lgFragment)
        }
        val addressLayout = view.findViewById<LinearLayout>(R.id.addressID)
        addressLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_addressFragment)
        }
        val dangerLayout = view.findViewById<LinearLayout>(R.id.dangerID)
        dangerLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_dangerFragment)
        }
        val etcLayout = view.findViewById<LinearLayout>(R.id.etcID)
        etcLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_etcFragment)
        }
        val historyLayout = view.findViewById<LinearLayout>(R.id.historyID)
        historyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_historyFragment)
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
}
