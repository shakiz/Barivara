package com.shakil.barivara.activities.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityAddNewTenantBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.InputValidation;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;

import java.util.ArrayList;
import java.util.UUID;

public class NewTenantActivity extends AppCompatActivity {
    private ActivityAddNewTenantBinding activityAddNewTenantBinding;
    private Toolbar toolbar;
    private DbHelperParent dbHelperParent;
    private SpinnerAdapter spinnerAdapter;
    private SpinnerData spinnerData;
    private int AssociateRoomId, StartingMonthId;
    private String tenantNameStr, AssociateRoomStr, StartingMonthStr;
    private InputValidation inputValidation;
    private Tenant tenant = new Tenant();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;
    private ArrayList<String> roomNames;
    private ArrayAdapter<String> roomNameSpinnerAdapter;

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
            activityAddNewTenantBinding.AssociateRoomId.setSelection(tenant.getAssociateRoomId(),true);
        }
    }
    //endregion

    ///region init all objects
    private void init() {
        inputValidation = new InputValidation(this,activityAddNewTenantBinding.mainLayout);
        toolbar = findViewById(R.id.tool_bar);
        dbHelperParent = new DbHelperParent(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        roomNames = new ArrayList<>();
    }
    //endregion

    //region bind UI with components
    private void bindUiWithComponents(){
        spinnerAdapter.setSpinnerAdapter(activityAddNewTenantBinding.StartingMonthId,this,spinnerData.setMonthData());

        //region set month spinner
        firebaseCrudHelper.getAllName("room", "roomName", new FirebaseCrudHelper.onNameFetch() {
            @Override
            public void onFetched(ArrayList<String> nameList) {
                roomNames = nameList;
                roomNameSpinnerAdapter = new ArrayAdapter<>(NewTenantActivity.this, R.layout.spinner_drop, roomNames);
                roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                activityAddNewTenantBinding.AssociateRoomId.setAdapter(roomNameSpinnerAdapter);
            }
        });
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
                inputValidation.checkEditTextInput(R.id.TenantName,"Please check your data");

                //region validation and save data
                tenantNameStr = activityAddNewTenantBinding.TenantName.getText().toString();
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
                //endregion
            }
        });
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
        dbHelperParent.close();
    }
    //endregion
}