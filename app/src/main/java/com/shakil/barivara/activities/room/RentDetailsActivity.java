package com.shakil.barivara.activities.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityNewRentDetailsBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RentDetailsActivity extends AppCompatActivity {
    private ActivityNewRentDetailsBinding activityNewRentDetailsBinding;
    private SpinnerData spinnerData;
    private SpinnerAdapter spinnerAdapter;
    private Rent rent = new Rent();
    private String command = "add";
    private int MonthId = 0, YearId = 0, AssociateRoomId = 0;
    private String MonthStr = "", YearName = "", AssociateRoomStr = "";
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> roomNames;
    private Validation validation;
    private Tools tools;
    private final Map<String, String[]> hashMap = new HashMap();
    private CustomAdManager customAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewRentDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_rent_details);

        //region get intent data
        getIntentData();
        //endregion

        init();
        setSupportActionBar(activityNewRentDetailsBinding.toolBar);

        activityNewRentDetailsBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bindUiWithComponents();

        //region load intent data to UI
        loadData();
        //endregion
    }

    //region get intent data
    private void getIntentData(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getParcelable("rent") != null){
                rent = getIntent().getExtras().getParcelable("rent");
            }
        }
    }
    //endregion

    //region load intent data to UI
    private void loadData(){
        if (rent.getRentId() != null) {
            command = "update";
            activityNewRentDetailsBinding.RentAmount.setText(String.valueOf(rent.getRentAmount()));
            activityNewRentDetailsBinding.MonthId.setSelection(rent.getMonthId(), true);
            activityNewRentDetailsBinding.YearId.setSelection(rent.getYearId(), true);
            activityNewRentDetailsBinding.Note.setText(rent.getNote());
        }
    }
    //endregion

    //region bind ui and perform all UI interactions
    private void bindUiWithComponents() {
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"RentAmount"},
                new String[]{getString(R.string.rent_amount_validation)});
        validation.setSpinnerIsNotEmpty(new String[]{"YearId","MonthId"});
        //endregion

        spinnerAdapter.setSpinnerAdapter(activityNewRentDetailsBinding.MonthId,this, spinnerData.setMonthData());
        spinnerAdapter.setSpinnerAdapter(activityNewRentDetailsBinding.YearId,this, spinnerData.setYearData());

        //region set room spinner
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("room", "roomName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    roomNames = nameList;
                    setRoomSpinner();
                }
            });
        } else {
            roomNames = spinnerData.setSpinnerNoData();
            setRoomSpinner();
        }
        //endregion

        //region spinner on change
        activityNewRentDetailsBinding.MonthId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MonthId = position;
                MonthStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityNewRentDetailsBinding.YearId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                YearId = position;
                YearName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityNewRentDetailsBinding.AssociateRoomId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AssociateRoomId = position;
                AssociateRoomStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        activityNewRentDetailsBinding.mAddRentMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation.isValid()) {
                    if (tools.hasConnection()) {
                        rent.setMonthId(MonthId);
                        rent.setMonthName(MonthStr);
                        rent.setYearId(YearId);
                        rent.setYearName(YearName);
                        rent.setAssociateRoomId(AssociateRoomId);
                        rent.setAssociateRoomName(AssociateRoomStr);
                        rent.setNote(activityNewRentDetailsBinding.Note.getText().toString());
                        rent.setRentAmount(Integer.parseInt(activityNewRentDetailsBinding.RentAmount.getText().toString()));
                        if (command.equals("add")) {
                            rent.setRentId(UUID.randomUUID().toString());
                            firebaseCrudHelper.add(rent, "rent");
                        } else {
                            firebaseCrudHelper.update(rent, rent.getFireBaseKey(), "rent");
                        }
                        Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RentDetailsActivity.this, RentListActivity.class));
                    } else {
                        Toast.makeText(RentDetailsActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    //endregion

    //region init objects
    private void init() {
        roomNames = new ArrayList<>();
        spinnerData = new SpinnerData(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        spinnerAdapter = new SpinnerAdapter();
        tools = new Tools(this);
        validation = new Validation(this, hashMap);
        customAdManager = new CustomAdManager(activityNewRentDetailsBinding.adView, this);
    }
    //endregion

    //region set spinner adapter
    private void setRoomSpinner(){
        ArrayAdapter<String> roomNameSpinnerAdapter = new ArrayAdapter<>(RentDetailsActivity.this, R.layout.spinner_drop, roomNames);
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityNewRentDetailsBinding.AssociateRoomId.setAdapter(roomNameSpinnerAdapter);
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(RentDetailsActivity.this, RentListActivity.class));
    }
    //endregion
}
