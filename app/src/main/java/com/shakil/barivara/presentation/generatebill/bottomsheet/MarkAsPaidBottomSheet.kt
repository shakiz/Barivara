package com.shakil.barivara.presentation.generatebill.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shakil.barivara.R
import com.shakil.barivara.databinding.DialogLayoutBillMarkAsPaidBinding

class MarkAsPaidBottomSheet :
    BottomSheetDialogFragment() {

    private var _binding: DialogLayoutBillMarkAsPaidBinding? = null
    private val binding get() = _binding!!
    private lateinit var markAsPaidListener: MarkAsPaidListener
    private var billId: Int = 0

    interface MarkAsPaidListener {
        fun onMarkAsPaidSubmitted(remarks: String, billId: Int)
    }

    fun setMarkAsPaidListener(billId: Int, markAsPaidListener: MarkAsPaidListener) {
        this.markAsPaidListener = markAsPaidListener
        this.billId = billId
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetWithTopHandleStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLayoutBillMarkAsPaidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitBtn.setOnClickListener {
            markAsPaidListener.onMarkAsPaidSubmitted(binding.remarks.text.toString(), billId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
