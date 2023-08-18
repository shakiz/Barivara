package com.shakil.barivara.activities.generatebill;

import static com.shakil.barivara.utils.Constants.REQUEST_CALL_CODE;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.DroidFileManager;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;
import com.shakil.barivara.utils.Validation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenerateBillActivity extends AppCompatActivity {
    private ActivityGenerateBillBinding activityBinding;
    private final Map<String, String[]> hashMap = new HashMap();
    private String YearStr, MonthStr;
    private Validation validation;
    private SpinnerData spinnerData;
    private SpinnerAdapter spinnerAdapter;
    private Tools tools;
    private Dialog dialogBill;
    private CustomAdManager customAdManager;
    private UX ux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_generate_bill);

        init();
        binUIWithComponents();
    }

    private void init() {
        customAdManager = new CustomAdManager(this);
        validation = new Validation(this, hashMap);
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
        tools = new Tools(this);
        ux = new UX(this);
    }

    private void binUIWithComponents() {
        customAdManager.generateAd(activityBinding.adView);

        if ((ContextCompat.checkSelfPermission(GenerateBillActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(GenerateBillActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(GenerateBillActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CALL_CODE);
        }

        activityBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        validation.setEditTextIsNotEmpty(new String[]{"AssociateRoom", "TenantName", "MobileNo", "RentAmount"},
                new String[]{getString(R.string.room_name_validation), getString(R.string.tenant_name_validation),
                        getString(R.string.rent_amount_validation), getString(R.string.mobile_validation)});
        validation.setSpinnerIsNotEmpty(new String[]{"YearId", "MonthId"});

        spinnerAdapter.setSpinnerAdapter(activityBinding.MonthId, this, spinnerData.setMonthData());
        spinnerAdapter.setSpinnerAdapter(activityBinding.YearId, this, spinnerData.setYearData());

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

        activityBinding.generateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()) {
                    if (tools.isValidMobile(activityBinding.MobileNo.getText().toString())) {
                        try {
                            GenerateBill generateBill = new GenerateBill(
                                    activityBinding.TenantName.getText().toString(),
                                    activityBinding.MobileNo.getText().toString(),
                                    MonthStr + " " + YearStr,
                                    activityBinding.AssociateRoom.getText().toString(),
                                    Integer.parseInt(activityBinding.RentAmount.getText().toString()),
                                    Integer.parseInt(activityBinding.GasBill.getText().toString()),
                                    Integer.parseInt(activityBinding.WaterBill.getText().toString()),
                                    Integer.parseInt(activityBinding.ElectricityBill.getText().toString()),
                                    Integer.parseInt(activityBinding.ServiceCharge.getText().toString())
                            );
                            doPopUpForBillDetails(generateBill);
                        } catch (Exception e) {
                            Log.e("onClick: ", e.getMessage());
                        }
                    } else {
                        Toast.makeText(GenerateBillActivity.this,
                                getString(R.string.mobile_number_not_valid), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        activityBinding.generatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()) {
                    if (tools.isValidMobile(activityBinding.MobileNo.getText().toString())) {
                        GenerateBill generateBill = new GenerateBill(
                                activityBinding.TenantName.getText().toString(),
                                activityBinding.MobileNo.getText().toString(),
                                MonthStr + " " + YearStr,
                                activityBinding.AssociateRoom.getText().toString(),
                                Integer.parseInt(activityBinding.RentAmount.getText().toString()),
                                Integer.parseInt(activityBinding.GasBill.getText().toString()),
                                Integer.parseInt(activityBinding.WaterBill.getText().toString()),
                                Integer.parseInt(activityBinding.ElectricityBill.getText().toString()),
                                Integer.parseInt(activityBinding.ServiceCharge.getText().toString())
                        );
                        generatePdf(generateBill);
                    }
                }
            }
        });
    }

    private void generatePdf(GenerateBill generateBill) {
        ux.getLoadingView();
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        String name = getString(R.string.tenant_name) + ": " + generateBill.getTenantName();
        String mobile = getString(R.string.mobile) + ": " + generateBill.getMobileNo();
        String monthAndYear = getString(R.string.month_and_year) + ": " + generateBill.getMonthAndYear();
        String roomName = getString(R.string.room_name) + ": " + generateBill.getAssociateRoom();
        String rentAmount = getString(R.string.rent_amount) + ": " + generateBill.getRentAmount() + " " + getString(R.string.taka);
        String gasBill = getString(R.string.gas_bill) + ": " + generateBill.getGasBill() + " " + getString(R.string.taka);
        String waterBill = getString(R.string.water_bill) + ": " + generateBill.getWaterBill() + " " + getString(R.string.taka);
        String electricityBill = getString(R.string.electricity_bill) + ": " + generateBill.getElectricityBill() + " " + getString(R.string.taka);
        String serviceCharge = getString(R.string.service_charge) + ": " + generateBill.getServiceCharge() + " " + getString(R.string.taka);

        canvas.drawText(name, 40, 50, paint);
        canvas.drawText(mobile, 40, 70, paint);
        canvas.drawText(monthAndYear, 40, 90, paint);
        canvas.drawText(roomName, 40, 110, paint);
        canvas.drawText(rentAmount, 40, 130, paint);
        canvas.drawText(gasBill, 40, 150, paint);
        canvas.drawText(waterBill, 40, 170, paint);
        canvas.drawText(electricityBill, 40, 190, paint);
        canvas.drawText(serviceCharge, 40, 2100, paint);
        document.finishPage(page);

        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/Vara-Adai/";
        String fileName = "Vara-Adai_" + generateBill.getMobileNo() + "-" + System.currentTimeMillis() + ".pdf";
        String targetPdf = directoryPath + fileName;

        new DroidFileManager().createFolder(directoryPath);
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            ux.removeLoadingView();
            Toast.makeText(this, getString(R.string.file_save_in_downloads), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            ux.removeLoadingView();
            Toast.makeText(this, getString(R.string.please_try_again_something_went_wrong), Toast.LENGTH_LONG).show();
        }
        document.close();
    }

    public void doPopUpForBillDetails(GenerateBill generateBill) {
        Button cancel, sendMessage;
        TextView tenantName, mobileNo, monthAndYear, roomName, rentAmount, gasBill, waterBill, serviceCharge, electricityBill;
        dialogBill = new Dialog(this, android.R.style.Theme_Dialog);
        dialogBill.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBill.setContentView(R.layout.bill_confirmation_layout);
        dialogBill.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBill.setCanceledOnTouchOutside(true);

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

        tenantName.setText(generateBill.getTenantName());
        mobileNo.setText(generateBill.getMobileNo());
        monthAndYear.setText(generateBill.getMonthAndYear());
        roomName.setText(generateBill.getAssociateRoom());
        rentAmount.setText("" + generateBill.getRentAmount() + " " + getString(R.string.taka));
        gasBill.setText("" + generateBill.getGasBill() + " " + getString(R.string.taka));
        waterBill.setText("" + generateBill.getWaterBill() + " " + getString(R.string.taka));
        serviceCharge.setText("" + generateBill.getServiceCharge() + " " + getString(R.string.taka));
        electricityBill.setText("" + generateBill.getElectricityBill() + " " + getString(R.string.taka));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBill.dismiss();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message =
                        "Name : " + generateBill.getTenantName() + "\n" +
                                "Mobile : " + generateBill.getMobileNo() + "\n" +
                                "Month and Year: " + generateBill.getMonthAndYear() + "\n" +
                                "Room Name : " + generateBill.getAssociateRoom() + "\n" +
                                "Rent Amount : " + generateBill.getRentAmount() + " " + getString(R.string.taka) + "\n" +
                                "Gas Bill : " + generateBill.getGasBill() + " " + getString(R.string.taka) + "\n" +
                                "Water Bill : " + generateBill.getWaterBill() + " " + getString(R.string.taka) + "\n" +
                                "Electricity Bill : " + generateBill.getElectricityBill() + " " + getString(R.string.taka) + "\n" +
                                "Service Charge : " + generateBill.getServiceCharge() + " " + getString(R.string.taka);
                sendMessage(message, generateBill.getMobileNo());
            }
        });

        dialogBill.setCanceledOnTouchOutside(false);
        dialogBill.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogBill.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialogBill.show();
    }

    private void sendMessage(String message, String mobileNo) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        smsIntent.setType("text/plain");
        smsIntent.putExtra("sms_body", message);
        smsIntent.setData(Uri.parse("sms:" + mobileNo));
        startActivity(smsIntent);
        Toast.makeText(this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
        dialogBill.dismiss();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GenerateBillActivity.this, MainActivity.class));
    }
}