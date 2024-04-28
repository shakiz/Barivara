package com.shakil.barivara.activities.meter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityElectricityBillDetailsBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.ElectricityBill;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UtilsForAll;
import com.shakil.barivara.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElectricityBillDetailsActivity extends AppCompatActivity {
    private ActivityElectricityBillDetailsBinding activityMeterCostDetailsBinding;
    private String meterNameStr, roomNameStr;
    private int totalUnitInt, previousMonthUnitInt, presentMonthUnitInt, unitPriceInt, totalElectricityBillInt;
    private UtilsForAll utilsForAll;
    private ElectricityBill electricityBill = new ElectricityBill();
    private String command = "add";
    private int AssociateMeterId = 0 , AssociateRoomId = 0;
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> roomNames, meterNames;
    private Validation validation;
    private Tools tools;
    private SpinnerData spinnerData;
    private final Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMeterCostDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_electricity_bill_details);

        getIntentData();
        init();
        setSupportActionBar(activityMeterCostDetailsBinding.toolBar);

        activityMeterCostDetailsBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bindUiWithComponents();
        loadData();
    }

    private void getIntentData(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getParcelable("electricityBill") != null){
                electricityBill = getIntent().getExtras().getParcelable("electricityBill");
            }
        }
    }

    private void loadData(){
        if (electricityBill.getBillId() != null) {
            command = "update";
            activityMeterCostDetailsBinding.RoomId.setSelection(electricityBill.getRoomId(),true);
            activityMeterCostDetailsBinding.UnitPrice.setText(""+electricityBill.getUnitPrice());
            activityMeterCostDetailsBinding.PresentUnit.setText(""+electricityBill.getPresentUnit());
            activityMeterCostDetailsBinding.PastUnit.setText(""+electricityBill.getPastUnit());
            activityMeterCostDetailsBinding.TotalUnit.setText(""+electricityBill.getTotalUnit());
            activityMeterCostDetailsBinding.TotalAmount.setText(""+(electricityBill.getTotalUnit() * electricityBill.getUnitPrice()));
        }
    }

    private void bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(new String[]{"PastUnit", "PresentUnit", "UnitPrice"},
                new String[]{getString(R.string.past_unit_validation), getString(R.string.present_unit_validation)
                        , getString(R.string.unit_price_validation)});

        activityMeterCostDetailsBinding.MeterId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meterNameStr = parent.getItemAtPosition(position).toString();
                AssociateMeterId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityMeterCostDetailsBinding.RoomId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomNameStr = parent.getItemAtPosition(position).toString();
                AssociateRoomId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("meter", "meterName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    meterNames = nameList;
                    setMeterAdapter();
                }
            });
        } else {
            meterNames = spinnerData.setSpinnerNoData();
            setMeterAdapter();
        }

        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("room", "roomName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    roomNames = nameList;
                    setRoomAdapter();
                }
            });
        } else {
            roomNames = spinnerData.setSpinnerNoData();
            setRoomAdapter();
        }

        activityMeterCostDetailsBinding.mAddMeterDetailsMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation.isValid()){
                    if (tools.hasConnection()) {
                        electricityBill.setMeterId(AssociateMeterId);
                        electricityBill.setRoomId(AssociateRoomId);
                        electricityBill.setMeterName(meterNameStr);
                        electricityBill.setRoomName(roomNameStr);
                        electricityBill.setUnitPrice(Double.parseDouble(activityMeterCostDetailsBinding.UnitPrice.getText().toString().trim()));
                        electricityBill.setPresentUnit(Integer.parseInt(activityMeterCostDetailsBinding.PresentUnit.getText().toString().trim()));
                        electricityBill.setPastUnit(Integer.parseInt(activityMeterCostDetailsBinding.PastUnit.getText().toString().trim()));
                        electricityBill.setTotalUnit(Double.parseDouble(activityMeterCostDetailsBinding.TotalUnit.getText().toString().trim()));
                        electricityBill.setTotalBill(Double.parseDouble(activityMeterCostDetailsBinding.TotalAmount.getText().toString().trim()));

                        if (command.equals("add")) {
                            electricityBill.setBillId(UUID.randomUUID().toString());
                            firebaseCrudHelper.add(electricityBill,"electricityBill");
                        } else {
                            firebaseCrudHelper.update(electricityBill, electricityBill.getFireBaseKey(), "electricityBill");
                        }
                        Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ElectricityBillDetailsActivity.this, ElectricityBillListActivity.class));
                    } else {
                        Toast.makeText(ElectricityBillDetailsActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        activityMeterCostDetailsBinding.PastUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previousMonthUnitInt = utilsForAll.toIntValue(charSequence.toString());
                presentMonthUnitInt = utilsForAll.toIntValue(activityMeterCostDetailsBinding.PresentUnit.getText().toString());
                checkUnitLimit(previousMonthUnitInt, presentMonthUnitInt);
                unitPriceInt = utilsForAll.toIntValue(activityMeterCostDetailsBinding.UnitPrice.getText().toString());
                totalElectricityBillInt = unitPriceInt*totalUnitInt;
                activityMeterCostDetailsBinding.TotalAmount.setText(String.valueOf(totalElectricityBillInt));
                Log.v(Constants.TAG,"previousMonthUnitInt : "+previousMonthUnitInt);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activityMeterCostDetailsBinding.PresentUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presentMonthUnitInt = utilsForAll.toIntValue(charSequence.toString());
                previousMonthUnitInt = utilsForAll.toIntValue(activityMeterCostDetailsBinding.PastUnit.getText().toString());
                checkUnitLimit(previousMonthUnitInt, presentMonthUnitInt);
                unitPriceInt = utilsForAll.toIntValue(activityMeterCostDetailsBinding.UnitPrice.getText().toString());
                totalElectricityBillInt = unitPriceInt*totalUnitInt;
                activityMeterCostDetailsBinding.TotalAmount.setText(String.valueOf(totalElectricityBillInt));
                Log.v(Constants.TAG,"presentMonthUnitInt : "+presentMonthUnitInt);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activityMeterCostDetailsBinding.UnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                unitPriceInt = utilsForAll.toIntValue(charSequence.toString());
                checkUnitLimit(previousMonthUnitInt, presentMonthUnitInt);
                totalElectricityBillInt = unitPriceInt*totalUnitInt;
                activityMeterCostDetailsBinding.TotalAmount.setText(String.valueOf(totalElectricityBillInt));
                Log.v(Constants.TAG,"totalElectricityBillInt : "+totalElectricityBillInt);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setMeterAdapter(){
        ArrayAdapter<String> meterNameSpinnerAdapter = new ArrayAdapter<>(ElectricityBillDetailsActivity.this, R.layout.spinner_drop, meterNames);
        meterNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityMeterCostDetailsBinding.MeterId.setAdapter(meterNameSpinnerAdapter);
    }

    private void setRoomAdapter(){
        ArrayAdapter<String> roomNameSpinnerAdapter = new ArrayAdapter<>(ElectricityBillDetailsActivity.this, R.layout.spinner_drop, roomNames);
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityMeterCostDetailsBinding.RoomId.setAdapter(roomNameSpinnerAdapter);
    }

    private void checkUnitLimit(int previousMonthUnitValue , int presentMonthUnitValue){
        if (presentMonthUnitValue > previousMonthUnitValue) {
            calculateUnit(presentMonthUnitValue, previousMonthUnitValue);
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.warning_message_unit, Toast.LENGTH_SHORT).show();
            totalUnitInt = 0;
            activityMeterCostDetailsBinding.TotalUnit.setText(String.valueOf(totalUnitInt));
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(getResources().getColor(R.color.md_red_400));
        }
    }

    private void calculateUnit(int presentMonthUnitValue, int previousMonthUnitValue){
        if (presentMonthUnitValue > 0 ){
            totalUnitInt = presentMonthUnitValue - previousMonthUnitValue;
            activityMeterCostDetailsBinding.TotalUnit.setText(String.valueOf(totalUnitInt));
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(getResources().getColor(R.color.md_grey_800));
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.please_check_unit_values), Toast.LENGTH_SHORT).show();
            totalUnitInt = 0;
            activityMeterCostDetailsBinding.TotalUnit.setText(String.valueOf(totalUnitInt));
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(getResources().getColor(R.color.md_red_400));
        }
    }

    private void init() {
        roomNames = new ArrayList<>();
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this);
        tools = new Tools(this);
        spinnerData = new SpinnerData(this);
        validation = new Validation(this, hashMap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
