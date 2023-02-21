package com.shakil.barivara.activities.tenant;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.RecyclerTenantListAdapter;
import com.shakil.barivara.databinding.ActivityTenantListBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.FilterManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;

public class TenantListActivity extends AppCompatActivity {
    private ActivityTenantListBinding activityTenantListBinding;
    private ArrayList<Tenant> tenantList;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;
    private Tools tools;
    private FilterManager filterManager;
    private ImageButton searchButton, refreshButton;
    private EditText searchName;
    private CustomAdManager customAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTenantListBinding = DataBindingUtil.setContentView(this, R.layout.activity_tenant_list);

        init();

        setSupportActionBar(activityTenantListBinding.toolBar);

        activityTenantListBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TenantListActivity.this, MainActivity.class));
            }
        });

        binUiWithComponents();
    }

    private void init() {
        searchButton = findViewById(R.id.searchButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchName = findViewById(R.id.SearchName);
        filterManager = new FilterManager(this);
        tenantList = new ArrayList<>();
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        ux = new UX(this);
        tools = new Tools(this);
        customAdManager = new CustomAdManager(this);
    }

    private void binUiWithComponents(){
        //region for ad
        customAdManager.generateAd(activityTenantListBinding.adView);
        //endregion

        searchName.setHint(getString(R.string.search_tenant_name));
        //region check internet connection
        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        //endregion

        activityTenantListBinding.mAddTenantMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TenantListActivity.this, NewTenantActivity.class));
            }
        });

        //region filter
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    if (!TextUtils.isEmpty(searchName.getText().toString())){
                        filterManager.onFilterClick(searchName.getText().toString(), tenantList, new FilterManager.onTenantFilterClick() {
                            @Override
                            public void onClick(ArrayList<Tenant> objects) {
                                if (objects.size() > 0) {
                                    tenantList = objects;
                                    setRecyclerAdapter();
                                    Tools.hideKeyboard(TenantListActivity.this);
                                    Toast.makeText(TenantListActivity.this, getString(R.string.filterd), Toast.LENGTH_SHORT).show();
                                } else {
                                    Tools.hideKeyboard(TenantListActivity.this);
                                    activityTenantListBinding.mNoDataMessage.setVisibility(View.VISIBLE);
                                    activityTenantListBinding.mNoDataMessage.setText(R.string.no_data_message);
                                    activityTenantListBinding.mRecylerView.setVisibility(View.GONE);
                                    Toast.makeText(TenantListActivity.this, getString(R.string.no_data_message), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(TenantListActivity.this, getString(R.string.enter_data_validation), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TenantListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    activityTenantListBinding.mRecylerView.setVisibility(View.VISIBLE);
                    searchName.setText("");
                    activityTenantListBinding.mNoDataMessage.setVisibility(View.GONE);
                    Tools.hideKeyboard(TenantListActivity.this);
                    setData();
                    Toast.makeText(TenantListActivity.this, getString(R.string.list_refreshed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TenantListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion
    }

    //region set recycler data
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllTenant("tenant", new FirebaseCrudHelper.onTenantDataFetch() {
            @Override
            public void onFetch(ArrayList<Tenant> objects) {
                tenantList = objects;
                if (tenantList.size() <= 0){
                    activityTenantListBinding.mNoDataMessage.setVisibility(View.VISIBLE);
                    activityTenantListBinding.mNoDataMessage.setText(R.string.no_data_message);
                }
                setRecyclerAdapter();
                ux.removeLoadingView();
            }
        });
    }
    //endregion

    //region set recycler adapter
    private void setRecyclerAdapter(){
        RecyclerTenantListAdapter recyclerTenantListAdapter = new RecyclerTenantListAdapter(tenantList, this);
        activityTenantListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityTenantListBinding.mRecylerView.setAdapter(recyclerTenantListAdapter);
        recyclerTenantListAdapter.notifyDataSetChanged();
        recyclerTenantListAdapter.setOnItemClickListener(new RecyclerTenantListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Tenant tenant) {
                startActivity(new Intent(TenantListActivity.this, NewTenantActivity.class).putExtra("tenant", tenant));
            }
        });
        recyclerTenantListAdapter.onEditListener(new RecyclerTenantListAdapter.onEditListener() {
            @Override
            public void onEdit(Tenant tenant) {
                startActivity(new Intent(TenantListActivity.this, NewTenantActivity.class).putExtra("tenant", tenant));
            }
        });
        recyclerTenantListAdapter.onDeleteListener(new RecyclerTenantListAdapter.onDeleteListener() {
            @Override
            public void onDelete(Tenant tenant) {
                doPopUpForDeleteConfirmation(tenant);
            }
        });
    }
    //endregion

    //region ask to delete confirmation
    private void doPopUpForDeleteConfirmation(Tenant tenant){
        Button cancel, delete;
        Dialog dialog = new Dialog(TenantListActivity.this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        cancel = dialog.findViewById(R.id.cancelButton);
        delete = dialog.findViewById(R.id.deleteButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseCrudHelper.deleteRecord("tenant",tenant.getFireBaseKey());
                dialog.dismiss();
                setData();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    //endregion

    //region activity components

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TenantListActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //endregion
}
