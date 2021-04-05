package com.shakil.barivara.activities.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityNewRentDetailsBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.utils.InputValidation;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;

public class RentDetailsActivity extends AppCompatActivity {
    private ActivityNewRentDetailsBinding activityNewRentDetailsBinding;
    private SpinnerData spinnerData;
    private InputValidation inputValidation;
    private SpinnerAdapter spinnerAdapter;
    Rent rent = new Rent();
    private String command = "add";
    DbHelperParent dbHelperParent;
    private int MonthId = 0, AssociateRoomId = 0;
    private String MonthStr = "", AssociateRoomStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewRentDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_rent_details);

        //region get intent data
        getIntentData();
        //endregion

        init();
        setSupportActionBar(activityNewRentDetailsBinding.toolBar);

        activityNewRentDetailsBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RentDetailsActivity.this, MainActivity.class));
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
            if (getIntent().getExtras().getParcelable("rent") != null){
                rent = getIntent().getExtras().getParcelable("rent");
            }
        }
    }
    //endregion

    //region load intent data to UI
    private void loadData(){
        if (rent.getRentId() != 0) {
            command = "update";
            activityNewRentDetailsBinding.RentAmount.setText(String.valueOf(rent.getRentAmount()));
            activityNewRentDetailsBinding.MonthId.setSelection(rent.getMonthId(), true);
            activityNewRentDetailsBinding.AssociateRoomId.setSelection(rent.getAssociateRoomId(), true);
        }
    }
    //endregion

    private void bindUiWithComponents() {
        spinnerAdapter.setSpinnerAdapter(activityNewRentDetailsBinding.MonthId,spinnerData.setMonthData(),this);
        spinnerAdapter.setSpinnerAdapter(activityNewRentDetailsBinding.AssociateRoomId,spinnerData.setRoomData(),this);

        //region spinner on change
        activityNewRentDetailsBinding.MonthId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MonthId = position;
                MonthStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityNewRentDetailsBinding.AssociateRoomId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AssociateRoomId = position;
                AssociateRoomStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        activityNewRentDetailsBinding.mAddRentMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation.checkEditTextInput(R.id.RentAmount,"Please check your value");
                rent.setMonthId(MonthId);
                rent.setMonthName(MonthStr);
                rent.setAssociateRoomId(AssociateRoomId);
                rent.setAssociateRoomName(AssociateRoomStr);
                rent.setRentAmount(Integer.parseInt(activityNewRentDetailsBinding.RentAmount.getText().toString()));
                if (command.equals("add")){
                    dbHelperParent.addRent(rent);
                }
                else if (command.equals("update")){
                    dbHelperParent.updateRent(rent, rent.getRentId());
                }
                Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RentDetailsActivity.this, RentListActivity.class));
            }
        });
    }

    private void init() {
        spinnerData = new SpinnerData(this);
        dbHelperParent = new DbHelperParent(this);
        spinnerAdapter = new SpinnerAdapter();
        inputValidation = new InputValidation(this, activityNewRentDetailsBinding.mainLayout);
    }

    //region activity components

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //endregion
}
