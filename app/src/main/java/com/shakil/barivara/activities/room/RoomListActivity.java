package com.shakil.barivara.activities.room;

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
import com.shakil.barivara.adapter.RecyclerRoomListAdapter;
import com.shakil.barivara.databinding.ActivityRoomListBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.FilterManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity {
    private ActivityRoomListBinding activityRoomListBinding;
    private ArrayList<Room> roomList;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;
    private Tools tools;
    private ImageButton searchButton, refreshButton;
    private EditText searchName;
    private FilterManager filterManager;
    private CustomAdManager customAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRoomListBinding = DataBindingUtil.setContentView(this, R.layout.activity_room_list);

        init();
        setSupportActionBar(activityRoomListBinding.toolBar);

        activityRoomListBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoomListActivity.this, MainActivity.class));
            }
        });

        binUiWIthComponents();
    }

    private void init() {
        searchButton = findViewById(R.id.searchButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchName = findViewById(R.id.SearchName);
        roomList = new ArrayList<>();
        ux = new UX(this);
        tools = new Tools(this);
        filterManager = new FilterManager();
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        customAdManager = new CustomAdManager(this);
    }

    private void binUiWIthComponents() {
        //region for ad
        customAdManager.generateAd(activityRoomListBinding.adView);
        //endregion

        searchName.setHint(getString(R.string.search_room_name));
        //region check internet connection
        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        //endregion

        activityRoomListBinding.mAddRoomMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RoomListActivity.this, RoomActivity.class));
            }
        });

        //region filter
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    if (!TextUtils.isEmpty(searchName.getText().toString())){
                        filterManager.onFilterClick(searchName.getText().toString(), roomList, new FilterManager.onFilterClick() {
                            @Override
                            public void onClick(ArrayList<Room> objects) {
                                if (objects.size() > 0) {
                                    roomList = objects;
                                    setRecyclerAdapter();
                                    Tools.hideKeyboard(RoomListActivity.this);
                                    Toast.makeText(RoomListActivity.this, getString(R.string.filterd), Toast.LENGTH_SHORT).show();
                                } else {
                                    Tools.hideKeyboard(RoomListActivity.this);
                                    activityRoomListBinding.mNoDataMessage.setVisibility(View.VISIBLE);
                                    activityRoomListBinding.mNoDataMessage.setText(R.string.no_data_message);
                                    activityRoomListBinding.mRecylerView.setVisibility(View.GONE);
                                    Toast.makeText(RoomListActivity.this, getString(R.string.no_data_message), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(RoomListActivity.this, getString(R.string.enter_data_validation), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RoomListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tools.hasConnection()) {
                    activityRoomListBinding.mRecylerView.setVisibility(View.VISIBLE);
                    searchName.setText("");
                    activityRoomListBinding.mNoDataMessage.setVisibility(View.GONE);
                    Tools.hideKeyboard(RoomListActivity.this);
                    setData();
                    Toast.makeText(RoomListActivity.this, getString(R.string.list_refreshed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RoomListActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion
    }

    //region set recycler data
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllRoom("room", new FirebaseCrudHelper.onRoomDataFetch() {
            @Override
            public void onFetch(ArrayList<Room> objects) {
                roomList = objects;
                if (roomList.size()<=0){
                    activityRoomListBinding.mNoDataMessage.setVisibility(View.VISIBLE);
                    activityRoomListBinding.mNoDataMessage.setText(R.string.no_data_message);
                }
                setRecyclerAdapter();
                ux.removeLoadingView();
            }
        });
    }
    //endregion

    //region set recycler adapter
    private void setRecyclerAdapter(){
        RecyclerRoomListAdapter recyclerRoomListAdapter = new RecyclerRoomListAdapter(roomList);
        activityRoomListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityRoomListBinding.mRecylerView.setAdapter(recyclerRoomListAdapter);
        recyclerRoomListAdapter.notifyDataSetChanged();
        recyclerRoomListAdapter.setOnItemClickListener(new RecyclerRoomListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Room room) {
                startActivity(new Intent(RoomListActivity.this, RoomActivity.class).putExtra("room", room));
            }
        });
        recyclerRoomListAdapter.onEditListener(new RecyclerRoomListAdapter.onEditListener() {
            @Override
            public void onEdit(Room room) {
                startActivity(new Intent(RoomListActivity.this, RoomActivity.class).putExtra("room", room));
            }
        });
        recyclerRoomListAdapter.onDeleteListener(new RecyclerRoomListAdapter.onDeleteListener() {
            @Override
            public void onDelete(Room room) {
                doPopUpForDeleteConfirmation(room);
            }
        });
    }
    //endregion

    //region ask to delete confirmation
    private void doPopUpForDeleteConfirmation(Room room){
        Button cancel, delete;
        Dialog dialog = new Dialog(RoomListActivity.this, android.R.style.Theme_Dialog);
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
                firebaseCrudHelper.deleteRecord("room",room.getFireBaseKey());
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RoomListActivity.this,MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

