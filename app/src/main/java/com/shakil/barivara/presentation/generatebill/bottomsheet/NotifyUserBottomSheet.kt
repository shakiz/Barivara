package com.shakil.barivara.presentation.generatebill.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shakil.barivara.R
import com.shakil.barivara.data.model.generatebill.BillHistory
import com.shakil.barivara.databinding.DialogLayoutBillMarkAsPaidBinding
import com.shakil.barivara.databinding.DialogLayoutNotifyUserBinding
import com.shakil.barivara.utils.orZero

class NotifyUserBottomSheet :
    BottomSheetDialogFragment() {

    private var _binding: DialogLayoutNotifyUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var notifyUserListener: NotifyUserListener
    private var billHistory: BillHistory? = null

    companion object {
        private const val ARG_BILL_HISTORY_ITEM = "arg_bill_history_item"

        fun newInstance(billHistory: BillHistory): NotifyUserBottomSheet {
            val fragment = NotifyUserBottomSheet()
            val args = Bundle().apply {
                putParcelable(ARG_BILL_HISTORY_ITEM, billHistory)
            }
            fragment.arguments = args
            return fragment
        }
    }

    interface NotifyUserListener {
        fun sentViaDirectMessage(billHistory: BillHistory?)
        fun sentViaWhatsapp(billHistory: BillHistory?)
    }

    fun setNotifyUserListener(notifyUserListener: NotifyUserListener) {
        this.notifyUserListener = notifyUserListener
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetWithTopHandleStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLayoutNotifyUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billHistory = arguments?.getParcelable(ARG_BILL_HISTORY_ITEM)

        binding.RoomId.text = context?.getString(R.string.x_s, billHistory?.room)
        binding.TenantId.text = context?.getString(R.string.x_s, billHistory?.tenantName)
        binding.TotalBill.text = context?.getString(R.string.x_d, billHistory?.rent)
        binding.btnViaWhatsApp.setOnClickListener {
            notifyUserListener.sentViaWhatsapp(billHistory)
        }
        binding.btnDirectMessage.setOnClickListener {
            notifyUserListener.sentViaDirectMessage(billHistory)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
