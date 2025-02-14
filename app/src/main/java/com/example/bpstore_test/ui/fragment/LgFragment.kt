package com.example.bpstore_test.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.example.bpstore_test.R

class LgFragment : Fragment(R.layout.fragment_lg) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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