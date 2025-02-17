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

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitClient.apiService.saveUserBusiness(
                        userId, customerName, businessNumber, billingAccount, "","","","","", 501
                    )
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "저장 성공!", Toast.LENGTH_SHORT).show()
                        Log.e("ErrorFragment", "Response not successful: ${response.errorBody()?.string()}")
                    } else {
                        Toast.makeText(requireContext(), "저장 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.e("ErrorFragment", "Response not successful: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
}