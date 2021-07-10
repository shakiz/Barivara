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
import com.shakil.barivara.databinding.ActivityAddNewRoomBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomActivity extends AppCompatActivity {
    private ActivityAddNewRoomBinding activityAddNewRoomBinding;
    private SpinnerData spinnerData;
    private SpinnerAdapter spinnerAdapter;
    private String roomNameStr, startMonthStr,associateMeterStr,tenantNameStr;
    private int StartMonthId, AssociateMeterId, TenantId;
    private Room room = new Room();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> meterNames, tenantNames;
    private ArrayAdapter<String> meterNameSpinnerAdapter, tenantNameSpinnerAdapter;
    private Validation validation;
    private Tools tools;
    private Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNewRoomBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_room);

        //region get intent data
        getIntentData();
        //endregion

        init();
        setSupportActionBar(activityAddNewRoomBinding.toolBar);

        activityAddNewRoomBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bindUIWithComponents();

        //region load intent data to UI
        loadData();
        //endregion
    }

    //region get intent data
    private void getIntentData(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getParcelable("room") != null){
                room = getIntent().getExtras().getParcelable("room");
            }
        }
    }
    //endregion

    //region load intent data to UI
    private void loadData(){
        if (room.getRoomId() != null) {
            command = "update";
            activityAddNewRoomBinding.RoomName.setText(room.getRoomName());
            activityAddNewRoomBinding.StartMonthId.setSelection(room.getStartMonthId(), true);
        }
    }
    //endregion

    private void bindUIWithComponents() {
        spinnerAdapter.setSpinnerAdapter(activityAddNewRoomBinding.StartMonthId,this,spinnerData.setMonthData());

        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"RoomName"},
                new String[]{getString(R.string.room_name_validation)});
        validation.setSpinnerIsNotEmpty(new String[]{"StartMonthId"});
        //endregion

        //region set meter spinner
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("meter", "meterName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    meterNames = nameList;
                    setMeterSpinner();

                    if (room.getRoomId() != null) {
                        activityAddNewRoomBinding.AssociateMeterId.setSelection(room.getAssociateMeterId(), true);
                    }
                }
            });
        } else {
            meterNames = spinnerData.setSpinnerNoData();
            setMeterSpinner();
        }
        //endregion

        //region set tenant spinner
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("tenant", "tenantName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    tenantNames = nameList;
                    setTenantSpinner();

                    if (room.getRoomId() != null) {
                        activityAddNewRoomBinding.TenantNameId.setSelection(room.getTenantNameId(), true);
                    }
                }
            });
        } else {
            tenantNames = spinnerData.setSpinnerNoData();
            setTenantSpinner();
        }
        //endregion

        //region month and meter selection spinner
        activityAddNewRoomBinding.StartMonthId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startMonthStr = parent.getItemAtPosition(position).toString();
                StartMonthId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityAddNewRoomBinding.AssociateMeterId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                associateMeterStr = parent.getItemAtPosition(position).toString();
                AssociateMeterId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityAddNewRoomBinding.TenantNameId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tenantNameStr = parent.getItemAtPosition(position).toString();
                TenantId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        //region save or update click listener
        activityAddNewRoomBinding.mSaveRoomMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation.isValid()){
                    if (tools.hasConnection()) {
                        saveOrUpdateData();
                    } else {
                        Toast.makeText(RoomActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //endregion
    }

    //region init all objects
    private void init() {
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        tools = new Tools(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        validation = new Validation(this, hashMap);
    }
    //endregion

    //region set tenant spinner
    private void setTenantSpinner(){
        tenantNameSpinnerAdapter = new ArrayAdapter<>(RoomActivity.this, R.layout.spinner_drop, tenantNames);
        tenantNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityAddNewRoomBinding.TenantNameId.setAdapter(tenantNameSpinnerAdapter);
    }
    //endregion

    //region set tenant spinner
    private void setMeterSpinner(){
        meterNameSpinnerAdapter = new ArrayAdapter<>(RoomActivity.this, R.layout.spinner_drop, meterNames);
        meterNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityAddNewRoomBinding.AssociateMeterId.setAdapter(meterNameSpinnerAdapter);
    }
    //endregion

    //region save all data
    private void saveOrUpdateData(){
        roomNameStr = activityAddNewRoomBinding.RoomName.getText().toString();
        room.setRoomName(roomNameStr);
        room.setStartMonthName(startMonthStr);
        room.setStartMonthId(StartMonthId);
        room.setAssociateMeterName(associateMeterStr);
        room.setAssociateMeterId(AssociateMeterId);
        room.setTenantName(tenantNameStr);
        room.setTenantNameId(TenantId);
        if (command.equals("add")) {
            room.setRoomId(UUID.randomUUID().toString());
            firebaseCrudHelper.add(room, "room");
        } else {
            firebaseCrudHelper.update(room, room.getFireBaseKey(), "room");
        }
        Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RoomActivity.this,RoomListActivity.class));
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //endregion

}
