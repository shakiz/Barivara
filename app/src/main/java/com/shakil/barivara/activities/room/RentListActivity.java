package com.shakil.barivara.activities.room;

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
import com.shakil.barivara.adapter.RecyclerRentListAdapter;
import com.shakil.barivara.databinding.ActivityRentListBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.utils.FilterManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;

public class RentListActivity extends AppCompatActivity {
    private ActivityRentListBinding activityRentListBinding;
    private RecyclerRentListAdapter recyclerMeterListAdapter;
    private ArrayList<Rent> rentList;
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
        activityRentListBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_list);

        activityRentListBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
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
        rentList = new ArrayList<>();
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        ux = new UX(this);
        tools = new Tools(this);
        filterManager = new FilterManager(this);
        noDataTXT = findViewById(R.id.mNoDataMessage);
    }
    //endregion

    //region perform all UI interactions
    private void bindUIWithComponents(){
        searchName.setHint(getString(R.string.search_month_name));
        //region check internet connection
        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        //endregion

        activityRentListBinding.mAddMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RentListActivity.this, RentDetailsActivity.class));
            }
        });

        //region filter
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    if (!TextUtils.isEmpty(searchName.getText().toString())){
                        filterManager.onFilterClick(searchName.getText().toString(), rentList, new FilterManager.onRentFilterClick() {
                            @Override
                            public void onClick(ArrayList<Rent> objects) {
                                if (objects.size() > 0) {
                                    rentList = objects;
                                    setRecyclerAdapter();
                                    Tools.hideKeyboard(RentListActivity.this);
                                    Toast.makeText(RentListActivity.this, getString(R.string.filterd), Toast.LENGTH_SHORT).show();
                                } else {
                                    Tools.hideKeyboard(RentListActivity.this);
                                    noDataTXT.setVisibility(View.VISIBLE);
                                    noDataTXT.setText(R.string.no_data_message);
                                    activityRentListBinding.mRecylerView.setVisibility(View.GONE);
                                    Toast.makeText(RentListActivity.this, getString(R.string.no_data_message), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(RentListActivity.this, getString(R.string.enter_data_validation), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RentListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    activityRentListBinding.mRecylerView.setVisibility(View.VISIBLE);
                    searchName.setText("");
                    noDataTXT.setVisibility(View.GONE);
                    Tools.hideKeyboard(RentListActivity.this);
                    setData();
                    Toast.makeText(RentListActivity.this, getString(R.string.list_refreshed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RentListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion
    }
    //endregion

    //region set recycler data
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllRent("rent", new FirebaseCrudHelper.onRentDataFetch() {
            @Override
            public void onFetch(ArrayList<Rent> objects) {
                rentList = objects;
                if (rentList.size() <= 0){
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
        recyclerMeterListAdapter = new RecyclerRentListAdapter(rentList);
        activityRentListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityRentListBinding.mRecylerView.setAdapter(recyclerMeterListAdapter);
        recyclerMeterListAdapter.notifyDataSetChanged();
        recyclerMeterListAdapter.setOnItemClickListener(new RecyclerRentListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Rent rent) {
                startActivity(new Intent(RentListActivity.this, RentDetailsActivity.class).putExtra("rent", rent));
            }
        });
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(RentListActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //endregion
}