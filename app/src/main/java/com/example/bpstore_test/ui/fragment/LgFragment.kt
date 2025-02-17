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

class LgFragment : Fragment(R.layout.fragment_lg) {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        val bsNumber = view.findViewById<TextInputEditText>(R.id.bsNumber)
        val tel = view.findViewById<TextInputEditText>(R.id.tel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val businessNumber = bsNumber.text.toString()
            val phoneNumber = tel.text.toString()

            if (businessNumber.isEmpty() || phoneNumber.isEmpty()) {
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
                        userId, "", businessNumber, "", phoneNumber,"","","","",502
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
        val lgFragment = view.findViewById<LinearLayout>(R.id.lgID)
        lgFragment.setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_self)
        }
        val arpuFragment = view.findViewById<LinearLayout>(R.id.arpuID)
        arpuFragment.setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_arpuFragment)
        }
        val addressFragment = view.findViewById<LinearLayout>(R.id.addressID)
        addressFragment.setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_addressFragment)
        }
        val etcFragment = view.findViewById<LinearLayout>(R.id.etcID)
        etcFragment.setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_etcFragment)
        }
        val dangerFragment = view.findViewById<LinearLayout>(R.id.dangerID)
        dangerFragment.setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_dangerFragment)
        }
        val historyFragment = view.findViewById<LinearLayout>(R.id.historyID)
        historyFragment.setOnClickListener {
            findNavController().navigate(R.id.action_lgFragment_to_historyFragment)
        }
    }
}
