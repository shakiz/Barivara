package com.shakil.barivara.activities.meter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.RecyclerElectricityBillListAdapter;
import com.shakil.barivara.databinding.ActivityElectricityBillListBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.model.meter.ElectricityBill;

import java.util.ArrayList;

public class ElectricityBillListActivity extends AppCompatActivity {
    private ActivityElectricityBillListBinding activityMeterCostListBinding;
    private RecyclerElectricityBillListAdapter recyclerBillListAdapter;
    private ArrayList<ElectricityBill> electricityBills;
    private TextView noDataTXT;
    private DbHelperParent dbHelperParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMeterCostListBinding = DataBindingUtil.setContentView(this, R.layout.activity_electricity_bill_list);

        activityMeterCostListBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //region init UI and perform all UI interactions
        init();
        bindUIWithComponents();
        //endregion
    }

    private void setData() {
        electricityBills = dbHelperParent.getAllElectricityBills();
        if (electricityBills.size() <= 0){
            noDataTXT.setVisibility(View.VISIBLE);
            noDataTXT.setText(R.string.no_data_message);
        }
    }

    //region init components
    private void init(){
        electricityBills = new ArrayList<>();
        dbHelperParent = new DbHelperParent(this);
        noDataTXT = findViewById(R.id.mNoDataMessage);
    }
    //endregion

    //region perform all UI interactions
    private void bindUIWithComponents(){
        setData();

        recyclerBillListAdapter = new RecyclerElectricityBillListAdapter(electricityBills, this);
        activityMeterCostListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityMeterCostListBinding.mRecylerView.setAdapter(recyclerBillListAdapter);
        recyclerBillListAdapter.notifyDataSetChanged();
        recyclerBillListAdapter.setOnItemClickListener(new RecyclerElectricityBillListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ElectricityBill electricityBill) {
                startActivity(new Intent(ElectricityBillListActivity.this, ElectricityBillDetailsActivity.class).putExtra("electricityBill", electricityBill));
            }
        });

        activityMeterCostListBinding.mAddMeterMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ElectricityBillListActivity.this, ElectricityBillDetailsActivity.class));
            }
        });
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ElectricityBillListActivity.this, MainActivity.class));
    }
    //endregion
}