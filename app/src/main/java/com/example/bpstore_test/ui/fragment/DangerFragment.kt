package com.example.bpstore_test.ui.fragment

import android.os.Bundle
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

        btnSave.setOnClickListener {
            val dangerName = dangerName.text.toString()
            val bsNumber = bsNumber.text.toString()
            val rNumber = rNumber.text.toString()
            val phoneNumber = tel.text.toString()

            if (dangerName.isEmpty() || bsNumber.isEmpty() || rNumber.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
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
                name = dangerName,
                b_num = bsNumber,
                c_num = "",
                tel = phoneNumber,
                addr = "",
                detail_addr = "",
                r_num = rNumber,
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
        val dangerLayout = view.findViewById<LinearLayout>(R.id.dangerID)
        dangerLayout.setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_self)
        }
        val arpuLayout = view.findViewById<LinearLayout>(R.id.arpuID)
        arpuLayout.setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_arpuFragment)
        }
        val lgLayout = view.findViewById<LinearLayout>(R.id.lgID)
        lgLayout.setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_lgFragment)
        }
        val addressLayout = view.findViewById<LinearLayout>(R.id.addressID)
        addressLayout.setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_addressFragment)
        }
        val etcLayout = view.findViewById<LinearLayout>(R.id.etcID)
        etcLayout.setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_etcFragment)
        }
        val historyLayout = view.findViewById<LinearLayout>(R.id.historyID)
        historyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_dangerFragment_to_historyFragment)
        }

    }
}