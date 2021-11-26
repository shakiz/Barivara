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
import com.shakil.barivara.activities.room.RoomActivity;
import com.shakil.barivara.activities.room.RoomListActivity;
import com.shakil.barivara.adapter.RecyclerMeterListAdapter;
import com.shakil.barivara.adapter.RecyclerRoomListAdapter;
import com.shakil.barivara.databinding.ActivityMeterListBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.utils.FilterManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;

public class MeterListActivity extends AppCompatActivity {
    private ActivityMeterListBinding activityMeterListBinding;
    private ArrayList<Meter> meterList;
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
        searchName.setHint(getString(R.string.search_meter_name));
        //region check internet connection
        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        //endregion

        activityMeterListBinding.mAddMeterMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeterListActivity.this, NewMeterActivity.class));
            }
        });

        //region filter
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    if (!TextUtils.isEmpty(searchName.getText().toString())){
                        filterManager.onFilterClick(searchName.getText().toString(), meterList, new FilterManager.onMeterFilterClick() {
                            @Override
                            public void onClick(ArrayList<Meter> objects) {
                                if (objects.size() > 0) {
                                    meterList = objects;
                                    setRecyclerAdapter();
                                    Tools.hideKeyboard(MeterListActivity.this);
                                    Toast.makeText(MeterListActivity.this, getString(R.string.filterd), Toast.LENGTH_SHORT).show();
                                } else {
                                    Tools.hideKeyboard(MeterListActivity.this);
                                    noDataTXT.setVisibility(View.VISIBLE);
                                    noDataTXT.setText(R.string.no_data_message);
                                    activityMeterListBinding.mRecylerView.setVisibility(View.GONE);
                                    Toast.makeText(MeterListActivity.this, getString(R.string.no_data_message), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(MeterListActivity.this, getString(R.string.enter_data_validation), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MeterListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    activityMeterListBinding.mRecylerView.setVisibility(View.VISIBLE);
                    searchName.setText("");
                    noDataTXT.setVisibility(View.GONE);
                    Tools.hideKeyboard(MeterListActivity.this);
                    setData();
                    Toast.makeText(MeterListActivity.this, getString(R.string.list_refreshed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MeterListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion
    }

    //region recycler adapter
    private void setRecyclerAdapter(){
        RecyclerMeterListAdapter recyclerMeterListAdapter = new RecyclerMeterListAdapter(meterList);
        activityMeterListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityMeterListBinding.mRecylerView.setAdapter(recyclerMeterListAdapter);
        recyclerMeterListAdapter.notifyDataSetChanged();
        recyclerMeterListAdapter.setOnItemClickListener(new RecyclerMeterListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Meter meter) {
                startActivity(new Intent(MeterListActivity.this, NewMeterActivity.class).putExtra("meter", meter));
            }
        });

        recyclerMeterListAdapter.onEditListener(new RecyclerMeterListAdapter.onEditListener() {
            @Override
            public void onEdit(Meter meter) {
                startActivity(new Intent(MeterListActivity.this, NewMeterActivity.class).putExtra("meter", meter));
            }
        });
        recyclerMeterListAdapter.onDeleteListener(new RecyclerMeterListAdapter.onDeleteListener() {
            @Override
            public void onDelete(Meter meter) {
                firebaseCrudHelper.deleteRecord("meter",meter.getFireBaseKey());
                setData();
            }
        });
    }
    //endregion

    //region set recycler data
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllMeter("meter", new FirebaseCrudHelper.onDataFetch() {
            @Override
            public void onFetch(ArrayList<Meter> objects) {
                meterList = objects;
                setRecyclerAdapter();
                ux.removeLoadingView();

                if (meterList.size() <= 0){
                    noDataTXT.setVisibility(View.VISIBLE);
                    noDataTXT.setText(R.string.no_data_message);
                }
            }
        });
    }
    //endregion

    private void init() {
        searchButton = findViewById(R.id.searchButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchName = findViewById(R.id.SearchName);
        meterList = new ArrayList<>();
        ux = new UX(this);
        tools = new Tools(this);
        filterManager = new FilterManager(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        noDataTXT = findViewById(R.id.mNoDataMessage);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MeterListActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
