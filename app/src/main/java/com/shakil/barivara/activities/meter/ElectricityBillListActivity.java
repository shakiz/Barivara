package com.shakil.barivara.activities.meter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.RecyclerElectricityBillListAdapter;
import com.shakil.barivara.databinding.ActivityElectricityBillListBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.ElectricityBill;
import com.shakil.barivara.utils.FilterManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;

public class ElectricityBillListActivity extends AppCompatActivity {
    private ActivityElectricityBillListBinding activityMeterCostListBinding;
    private RecyclerElectricityBillListAdapter recyclerBillListAdapter;
    private ArrayList<ElectricityBill> electricityBills;
    private TextView noDataTXT;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;
    private Tools tools;
    private FilterManager filterManager;
    private ImageButton searchButton, refreshButton;
    private EditText searchName;

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

    //region init components
    private void init(){
        searchButton = findViewById(R.id.searchButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchName = findViewById(R.id.SearchName);
        electricityBills = new ArrayList<>();
        ux = new UX(this);
        tools = new Tools(this);
        noDataTXT = findViewById(R.id.mNoDataMessage);
        filterManager = new FilterManager(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
    }
    //endregion

    //region perform all UI interactions
    private void bindUIWithComponents(){
        searchName.setHint(getString(R.string.search_room_name));
        //region check internet connection
        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        //endregion

        activityMeterCostListBinding.mAddBillMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ElectricityBillListActivity.this, ElectricityBillDetailsActivity.class));
            }
        });

        //region filter
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    if (!TextUtils.isEmpty(searchName.getText().toString())){
                        filterManager.onFilterClick(searchName.getText().toString(), electricityBills, new FilterManager.onBillFilterClick() {
                            @Override
                            public void onClick(ArrayList<ElectricityBill> objects) {
                                if (objects.size() > 0) {
                                    electricityBills = objects;
                                    setRecyclerAdapter();
                                    Tools.hideKeyboard(ElectricityBillListActivity.this);
                                    Toast.makeText(ElectricityBillListActivity.this, getString(R.string.filterd), Toast.LENGTH_SHORT).show();
                                } else {
                                    Tools.hideKeyboard(ElectricityBillListActivity.this);
                                    noDataTXT.setVisibility(View.VISIBLE);
                                    noDataTXT.setText(R.string.no_data_message);
                                    activityMeterCostListBinding.mRecylerView.setVisibility(View.GONE);
                                    Toast.makeText(ElectricityBillListActivity.this, getString(R.string.no_data_message), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(ElectricityBillListActivity.this, getString(R.string.enter_data_validation), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ElectricityBillListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    activityMeterCostListBinding.mRecylerView.setVisibility(View.VISIBLE);
                    searchName.setText("");
                    noDataTXT.setVisibility(View.GONE);
                    Tools.hideKeyboard(ElectricityBillListActivity.this);
                    setData();
                    Toast.makeText(ElectricityBillListActivity.this, getString(R.string.list_refreshed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ElectricityBillListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion
    }
    //endregion

    //region set data for recycler
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllElectricityBills("electricityBill", new FirebaseCrudHelper.onElectricityBillDataFetch() {
            @Override
            public void onFetch(ArrayList<ElectricityBill> objects) {
                electricityBills = objects;
                if (electricityBills.size() <= 0){
                    noDataTXT.setVisibility(View.VISIBLE);
                    noDataTXT.setText(R.string.no_data_message);
                }
                setRecyclerAdapter();
                ux.removeLoadingView();
            }
        });
    }
    //endregion

    //region set recycler adapter
    private void setRecyclerAdapter(){
        recyclerBillListAdapter = new RecyclerElectricityBillListAdapter(electricityBills);
        activityMeterCostListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityMeterCostListBinding.mRecylerView.setAdapter(recyclerBillListAdapter);
        recyclerBillListAdapter.notifyDataSetChanged();
        recyclerBillListAdapter.setOnItemClickListener(new RecyclerElectricityBillListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ElectricityBill electricityBill) {
                startActivity(new Intent(ElectricityBillListActivity.this, ElectricityBillDetailsActivity.class).putExtra("electricityBill", electricityBill));
            }
        });

        recyclerBillListAdapter.setOnRemoveClick(new RecyclerElectricityBillListAdapter.onRemoveClick() {
            @Override
            public void itemClick(ElectricityBill electricityBill) {
                firebaseCrudHelper.deleteRecord("electricityBill",electricityBill.getFireBaseKey());
                setData();
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