package com.shakil.barivara.activities.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityAddNewRoomBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.utils.InputValidation;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;

import java.util.ArrayList;
import java.util.UUID;

public class RoomActivity extends AppCompatActivity {
    private ActivityAddNewRoomBinding activityAddNewRoomBinding;
    private SpinnerData spinnerData;
    private SpinnerAdapter spinnerAdapter;
    private InputValidation inputValidation;
    private String roomNameStr, startMonthStr,associateMeterStr,tenantNameStr;
    private int StartMonthId, AssociateMeterId, TenantId;
    private int advancedAmountInt;
    private Room room = new Room();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> meterNames, tenantNames;
    private ArrayAdapter<String> meterNameSpinnerAdapter, tenantNameSpinnerAdapter;

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
            activityAddNewRoomBinding.TenantId.setSelection(room.getTenantNameId());
            activityAddNewRoomBinding.StartMonthId.setSelection(room.getStartMonthId(), true);
            activityAddNewRoomBinding.AssociateMeterId.setSelection(room.getAssociateMeterId(), true);
            activityAddNewRoomBinding.AdvanceAmount.setText(""+room.getAdvancedAmount());
        }
    }
    //endregion

    private void bindUIWithComponents() {
        spinnerAdapter.setSpinnerAdapter(activityAddNewRoomBinding.StartMonthId,this,spinnerData.setMonthData());

        //region set meter spinner
        firebaseCrudHelper.getAllName("meter", "meterName", new FirebaseCrudHelper.onNameFetch() {
            @Override
            public void onFetched(ArrayList<String> nameList) {
                meterNames = nameList;
                meterNameSpinnerAdapter = new ArrayAdapter<>(RoomActivity.this, R.layout.spinner_drop, meterNames);
                meterNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                activityAddNewRoomBinding.AssociateMeterId.setAdapter(meterNameSpinnerAdapter);
            }
        });
        //endregion

        //region set tenant spinner
        firebaseCrudHelper.getAllName("tenant", "tenantName", new FirebaseCrudHelper.onNameFetch() {
            @Override
            public void onFetched(ArrayList<String> nameList) {
                tenantNames = nameList;
                tenantNameSpinnerAdapter = new ArrayAdapter<>(RoomActivity.this, R.layout.spinner_drop, tenantNames);
                tenantNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                activityAddNewRoomBinding.TenantId.setAdapter(tenantNameSpinnerAdapter);
            }
        });
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

        activityAddNewRoomBinding.TenantId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        activityAddNewRoomBinding.AdvanceCehckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean visibilityValue) {
                if (visibilityValue){
                    activityAddNewRoomBinding.advanceAmountLayout.setVisibility(View.VISIBLE);
                    if (inputValidation.checkEditTextInput(R.id.AdvanceAmount,"Please give amount")){
                        advancedAmountInt = Integer.parseInt(activityAddNewRoomBinding.AdvanceAmount.getText().toString());
                    }
                    else{
                        Toast.makeText(getApplicationContext(),R.string.warning_message, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    activityAddNewRoomBinding.advanceAmountLayout.setVisibility(View.GONE);
                }
            }
        });

        activityAddNewRoomBinding.mSaveRoomMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation.checkEditTextInput(new int[]{R.id.RoomName},"Please check your data");

                //region validation and save
                roomNameStr = activityAddNewRoomBinding.RoomName.getText().toString();

                room.setRoomName(roomNameStr);
                room.setStartMonthName(startMonthStr);
                room.setStartMonthId(StartMonthId);
                room.setAssociateMeterName(associateMeterStr);
                room.setAssociateMeterId(AssociateMeterId);
                room.setTenantName(tenantNameStr);
                room.setTenantNameId(TenantId);
                room.setAdvancedAmount(advancedAmountInt);
                if (command.equals("add")) {
                    room.setRoomId(UUID.randomUUID().toString());
                    firebaseCrudHelper.add(room, "room");
                } else {
                    firebaseCrudHelper.update(room, room.getFireBaseKey(), "room");
                }
                Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RoomActivity.this,RoomListActivity.class));
                //endregion
            }
        });
    }

    private void init() {
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        inputValidation = new InputValidation(this,activityAddNewRoomBinding.mainLayout);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
    }

    //region activity components

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //endregion

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
