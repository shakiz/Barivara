package com.shakil.barivara.activities.meter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityNewMeterBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewMeterActivity extends AppCompatActivity {
    private ActivityNewMeterBinding activityNewMeterBinding;
    private SpinnerAdapter spinnerAdapter;
    private SpinnerData spinnerData;
    private String meterNameStr, roomNameStr, meterTypeStr;
    private int AssociateRoomId, MeterTypeId;
    private Meter meter = new Meter();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> roomNames;
    private Validation validation;
    private Tools tools;
    private final Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewMeterBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_meter);

        getIntentData();
        init();

        setSupportActionBar(activityNewMeterBinding.toolBar);
        activityNewMeterBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bindUIWithComponents();
        loadData();
    }

    private void getIntentData(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getParcelable("meter") != null){
                meter = getIntent().getExtras().getParcelable("meter");
            }
        }
    }

    private void loadData(){
        if (meter.getMeterId() != null) {
            command = "update";
            activityNewMeterBinding.MeterName.setText(meter.getMeterName());
            activityNewMeterBinding.MeterTypeId.setSelection(meter.getMeterTypeId(),true);
        }
    }

    private void bindUIWithComponents() {
        validation.setEditTextIsNotEmpty(new String[]{"MeterName"},
                new String[]{getString(R.string.meter_name_validation)});
        validation.setSpinnerIsNotEmpty(new String[]{"MeterTypeId"});

        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("room", "roomName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    roomNames = nameList;
                    setRoomNameSpinner();
                }
            });
        } else {
            roomNames = spinnerData.setSpinnerNoData();
            setRoomNameSpinner();
        }

        spinnerAdapter.setSpinnerAdapter(activityNewMeterBinding.MeterTypeId,this, spinnerData.setMeterTypeData());

        activityNewMeterBinding.AssociateRoomId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomNameStr = parent.getItemAtPosition(position).toString();
                AssociateRoomId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityNewMeterBinding.MeterTypeId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meterTypeStr = parent.getItemAtPosition(position).toString();
                MeterTypeId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityNewMeterBinding.mSaveMeterMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation.isValid()) {
                    if (tools.hasConnection()) {
                        meterNameStr = activityNewMeterBinding.MeterName.getText().toString();
                        meter.setMeterName(meterNameStr);
                        meter.setAssociateRoom(roomNameStr);
                        meter.setAssociateRoomId(AssociateRoomId);
                        meter.setMeterTypeName(meterTypeStr);
                        meter.setMeterTypeId(MeterTypeId);
                        if (command.equals("add")) {
                            meter.setMeterId(UUID.randomUUID().toString());
                            firebaseCrudHelper.add(meter, "meter");
                        } else {
                            firebaseCrudHelper.update(meter, meter.getFireBaseKey(), "meter");
                        }
                        Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewMeterActivity.this,MeterListActivity.class));
                    } else {
                        Toast.makeText(NewMeterActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void init() {
        roomNames = new ArrayList<>();
        spinnerAdapter = new SpinnerAdapter();
        spinnerData = new SpinnerData(this);
        tools = new Tools(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        validation = new Validation(this, hashMap);
    }

    private void setRoomNameSpinner(){
        ArrayAdapter<String> roomNameSpinnerAdapter = new ArrayAdapter<>(NewMeterActivity.this, R.layout.spinner_drop, roomNames);
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityNewMeterBinding.AssociateRoomId.setAdapter(roomNameSpinnerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NewMeterActivity.this, MeterListActivity.class));
    }
}
