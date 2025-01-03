package com.shakil.barivara.presentation.generatebill

import android.os.Bundle
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
    @Inject
    lateinit var utilsForAll: UtilsForAll
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
    }

    private fun setRecyclerAdapter() {
        recyclerBillInfoAdapter = RecyclerBillInfoAdapter()
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = recyclerBillInfoAdapter
        //recyclerBillInfoAdapter.setGenerateBillCallBacks(this)
    }
}