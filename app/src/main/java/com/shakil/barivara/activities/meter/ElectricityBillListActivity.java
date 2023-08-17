package com.shakil.barivara.activities.meter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

public class ElectricityBillListActivity extends AppCompatActivity implements RecyclerElectricityBillListAdapter.ElectricityBillBacks {
    private ActivityElectricityBillListBinding activityMeterCostListBinding;
    private ArrayList<ElectricityBill> electricityBills;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;
    private Tools tools;
    private FilterManager filterManager;
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

        init();
        bindUIWithComponents();
    }

    private void init(){
        searchName = findViewById(R.id.SearchName);
        electricityBills = new ArrayList<>();
        ux = new UX(this);
        tools = new Tools(this);
        filterManager = new FilterManager();
        firebaseCrudHelper = new FirebaseCrudHelper(this);
    }

    private void bindUIWithComponents(){
        searchName.setHint(getString(R.string.search_room_name));

        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }

        activityMeterCostListBinding.mAddBillMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ElectricityBillListActivity.this, ElectricityBillDetailsActivity.class));
            }
        });

        activityMeterCostListBinding.searchLayout.searchButton.setOnClickListener(new View.OnClickListener() {
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
                                    activityMeterCostListBinding.mNoDataMessage.setVisibility(View.VISIBLE);
                                    activityMeterCostListBinding.mNoDataMessage.setText(R.string.no_data_message);
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

        activityMeterCostListBinding.searchLayout.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    activityMeterCostListBinding.mRecylerView.setVisibility(View.VISIBLE);
                    searchName.setText("");
                    activityMeterCostListBinding.mNoDataMessage.setVisibility(View.GONE);
                    Tools.hideKeyboard(ElectricityBillListActivity.this);
                    setData();
                    Toast.makeText(ElectricityBillListActivity.this, getString(R.string.list_refreshed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ElectricityBillListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllElectricityBills("electricityBill", new FirebaseCrudHelper.onElectricityBillDataFetch() {
            @Override
            public void onFetch(ArrayList<ElectricityBill> objects) {
                electricityBills = objects;
                if (electricityBills.size() <= 0){
                    activityMeterCostListBinding.mNoDataMessage.setVisibility(View.VISIBLE);
                    activityMeterCostListBinding.mNoDataMessage.setText(R.string.no_data_message);
                }
                setRecyclerAdapter();
                ux.removeLoadingView();
            }
        });
    }

    private void setRecyclerAdapter(){
        RecyclerElectricityBillListAdapter recyclerBillListAdapter = new RecyclerElectricityBillListAdapter(electricityBills);
        activityMeterCostListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityMeterCostListBinding.mRecylerView.setAdapter(recyclerBillListAdapter);
        recyclerBillListAdapter.setElectricityBillBack(this);
    }

    private void doPopUpForDeleteConfirmation(ElectricityBill electricityBill){
        Button cancel, delete;
        Dialog dialog = new Dialog(ElectricityBillListActivity.this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        cancel = dialog.findViewById(R.id.cancelButton);
        delete = dialog.findViewById(R.id.deleteButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseCrudHelper.deleteRecord("electricityBill",electricityBill.getFireBaseKey());
                dialog.dismiss();
                setData();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ElectricityBillListActivity.this, MainActivity.class));
    }

    @Override
    public void onDelete(ElectricityBill electricityBill) {
        doPopUpForDeleteConfirmation(electricityBill);
    }

    @Override
    public void onItemClick(ElectricityBill electricityBill) {
        startActivity(new Intent(ElectricityBillListActivity.this, ElectricityBillDetailsActivity.class).putExtra("electricityBill", electricityBill));
    }
}