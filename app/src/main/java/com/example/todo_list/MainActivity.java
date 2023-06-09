package com.example.todo_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.taskLinearLayoutId);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference root = db.getReference("Tasks");
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Access each child node
                    String key = snapshot.getKey();
                    Tasks value = snapshot.getValue(Tasks.class);

                    View rowLayout = getLayoutInflater().inflate(R.layout.activity_task_row, null);

                    TextView textViewTitle = rowLayout.findViewById(R.id.taskTitleTextViewId);
                    textViewTitle.setText(value.getTitle());

                    CheckBox statusCheckBox = rowLayout.findViewById(R.id.statusCheckBoxId);
                    statusCheckBox.setChecked(value.isStatus());
                    statusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            root.child(key).child("status").setValue(b);
                        }
                    });

                    Button viewBtn = rowLayout.findViewById(R.id.viewTaskBtnId);
                    FrameLayout frameLayout = rowLayout.findViewById(R.id.viewTaskFrameLayoutId);
                    frameLayout.setId(View.generateViewId());

                    viewBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getSupportFragmentManager();
                            Fragment isFrag = fm.findFragmentById(frameLayout.getId());
                            FragmentTransaction ft = fm.beginTransaction();

                            if(isFrag == null) {
                                Fragment fragment = new ViewTaskFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("title", value.getTitle());
                                bundle.putString("description", value.getDescription());
                                bundle.putString("deadline", value.getDeadline());
                                bundle.putString("status", (value.isStatus()==true?"Done":"ToDo"));
                                bundle.putString("key", key);
                                fragment.setArguments(bundle);
                                ft.replace(frameLayout.getId(), fragment);
                                ft.commit();
                            }
                            else {
                                ft.remove(isFrag);
                                ft.commit();
                            }
                        }
                    });


                    Button deleteBtn = rowLayout.findViewById(R.id.deleteTaskBtnId);
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            root.child(key).removeValue();

                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();
                        }
                    });

                    linearLayout.addView(rowLayout);

                    // Process the data as needed
                    //Log.d("FirebaseEntry", "Key: " + key + ", Value: " + value.isStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewTask(View view) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment isFrag = fm.findFragmentById(R.id.frameLayoutId);
            FragmentTransaction ft = fm.beginTransaction();

            if(isFrag == null) {

                ft.replace(R.id.frameLayoutId, new AddNewTaskInputForm());
                ft.commit();
            }
            else {
                ft.remove(isFrag);
                ft.commit();
            }
    }
}