package com.shakil.barivara.activities.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityAddNewTenantBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.tenant.Tenant;
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
    private Toolbar toolbar;
    private SpinnerAdapter spinnerAdapter;
    private SpinnerData spinnerData;
    private int AssociateRoomId, StartingMonthId;
    private String tenantNameStr, AssociateRoomStr, StartingMonthStr;
    private Tenant tenant = new Tenant();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> roomNames;
    private ArrayAdapter<String> roomNameSpinnerAdapter;
    private Validation validation;
    private int advancedAmountInt;
    private Map<String, String[]> hashMap = new HashMap();
    private UtilsForAll utilsForAll;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNewTenantBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_tenant);

        //region get intent data
        getIntentData();
        //endregion

        init();
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
            if (getIntent().getExtras().getParcelable("tenant") != null){
                tenant = getIntent().getExtras().getParcelable("tenant");
            }
        }
    }
    //endregion

    //region load intent data to UI
    private void loadData(){
        if (tenant.getTenantId() != null) {
            command = "update";
            activityAddNewTenantBinding.TenantName.setText(tenant.getTenantName());
            activityAddNewTenantBinding.StartingMonthId.setSelection(tenant.getStartingMonthId(),true);
            activityAddNewTenantBinding.NID.setText(tenant.getNID());
            activityAddNewTenantBinding.MobileNo.setText(tenant.getMobileNo());
            activityAddNewTenantBinding.NumberOfPerson.setText(""+tenant.getNumberOfPerson());
            if (tenant.getAdvancedAmount() > 0){
                activityAddNewTenantBinding.advanceAmountLayout.setVisibility(View.VISIBLE);
                activityAddNewTenantBinding.AdvanceCheckBox.setChecked(true);
                activityAddNewTenantBinding.AdvanceAmount.setText(""+tenant.getAdvancedAmount());
            }
        }
    }
    //endregion

    ///region init all objects
    private void init() {
        toolbar = findViewById(R.id.tool_bar);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this);
        validation = new Validation(this, hashMap);
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        tools = new Tools(this);
        roomNames = new ArrayList<>();
    }
    //endregion

    //region bind UI with components
    private void bindUiWithComponents(){
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"TenantName", "NID", "MobileNo"},
                new String[]{getString(R.string.tenant_amount_validation), getString(R.string.nid_number_hint)
                ,getString(R.string.mobile_number_hint)});
        validation.setSpinnerIsNotEmpty(new String[]{"StartingMonthId"});
        //endregion

        spinnerAdapter.setSpinnerAdapter(activityAddNewTenantBinding.StartingMonthId,this,spinnerData.setMonthData());

        //region is advance amount or not
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
        //endregion

        //region set month spinner
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
        //endregion

        //region spinner on change
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
        //endregion

        activityAddNewTenantBinding.mSaveTenantMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //region validation and save data
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
                //endregion
            }
        });
    }
    //endregion

    //region set room spinner
    private void setRoomSpinner(){
        roomNameSpinnerAdapter = new ArrayAdapter<>(NewTenantActivity.this, R.layout.spinner_drop, roomNames);
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityAddNewTenantBinding.AssociateRoomId.setAdapter(roomNameSpinnerAdapter);
    }
    //endregion

    //region save all data
    private void saveOrUpdateData(){
        tenantNameStr = activityAddNewTenantBinding.TenantName.getText().toString();
        tenant.setTenantName(tenantNameStr);
        tenant.setNID(activityAddNewTenantBinding.NID.getText().toString());
        tenant.setMobileNo(activityAddNewTenantBinding.MobileNo.getText().toString());
        tenant.setNumberOfPerson(utilsForAll.toIntValue(activityAddNewTenantBinding.NumberOfPerson.getText().toString()));
        tenant.setTenantName(tenantNameStr);
        tenant.setAssociateRoom(AssociateRoomStr);
        tenant.setAssociateRoomId(AssociateRoomId);
        tenant.setStartingMonthId(StartingMonthId);
        tenant.setStartingMonth(StartingMonthStr);
        if (command.equals("add")) {
            tenant.setTenantId(UUID.randomUUID().toString());
            firebaseCrudHelper.add(tenant, "tenant");
        } else {
            firebaseCrudHelper.update(tenant, tenant.getFireBaseKey(), "tenant");
        }
        Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(NewTenantActivity.this,TenantListActivity.class));
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(NewTenantActivity.this, TenantListActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //endregion
}
