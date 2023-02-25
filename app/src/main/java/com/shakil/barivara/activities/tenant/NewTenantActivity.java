package com.shakil.barivara.activities.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityAddNewTenantBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UtilsForAll;
import com.shakil.barivara.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewTenantActivity extends AppCompatActivity {
    private ActivityAddNewTenantBinding activityAddNewTenantBinding;
    private SpinnerAdapter spinnerAdapter;
    private SpinnerData spinnerData;
    private int AssociateRoomId, StartingMonthId, TenantTypeId;
    private String tenantNameStr, gender, AssociateRoomStr, StartingMonthStr, TenantTypeStr, IsActiveValue;
    private Tenant tenant = new Tenant();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> roomNames;
    private ArrayAdapter<String> roomNameSpinnerAdapter;
    private Validation validation;
    private int advancedAmountInt, isActiveId = 0;
    private final Map<String, String[]> hashMap = new HashMap();
    private UtilsForAll utilsForAll;
    private Tools tools;
    private CustomAdManager customAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNewTenantBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_tenant);

        getIntentData();
        init();

        setSupportActionBar(activityAddNewTenantBinding.toolBar);
        activityAddNewTenantBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
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
            if (getIntent().getExtras().getParcelable("tenant") != null){
                tenant = getIntent().getExtras().getParcelable("tenant");
            }
        }
    }

    private void loadData(){
        if (tenant.getTenantId() != null) {
            command = "update";
            if (tenant.getGender() != null && !TextUtils.isEmpty(tenant.getGender())) {
                changeGender(tenant.getGender());
            }
            activityAddNewTenantBinding.IsActiveLayout.setVisibility(View.VISIBLE);
            activityAddNewTenantBinding.IsActive.check(tenant.getIsActiveId());
            activityAddNewTenantBinding.TenantName.setText(tenant.getTenantName());
            activityAddNewTenantBinding.AssociateRoomId.setVisibility(View.GONE);
            activityAddNewTenantBinding.StartingMonthId.setSelection(tenant.getStartingMonthId(),true);
            activityAddNewTenantBinding.TenantTypeId.setSelection(tenant.getTenantTypeId(),true);
            activityAddNewTenantBinding.NID.setText(tenant.getNID());
            activityAddNewTenantBinding.MobileNo.setText(tenant.getMobileNo());
            activityAddNewTenantBinding.NumberOfPerson.setText(""+tenant.getNumberOfPerson());
            if (tenant.getAdvancedAmount() > 0){
                activityAddNewTenantBinding.advanceAmountLayout.setVisibility(View.VISIBLE);
                activityAddNewTenantBinding.AdvanceCheckBox.setChecked(true);
                activityAddNewTenantBinding.AdvanceAmount.setText(""+tenant.getAdvancedAmount());
                activityAddNewTenantBinding.AdvanceCheckBox.setEnabled(false);
                activityAddNewTenantBinding.AdvanceAmount.setEnabled(false);
            }
        }
    }

    private void init() {
        customAdManager = new CustomAdManager(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this);
        validation = new Validation(this, hashMap);
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        tools = new Tools(this);
        roomNames = new ArrayList<>();
    }

    private void bindUiWithComponents(){
        customAdManager.generateAd(activityAddNewTenantBinding.adView);

        validation.setEditTextIsNotEmpty(new String[]{"TenantName", "NID", "MobileNo"},
                new String[]{getString(R.string.tenant_amount_validation), getString(R.string.nid_number_hint)
                ,getString(R.string.mobile_number_hint)});
        validation.setSpinnerIsNotEmpty(new String[]{"TenantTypeId","StartingMonthId"});

        spinnerAdapter.setSpinnerAdapter(activityAddNewTenantBinding.StartingMonthId,this,spinnerData.setMonthData());
        spinnerAdapter.setSpinnerAdapter(activityAddNewTenantBinding.TenantTypeId,this,spinnerData.setTenantTypeData());

        activityAddNewTenantBinding.maleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = getString(R.string.male);
                changeGender(getString(R.string.male));
            }
        });
        activityAddNewTenantBinding.femaleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = getString(R.string.female);
                changeGender(getString(R.string.female));
            }
        });

        activityAddNewTenantBinding.AdvanceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean visibilityValue) {
                if (visibilityValue){
                    activityAddNewTenantBinding.advanceAmountLayout.setVisibility(View.VISIBLE);
                    activityAddNewTenantBinding.AdvanceAmount.setText("");
                }
                else{
                    activityAddNewTenantBinding.advanceAmountLayout.setVisibility(View.GONE);
                }
            }
        });

        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("room", "roomName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    roomNames = nameList;
                    setRoomSpinner();

                    if (tenant.getTenantId() != null) {
                        activityAddNewTenantBinding.AssociateRoomId.setSelection(tenant.getAssociateRoomId(),true);
                    }
                }
            });
        } else {
            roomNames = spinnerData.setSpinnerNoData();
            setRoomSpinner();
        }

        activityAddNewTenantBinding.TenantTypeId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TenantTypeStr = parent.getItemAtPosition(position).toString();
                TenantTypeId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityAddNewTenantBinding.StartingMonthId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StartingMonthStr = parent.getItemAtPosition(position).toString();
                StartingMonthId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityAddNewTenantBinding.AssociateRoomId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AssociateRoomStr = parent.getItemAtPosition(position).toString();
                AssociateRoomId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityAddNewTenantBinding.IsActive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = null;
                try {
                    radioButton = findViewById(checkedId);
                } catch (Exception e) {
                    Log.i(Constants.TAG, "Radio Button Error : "+e.getMessage());
                }
                isActiveId = checkedId;
                if (radioButton != null) {
                    IsActiveValue = radioButton.getTag().toString();
                }
            }
        });

        activityAddNewTenantBinding.mSaveTenantMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation.isValid()){
                    if (tools.hasConnection()) {
                        if (utilsForAll.isValidMobileNo(activityAddNewTenantBinding.MobileNo.getText().toString())){
                            if (activityAddNewTenantBinding.advanceAmountLayout.getVisibility() == View.VISIBLE){
                                if (!TextUtils.isEmpty(activityAddNewTenantBinding.AdvanceAmount.getText().toString())) {
                                    advancedAmountInt = Integer.parseInt(activityAddNewTenantBinding.AdvanceAmount.getText().toString());
                                    tenant.setAdvancedAmount(advancedAmountInt);
                                    saveOrUpdateData();
                                }
                                else{
                                    Toast.makeText(NewTenantActivity.this, getString(R.string.advance_amount_validation), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                saveOrUpdateData();
                            }
                        }
                        else{
                            Toast.makeText(NewTenantActivity.this, getString(R.string.validation_mobile_number_length), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewTenantActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setRoomSpinner(){
        roomNameSpinnerAdapter = new ArrayAdapter<>(NewTenantActivity.this, R.layout.spinner_drop, roomNames);
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityAddNewTenantBinding.AssociateRoomId.setAdapter(roomNameSpinnerAdapter);
    }

    private void saveOrUpdateData(){
        tenantNameStr = activityAddNewTenantBinding.TenantName.getText().toString();
        tenant.setTenantName(tenantNameStr);
        tenant.setGender(gender);
        tenant.setNID(activityAddNewTenantBinding.NID.getText().toString());
        tenant.setMobileNo(activityAddNewTenantBinding.MobileNo.getText().toString());
        tenant.setNumberOfPerson(utilsForAll.toIntValue(activityAddNewTenantBinding.NumberOfPerson.getText().toString()));
        tenant.setTenantName(tenantNameStr);
        tenant.setAssociateRoom(AssociateRoomStr);
        tenant.setAssociateRoomId(AssociateRoomId);
        tenant.setTenantTypeStr(TenantTypeStr);
        tenant.setTenantTypeId(TenantTypeId);
        tenant.setStartingMonthId(StartingMonthId);
        tenant.setStartingMonth(StartingMonthStr);
        tenant.setIsActiveId(isActiveId);
        tenant.setIsActiveValue(IsActiveValue);
        if (command.equals("add")) {
            tenant.setTenantId(UUID.randomUUID().toString());
            firebaseCrudHelper.add(tenant, "tenant");
        } else {
            firebaseCrudHelper.update(tenant, tenant.getFireBaseKey(), "tenant");
        }
        Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(NewTenantActivity.this,TenantListActivity.class));
    }

    private void changeGender(String gender){
        if (gender.equals(getString(R.string.male))) {
            activityAddNewTenantBinding.maleLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.rectangle_background_filled_gender));
            activityAddNewTenantBinding.Male.setBackgroundResource(0);
            activityAddNewTenantBinding.Male.setTextColor(ContextCompat.getColor(this,R.color.md_white_1000));
            activityAddNewTenantBinding.femaleLayout.setBackgroundResource(0);
            activityAddNewTenantBinding.Female.setTextColor(ContextCompat.getColor(this,R.color.md_green_600));
            activityAddNewTenantBinding.MaleIcon.setImageResource(R.drawable.ic_male_white);
            activityAddNewTenantBinding.FemaleIcon.setImageResource(R.drawable.ic_female_green);
        } else {
            activityAddNewTenantBinding.femaleLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.rectangle_background_filled_gender));
            activityAddNewTenantBinding.Female.setBackgroundResource(0);
            activityAddNewTenantBinding.Female.setTextColor(ContextCompat.getColor(this,R.color.md_white_1000));
            activityAddNewTenantBinding.maleLayout.setBackgroundResource(0);
            activityAddNewTenantBinding.Male.setTextColor(ContextCompat.getColor(this,R.color.md_green_600));
            activityAddNewTenantBinding.MaleIcon.setImageResource(R.drawable.ic_male_green);
            activityAddNewTenantBinding.FemaleIcon.setImageResource(R.drawable.ic_female_white);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NewTenantActivity.this, TenantListActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
