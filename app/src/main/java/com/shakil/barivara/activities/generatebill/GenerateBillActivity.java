package com.shakil.barivara.activities.generatebill;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityGenerateBillBinding;
import com.shakil.barivara.model.bill.GenerateBill;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.shakil.barivara.utils.Constants.REQUEST_CALL_CODE;

public class GenerateBillActivity extends AppCompatActivity {
    private ActivityGenerateBillBinding activityBinding;
    private final Map<String, String[]> hashMap = new HashMap();
    private String YearStr, MonthStr;
    private Validation validation;
    private SpinnerData spinnerData;
    private SpinnerAdapter spinnerAdapter;
    private Tools tools;
    private Dialog dialogBill;

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
        tools = new Tools(this);
    }
    //endregion

    //region perform all Ui interactions
    private void binUIWithComponents(){
        //region ask permission
        if (ContextCompat.checkSelfPermission(GenerateBillActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GenerateBillActivity.this, new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_CALL_CODE);
        }
        //endregion

        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"AssociateRoom","TenantName","MobileNo","RentAmount"},
                new String[]{getString(R.string.room_name_validation), getString(R.string.tenant_name_validation),
                        getString(R.string.rent_amount_validation), getString(R.string.mobile_validation)});
        validation.setSpinnerIsNotEmpty(new String[]{"YearId","MonthId"});
        //endregion

        //region set spinner data
        spinnerAdapter.setSpinnerAdapter(activityBinding.MonthId,this, spinnerData.setMonthData());
        spinnerAdapter.setSpinnerAdapter(activityBinding.YearId,this, spinnerData.setYearData());
        //endregion

        //region spinner onChange listeners
        activityBinding.YearId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                YearStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityBinding.MonthId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MonthStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        //region generate bill listener
        activityBinding.generateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()){
                    if (tools.isValidMobile(activityBinding.MobileNo.getText().toString())){
                        GenerateBill generateBill = new GenerateBill(
                                activityBinding.TenantName.getText().toString(),
                                activityBinding.MobileNo.getText().toString(),
                                MonthStr + " "+YearStr,
                                activityBinding.AssociateRoom.getText().toString(),
                                Integer.parseInt(activityBinding.RentAmount.getText().toString()),
                                Integer.parseInt(activityBinding.GasBill.getText().toString()),
                                Integer.parseInt(activityBinding.WaterBill.getText().toString()),
                                Integer.parseInt(activityBinding.ElectricityBill.getText().toString()),
                                Integer.parseInt(activityBinding.ServiceCharge.getText().toString())
                        );
                        doPopUpForBillDetails(generateBill);
                    }
                    else{
                        Toast.makeText(GenerateBillActivity.this,
                                getString(R.string.mobile_number_not_valid), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //endregion
    }
    //endregion

    //region sending message to tenant with bill details
    public void doPopUpForBillDetails(GenerateBill generateBill){
        Button cancel, sendMessage;
        TextView tenantName, mobileNo, monthAndYear, roomName, rentAmount, gasBill, waterBill, serviceCharge, electricityBill;
        dialogBill = new Dialog(this, android.R.style.Theme_Dialog);
        dialogBill.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBill.setContentView(R.layout.bill_confirmation_layout);
        dialogBill.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBill.setCanceledOnTouchOutside(true);

        //region dialogs findViewByIds
        cancel = dialogBill.findViewById(R.id.cancelButton);
        sendMessage = dialogBill.findViewById(R.id.sendMessage);
        tenantName = dialogBill.findViewById(R.id.TenantName);
        mobileNo = dialogBill.findViewById(R.id.MobileNo);
        roomName = dialogBill.findViewById(R.id.RoomName);
        monthAndYear = dialogBill.findViewById(R.id.MonthAndYear);
        rentAmount = dialogBill.findViewById(R.id.RentAmount);
        gasBill = dialogBill.findViewById(R.id.GasBill);
        waterBill = dialogBill.findViewById(R.id.WaterBill);
        serviceCharge = dialogBill.findViewById(R.id.ServiceCharge);
        electricityBill = dialogBill.findViewById(R.id.ElectricityBill);
        //endregion

        //region set data
        tenantName.setText(generateBill.getTenantName());
        mobileNo.setText(generateBill.getMobileNo());
        monthAndYear.setText(generateBill.getMonthAndYear());
        roomName.setText(generateBill.getAssociateRoom());
        rentAmount.setText(""+generateBill.getRentAmount()+" "+getString(R.string.taka));
        gasBill.setText(""+generateBill.getGasBill()+" "+getString(R.string.taka));
        waterBill.setText(""+generateBill.getWaterBill()+" "+getString(R.string.taka));
        serviceCharge.setText(""+generateBill.getServiceCharge()+" "+getString(R.string.taka));
        electricityBill.setText(""+generateBill.getElectricityBill()+" "+getString(R.string.taka));
        //endregion

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBill.dismiss();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(generateBill);
            }
        });

        dialogBill.setCanceledOnTouchOutside(false);
        dialogBill.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogBill.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialogBill.show();
    }
    //endregion

    //region send message to tenant with details
    private void sendMessage(GenerateBill generateBill){
        //region ask permission
        if (ContextCompat.checkSelfPermission(GenerateBillActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GenerateBillActivity.this, new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_CALL_CODE);

            return;
        }
        //endregion
        /*This will be the actual content you wish you share.*/
        String message =
                        "Name : "+generateBill.getTenantName()+"\n"+
                        "Mobile : "+generateBill.getMobileNo()+"\n"+
                        "Month and Year: "+generateBill.getMonthAndYear()+"\n"+
                        "Room Name : "+generateBill.getAssociateRoom()+"\n"+
                        "Rent Amount : "+generateBill.getRentAmount()+"\n"+
                        "Gas Bill : "+generateBill.getGasBill()+"\n"+
                        "Water Bill : "+generateBill.getWaterBill()+"\n"+
                        "Electricity Bill : "+generateBill.getElectricityBill()+"\n"+
                        "Service Charge : "+generateBill.getServiceCharge()+"\n"
                ;
        try{
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(generateBill.getMobileNo(),null,parts,null,null);
            Toast.makeText(this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, getString(R.string.message_not_sent)+"\n"+
                    getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
        dialogBill.dismiss();
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(GenerateBillActivity.this, MainActivity.class));
    }
    //endregion
}