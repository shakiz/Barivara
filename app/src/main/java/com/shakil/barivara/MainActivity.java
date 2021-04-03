package com.shakil.barivara;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText Data1, Data2, Data3;
    Button Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("demo");

        Data1 = findViewById(R.id.Data1);
        Data2 = findViewById(R.id.Data2);
        Data3 = findViewById(R.id.Data3);
        Submit = findViewById(R.id.Submit);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Demo demo = new Demo();
                demo.setData1(Data1.getText().toString());
                demo.setData2(Data2.getText().toString());
                demo.setData3(Data3.getText().toString());
                Log.i("shakil-dev",""+databaseReference.getParent());
                String tenantId = databaseReference.push().getKey();
                databaseReference.child(tenantId).setValue(demo);
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }
        });

    }
}