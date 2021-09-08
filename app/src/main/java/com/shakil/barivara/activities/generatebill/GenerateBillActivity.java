package com.shakil.barivara.activities.generatebill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.room.RentDetailsActivity;
import com.shakil.barivara.activities.room.RoomActivity;
import com.shakil.barivara.databinding.ActivityGenerateBillBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenerateBillActivity extends AppCompatActivity {
    private ActivityGenerateBillBinding activityBinding;
    private final Map<String, String[]> hashMap = new HashMap();
    private ArrayList<String> roomNames, tenantNames;
    private ArrayAdapter<String> roomNameSpinnerAdapter, tenantNameSpinnerAdapter;
    private Validation validation;
    private SpinnerData spinnerData;
    private SpinnerAdapter spinnerAdapter;
    private FirebaseCrudHelper firebaseCrudHelper;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_generate_bill);

        //region init and bind UI actions
        init();
        binUIWithComponents();
        //endregion
    }

    //region init objects
    private void init(){
        validation = new Validation(this, hashMap);
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        tools = new Tools(this);
    }
    //endregion

    //region perform all Ui interactions
    private void binUIWithComponents(){
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"RentAmount"},
                new String[]{getString(R.string.rent_amount_validation)});
        validation.setSpinnerIsNotEmpty(new String[]{"AssociateRoomId","YearId","MonthId","TenantNameId"});
        //endregion

        //region set spinner data
        spinnerAdapter.setSpinnerAdapter(activityBinding.MonthId,this, spinnerData.setMonthData());
        spinnerAdapter.setSpinnerAdapter(activityBinding.YearId,this, spinnerData.setYearData());
        //endregion

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


        //region set tenant spinner
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName("tenant", "tenantName", new FirebaseCrudHelper.onNameFetch() {
                @Override
                public void onFetched(ArrayList<String> nameList) {
                    tenantNames = nameList;
                    setTenantSpinner();
                }
            });
        } else {
            tenantNames = spinnerData.setSpinnerNoData();
            setTenantSpinner();
        }
        //endregion

        //region generate bill listener
        activityBinding.generateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()){
                    doPopUpForBillDetails();
                }
            }
        });
        //endregion
    }
    //endregion

    //region set spinner adapter
    private void setRoomSpinner(){
        roomNameSpinnerAdapter = new ArrayAdapter<>(GenerateBillActivity.this, R.layout.spinner_drop, roomNames);
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityBinding.AssociateRoomId.setAdapter(roomNameSpinnerAdapter);
    }
    //endregion

    //region set tenant spinner
    private void setTenantSpinner(){
        tenantNameSpinnerAdapter = new ArrayAdapter<>(GenerateBillActivity.this, R.layout.spinner_drop, tenantNames);
        tenantNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        activityBinding.TenantNameId.setAdapter(tenantNameSpinnerAdapter);
    }
    //endregion

    //region sending message to tenant with bill details
    public void doPopUpForBillDetails(){
        Button cancel, sendMessage;
        Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bill_confirmation_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        cancel = dialog.findViewById(R.id.cancelButton);
        sendMessage = dialog.findViewById(R.id.sendMessage);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    //endregion
}