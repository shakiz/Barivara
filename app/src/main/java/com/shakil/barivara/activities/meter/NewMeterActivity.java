package com.shakil.barivara.activities.meter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityNewMeterBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.utils.InputValidation;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;

import java.util.UUID;

public class NewMeterActivity extends AppCompatActivity {
    private ActivityNewMeterBinding activityNewMeterBinding;
    private InputValidation inputValidation;
    private SpinnerAdapter spinnerAdapter;
    private SpinnerData spinnerData;
    private DbHelperParent dbHelperParent;
    private String meterNameStr, roomNameStr, meterTypeStr;
    private int AssociateRoomId, MeterTypeId;
    private Meter meter = new Meter();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewMeterBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_meter);

        //region get intent data
        getIntentData();
        //endregion

        init();

        setSupportActionBar(activityNewMeterBinding.toolBar);

        activityNewMeterBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
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
            if (getIntent().getExtras().getParcelable("meter") != null){
                meter = getIntent().getExtras().getParcelable("meter");
            }
        }
    }
    //endregion

    //region load intent data to UI
    private void loadData(){
        if (meter.getMeterId() != null) {
            command = "update";
            activityNewMeterBinding.MeterName.setText(meter.getMeterName());
            activityNewMeterBinding.RoomSpinner.setSelection(meter.getAssociateRoomId(),true);
            activityNewMeterBinding.MeterTypeName.setSelection(meter.getMeterTypeId(),true);
        }
    }
    //endregion

    private void bindUIWithComponents() {
        spinnerAdapter.setSpinnerAdapter(activityNewMeterBinding.RoomSpinner,spinnerData.setRoomData(),this);
        spinnerAdapter.setSpinnerAdapter(activityNewMeterBinding.MeterTypeName,spinnerData.setMeterTypeData(),this);

        //region select spinner
        activityNewMeterBinding.RoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomNameStr = parent.getItemAtPosition(position).toString();
                AssociateRoomId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityNewMeterBinding.MeterTypeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meterTypeStr = parent.getItemAtPosition(position).toString();
                MeterTypeId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        activityNewMeterBinding.mSaveMeterMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputValidation.checkEditTextInput(R.id.MeterName,"Please check your data");

                //region validation and save data
                if (!roomNameStr.equals("Select Data") && !meterTypeStr.equals("Select Data")){
                    meterNameStr = activityNewMeterBinding.MeterName.getText().toString();

                    meter.setMeterName(meterNameStr);
                    meter.setAssociateRoom(roomNameStr);
                    meter.setAssociateRoomId(AssociateRoomId);
                    meter.setMeterTypeName(meterTypeStr);
                    meter.setMeterTypeId(MeterTypeId);
                    //region add meter to firebase
                    if (command.equals("add")) {
                        meter.setMeterId(UUID.randomUUID().toString());
                        firebaseCrudHelper.add(meter, "meter");
                    } else {
                        firebaseCrudHelper.update(meter, meter.getFireBaseKey(), "meter");
                    }
                    //endregion
                    Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewMeterActivity.this,MeterListActivity.class));
                }
                else{
                    Toast.makeText(getApplicationContext(),R.string.warning_message, Toast.LENGTH_SHORT).show();
                }
                //endregion
            }
        });
    }

    private void init() {
        inputValidation = new InputValidation(this,activityNewMeterBinding.mainLayout);
        spinnerAdapter = new SpinnerAdapter();
        spinnerData = new SpinnerData(this);
        dbHelperParent = new DbHelperParent(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelperParent.close();
    }

    //region activity components

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NewMeterActivity.this, MeterListActivity.class));
    }

    //endregion
}
