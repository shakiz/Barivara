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
import com.shakil.barivara.adapter.RecyclerMeterListAdapter;
import com.shakil.barivara.databinding.ActivityMeterListBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.model.meter.Meter;

import java.util.ArrayList;

public class MeterListActivity extends AppCompatActivity {
    private ActivityMeterListBinding activityMeterListBinding;
    private RecyclerMeterListAdapter recyclerMeterListAdapter;
    private ArrayList<Meter> meterList;
    private TextView noDataTXT;
    private DbHelperParent dbHelperParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMeterListBinding = DataBindingUtil.setContentView(this, R.layout.activity_meter_list);

        init();
        setSupportActionBar(activityMeterListBinding.toolBar);

        activityMeterListBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MeterListActivity.this, MainActivity.class));
            }
        });

        binUiWIthComponents();
    }

    private void binUiWIthComponents() {
        setData();

        recyclerMeterListAdapter = new RecyclerMeterListAdapter(meterList, this);
        activityMeterListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityMeterListBinding.mRecylerView.setAdapter(recyclerMeterListAdapter);
        recyclerMeterListAdapter.notifyDataSetChanged();
        recyclerMeterListAdapter.setOnItemClickListener(new RecyclerMeterListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Meter meter) {
                startActivity(new Intent(MeterListActivity.this, NewMeterActivity.class).putExtra("meter", meter));
            }
        });

        activityMeterListBinding.mAddMeterMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeterListActivity.this, NewMeterActivity.class));
            }
        });
    }

    private void setData() {
        meterList = dbHelperParent.getAllMeterDetails();
        if (meterList.size() <= 0){
            noDataTXT.setVisibility(View.VISIBLE);
            noDataTXT.setText(R.string.no_data_message);
        }
    }

    private void init() {
        meterList = new ArrayList<>();
        dbHelperParent = new DbHelperParent(this);
        noDataTXT = findViewById(R.id.mNoDataMessage);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MeterListActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelperParent.close();
    }
}
