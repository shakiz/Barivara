package com.shakil.barivara.presentation.generatebill

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityGeneratedBillHistoryBinding
import com.shakil.barivara.presentation.adapter.RecyclerBillInfoAdapter
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeneratedBillHistoryActivity : BaseActivity<ActivityGeneratedBillHistoryBinding>() {
    private lateinit var activityBinding: ActivityGeneratedBillHistoryBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var year: Int = 0
    private var month: Int = 0
    private var validation = Validation(this, hashMap)
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private lateinit var utilsForAll: UtilsForAll
    private lateinit var ux: UX

    @Inject
    lateinit var prefManager: PrefManager
    private lateinit var recyclerBillInfoAdapter: RecyclerBillInfoAdapter

    private val viewModel by viewModels<GenerateBillViewModel>()

    override val layoutResourceId: Int
        get() = R.layout.activity_generated_bill_history

    override fun setVariables(dataBinding: ActivityGeneratedBillHistoryBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewed(ScreenNameConstants.appSreenGenerateBill)
        init()
        binUIWithComponents()
        initObservers()
        setRecyclerAdapter()
    }

    private fun init() {
        ux = UX(this)
        utilsForAll = UtilsForAll(this)
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun binUIWithComponents() {
        activityBinding.toolBar.setNavigationOnClickListener { finish() }

        activityBinding.searchLayout.refreshButton.setOnClickListener { }

        activityBinding.searchLayout.filterButton.setOnClickListener {
            showExtendedSearchLayout()
        }
    }

    private fun setRecyclerAdapter() {
        recyclerBillInfoAdapter = RecyclerBillInfoAdapter()
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = recyclerBillInfoAdapter
        //recyclerBillInfoAdapter.setGenerateBillCallBacks(this)
    }

    private fun showExtendedSearchLayout() {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.extended_search_panel_with_filter_items, null)
        val primaryButton = view.findViewById<Button>(R.id.primaryAction)
        val secondaryAction = view.findViewById<Button>(R.id.secondaryAction)
        val yearSpinner = view.findViewById<Spinner>(R.id.YearId)
        val monthSpinner = view.findViewById<Spinner>(R.id.MonthId)

        val dialog = Dialog(this, R.style.CustomDialogTheme).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.setDimAmount(0.5f)
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        dialog.setContentView(view)

        primaryButton.setOnClickListener {
            if (validation.isValid) {
                // TODO - Filter with Year and Month
            }
        }
        secondaryAction.setOnClickListener {
            dialog.dismiss()
        }

        validation.setSpinnerIsNotEmpty(arrayOf("YearId", "MonthId"))
        spinnerAdapter.setSpinnerAdapter(
            yearSpinner,
            this,
            spinnerData.setYearData()
        )
        spinnerAdapter.setSpinnerAdapter(
            monthSpinner,
            this,
            spinnerData.setMonthData(year)
        )
        dialog.show()
    }
}