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

class AddressFragment : Fragment(R.layout.fragment_address) {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        val addr = view.findViewById<TextInputEditText>(R.id.addr)
        val detailAddr = view.findViewById<TextInputEditText>(R.id.detail_addr)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        addr.requestFocus()

        btnSave.setOnClickListener {
            val address = addr.text.toString()
            val detailAddress = detailAddr.text.toString()

            if (address.isEmpty() || detailAddress.isEmpty()) {
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
                name = "",
                b_num = "",
                c_num = "",
                tel = "",
                addr = address,
                detail_addr = detailAddress,
                r_num = "",
                etc = "",
                bz_type = 503
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
                            Log.e("AddressFragment", "Response not successful: $errorBody")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 네비게이션 처리
        val addressFragment = view.findViewById<LinearLayout>(R.id.addressID)
        addressFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_self)
        }
        val arpuFragment = view.findViewById<LinearLayout>(R.id.arpuID)
        arpuFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_arpuFragment)
        }
        val lgFragment = view.findViewById<LinearLayout>(R.id.lgID)
        lgFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_lgFragment)
        }
        val etcFragment = view.findViewById<LinearLayout>(R.id.etcID)
        etcFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_etcFragment)
        }
        val dangerFragment = view.findViewById<LinearLayout>(R.id.dangerID)
        dangerFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_dangerFragment)
        }
        val historyFragment = view.findViewById<LinearLayout>(R.id.historyID)
        historyFragment.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_historyFragment)
        }
    }
}