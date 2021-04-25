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
import com.shakil.barivara.utils.InputValidation;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.UtilsForAll;

import java.util.ArrayList;
import java.util.UUID;

public class ElectricityBillDetailsActivity extends AppCompatActivity {
    private ActivityElectricityBillDetailsBinding activityMeterCostDetailsBinding;
    private String meterNameStr, roomNameStr;
    private int totalUnitInt, previousMonthUnitInt, presentMonthUnitInt, unitPriceInt, totalElectricityBillInt;
    private SpinnerData spinnerData;
    private SpinnerAdapter spinnerAdapter;
    private InputValidation inputValidation;
    private UtilsForAll utilsForAll;
    private ElectricityBill electricityBill = new ElectricityBill();
    private String command = "add";
    private int AssociateMeterId, AssociateRoomId;
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> roomNames, meterNames;
    private ArrayAdapter<String> roomNameSpinnerAdapter, meterNameSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMeterCostDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_electricity_bill_details);

        //region get intent data
        getIntentData();
        //endregion

        init();
        setSupportActionBar(activityMeterCostDetailsBinding.toolBar);

        activityMeterCostDetailsBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
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
            if (getIntent().getExtras().getParcelable("electricityBill") != null){
                electricityBill = getIntent().getExtras().getParcelable("electricityBill");
            }
        }
    }
    //endregion

    //region load intent data to UI
    private void loadData(){
        if (electricityBill.getBillId() != null) {
            command = "update";
            activityMeterCostDetailsBinding.UnitPrice.setText(""+electricityBill.getUnitPrice());
            activityMeterCostDetailsBinding.PresentUnit.setText(""+electricityBill.getPresentUnit());
            activityMeterCostDetailsBinding.PastUnit.setText(""+electricityBill.getPastUnit());
            activityMeterCostDetailsBinding.TotalUnit.setText(""+electricityBill.getTotalUnit());
            activityMeterCostDetailsBinding.TotalAmount.setText(""+(electricityBill.getTotalUnit() * electricityBill.getUnitPrice()));
        }
    }
    //endregion

    private void bindUiWithComponents() {
        //region room name select spinner
        activityMeterCostDetailsBinding.AssociateMeterId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meterNameStr = parent.getItemAtPosition(position).toString();
                AssociateMeterId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityMeterCostDetailsBinding.AssociateRoomId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomNameStr = parent.getItemAtPosition(position).toString();
                AssociateRoomId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        //region set meter spinner
        firebaseCrudHelper.getAllName("meter", "meterName", new FirebaseCrudHelper.onNameFetch() {
            @Override
            public void onFetched(ArrayList<String> nameList) {
                meterNames = nameList;
                meterNameSpinnerAdapter = new ArrayAdapter<>(ElectricityBillDetailsActivity.this, R.layout.spinner_drop, meterNames);
                meterNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                activityMeterCostDetailsBinding.AssociateMeterId.setAdapter(meterNameSpinnerAdapter);
            }
        });
        //endregion

        //region set room spinner
        firebaseCrudHelper.getAllName("room", "roomName", new FirebaseCrudHelper.onNameFetch() {
            @Override
            public void onFetched(ArrayList<String> nameList) {
                roomNames = nameList;
                roomNameSpinnerAdapter = new ArrayAdapter<>(ElectricityBillDetailsActivity.this, R.layout.spinner_drop, roomNames);
                roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                activityMeterCostDetailsBinding.AssociateRoomId.setAdapter(roomNameSpinnerAdapter);
            }
        });
        //endregion

        //region Adding new details
        activityMeterCostDetailsBinding.mAddMeterDetailsMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation.checkEditTextInput(new int[]{R.id.PresentUnit,R.id.PastUnit,R.id.UnitPrice},"Please check your value");

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
            }
        });
        //region end

        //region present month unit on change
        activityMeterCostDetailsBinding.PastUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previousMonthUnitInt = utilsForAll.toIntValue(charSequence.toString());
                Log.v("shakil","previousMonthUnitInt : "+previousMonthUnitInt);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //region end

        //region previous month unit on change
        activityMeterCostDetailsBinding.PresentUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presentMonthUnitInt = utilsForAll.toIntValue(charSequence.toString());
                checkUnitLimit(previousMonthUnitInt,presentMonthUnitInt);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //region end

        //region unit price on change
        activityMeterCostDetailsBinding.UnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                unitPriceInt = utilsForAll.toIntValue(charSequence.toString());
                totalElectricityBillInt = unitPriceInt*totalUnitInt;
                activityMeterCostDetailsBinding.TotalAmount.setText(String.valueOf(totalElectricityBillInt));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private int calculateUnit(int previousMonthUnitValue , int presentMonthUnitValue){
        if (presentMonthUnitValue >0 ){
            totalUnitInt = previousMonthUnitValue - presentMonthUnitValue;
            activityMeterCostDetailsBinding.TotalUnit.setText(String.valueOf(totalUnitInt));
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(getResources().getColor(R.color.md_grey_800));
        }
        else {
            Toast.makeText(getApplicationContext(),"Please check unit value", Toast.LENGTH_SHORT).show();
            totalUnitInt = 0;
            activityMeterCostDetailsBinding.TotalUnit.setText(String.valueOf(totalUnitInt));
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(getResources().getColor(R.color.md_red_400));
        }
        return totalUnitInt;
    }

    private void checkUnitLimit(int previousMonthUnitValue , int presentMonthUnitValue){
        if (previousMonthUnitValue>presentMonthUnitValue) {
            calculateUnit(previousMonthUnitValue, presentMonthUnitValue);
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.warning_message_unit, Toast.LENGTH_SHORT).show();
            totalUnitInt = 0;
            activityMeterCostDetailsBinding.TotalUnit.setText(String.valueOf(totalUnitInt));
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(getResources().getColor(R.color.md_red_400));
        }
    }


    private void init() {
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        roomNames = new ArrayList<>();
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        inputValidation = new InputValidation(this,activityMeterCostDetailsBinding.mainLayout);
        utilsForAll = new UtilsForAll(this,activityMeterCostDetailsBinding.mainLayout);
    }

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
