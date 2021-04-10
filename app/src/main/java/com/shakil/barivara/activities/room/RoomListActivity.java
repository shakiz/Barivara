package com.shakil.barivara.activities.room;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.RecyclerRoomListAdapter;
import com.shakil.barivara.databinding.ActivityRoomListBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.mvvm.RoomModelMVVM;
import com.shakil.barivara.mvvm.RoomViewModel;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private ActivityRoomListBinding activityRoomListBinding;
    private RecyclerRoomListAdapter recyclerRoomListAdapter;
    private TextView noDataTXT;
    private ArrayList<Room> roomList;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;
    private ImageButton searchButton, refreshButton;
    private EditText searchName;

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

    private void binUiWIthComponents() {

        setData();

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
                if (!TextUtils.isEmpty(searchName.getText().toString())){
                    for (int start = 0; start < roomList.size(); start++) {
                        if (roomList.get(start).getRoomName().contains(searchName.getText().toString())){
                            recyclerRoomListAdapter.notifyDataSetChanged();
                            Toast.makeText(RoomListActivity.this, getString(R.string.filterd), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(RoomListActivity.this, getString(R.string.enter_data_validation), Toast.LENGTH_SHORT).show();
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
        recyclerRoomListAdapter = new RecyclerRoomListAdapter(roomList, this);
        activityRoomListBinding.mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        activityRoomListBinding.mRecylerView.setAdapter(recyclerRoomListAdapter);
        recyclerRoomListAdapter.notifyDataSetChanged();
        recyclerRoomListAdapter.setOnItemClickListener(new RecyclerRoomListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Room room) {
                startActivity(new Intent(RoomListActivity.this, RoomActivity.class).putExtra("room", room));
            }
        });
    }
    //endregion

    private void init() {
        searchButton = findViewById(R.id.searchButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchName = findViewById(R.id.SearchName);
        roomList = new ArrayList<>();
        ux = new UX(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        noDataTXT = findViewById(R.id.mNoDataMessage);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RoomListActivity.this,MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

