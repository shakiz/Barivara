package com.shakil.barivara.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.homeapp.R;
import com.shakil.homeapp.activities.activities.meter.NewMeterActivity;
import com.shakil.homeapp.activities.model.dashboard.Dashboard;

import java.util.ArrayList;

public class RecyclerMeterDashboardAdapter extends RecyclerView.Adapter<RecyclerMeterDashboardAdapter.ViewHolder> {

    private ArrayList<Dashboard> dashboardList;
    private Context context;

    public RecyclerMeterDashboardAdapter(ArrayList<Dashboard> dashboardList, Context context) {
        this.dashboardList = dashboardList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_layout_meter_dashboard,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.totalUnit.setText(dashboardList.get(position).getTotalUnit());
        holder.totalMeter.setText(dashboardList.get(position).getTotalMeter());
        holder.adddNewMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, NewMeterActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dashboardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView adddNewMeter;
        TextView totalMeter , totalUnit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adddNewMeter = itemView.findViewById(R.id.mAddMasterElectricityBill);
            totalMeter = itemView.findViewById(R.id.total_meter);
            totalUnit = itemView.findViewById(R.id.total_unit);
        }
    }
}
