package com.shakil.barivara.activities.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.RecyclerTenantListAdapter;
import com.shakil.barivara.databinding.ActivityTenantListBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;

public class TenantListActivity extends AppCompatActivity {
    private ActivityTenantListBinding activityTenantListBinding;
    private RecyclerTenantListAdapter recyclerTenantListAdapter;
    private ArrayList<Tenant> tenantList;
    private TextView noDataTXT;
    private DbHelperParent dbHelperParent;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;

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
        tenantList = new ArrayList<>();
        dbHelperParent = new DbHelperParent(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        ux = new UX(this);
        noDataTXT = findViewById(R.id.mNoDataMessage);
    }

    private void binUiWithComponents(){
        setData();

        activityTenantListBinding.mAddTenantMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TenantListActivity.this, NewTenantActivity.class));
            }
        });
    }

    //region set recycler data
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllTenant("tenant", new FirebaseCrudHelper.onTenantDataFetch() {
            @Override
            public void onFetch(ArrayList<Tenant> objects) {
                tenantList = objects;
                if (tenantList.size() <= 0){
                    noDataTXT.setVisibility(View.VISIBLE);
                    noDataTXT.setText(R.string.no_data_message);
                }
                setRecyclerAdapter();
                ux.removeLoadingView();
            }
        });
    }
    //endregion

    //region set recycler adapter
    private void setRecyclerAdapter(){
        recyclerTenantListAdapter = new RecyclerTenantListAdapter(tenantList, this);
        activityTenantListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityTenantListBinding.mRecylerView.setAdapter(recyclerTenantListAdapter);
        recyclerTenantListAdapter.notifyDataSetChanged();
        recyclerTenantListAdapter.setOnItemClickListener(new RecyclerTenantListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Tenant tenant) {
                startActivity(new Intent(TenantListActivity.this, NewTenantActivity.class).putExtra("tenant", tenant));
            }
        });
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
        dbHelperParent.close();
    }

    //endregion
}
