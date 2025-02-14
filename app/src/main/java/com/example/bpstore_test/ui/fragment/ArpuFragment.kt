package com.example.bpstore_test.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.example.bpstore_test.R

class ArpuFragment : Fragment(R.layout.fragment_arpu) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)







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
        val etcLayout = view.findViewById<LinearLayout>(R.id.etcID)
        etcLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_etcFragment)
        }
        val dangerLayout = view.findViewById<LinearLayout>(R.id.dangerID)
        dangerLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_dangerFragment)
        }
        val historyLayout = view.findViewById<LinearLayout>(R.id.historyID)
        historyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_arpuFragment_to_historyFragment)
        }
    }
}
