package com.example.todo_list;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ViewTaskFragment extends Fragment {
    View view;
    int hour, minute;
    LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_task, container, false);

        Bundle bundle = getArguments();

        EditText titleEditText = view.findViewById(R.id.titleTextId);
        EditText descriptionEditText = view.findViewById(R.id.descriptionTextId);
        Button deadlineBtn = view.findViewById(R.id.deadlineBtnId);
        deadlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        deadlineBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute, false);
                timePickerDialog.setTitle("Select your deadline");
                timePickerDialog.show();
            }
        });

        TextView statusTextView = view.findViewById(R.id.statusTextId);

        EditText newSubTaskTitle = view.findViewById(R.id.newSubTaskTitleId);
        Button addSubtaskBtn = view.findViewById(R.id.addSubtaskBtnId);

        linearLayout = view.findViewById(R.id.subTaskLinearLayoutId);

        addSubtaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference root = db.getReference("Tasks");

                if(bundle.getString("key")!= null) root = root.child(bundle.getString("key"));

                root = root.child("subtasks");

                String unique_id = root.push().getKey();

                if (unique_id != null) {
                    root.child(unique_id).setValue(newSubTaskTitle.getText().toString());
                }

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference root = db.getReference("Tasks");
        root = root.child(bundle.getString("key")).child("subtasks");
        DatabaseReference finalRoot = root;
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String key = snapshot1.getKey();
                    Object value = snapshot1.getValue();

                    View rowLayout = getLayoutInflater().inflate(R.layout.activity_subtask, null);

                    TextView subTaskTitle = rowLayout.findViewById(R.id.subTaskTitleId);
                    subTaskTitle.setText(value.toString());

                    Button deleteBtn = rowLayout.findViewById(R.id.removeSubtaskBtnId);
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finalRoot.child(key).removeValue();
                        }
                    });

                    linearLayout.addView(rowLayout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (bundle!=null){
            titleEditText.setText(bundle.getString("title"));
            descriptionEditText.setText(bundle.getString("description"));
            statusTextView.setText(bundle.getString("status"));
            deadlineBtn.setText(bundle.getString("deadline"));
        }

        Button updateBtn = view.findViewById(R.id.updateBtnId);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String deadline = deadlineBtn.getText().toString();
                Tasks task = new Tasks(title, description, deadline, false);

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference root = db.getReference("Tasks");

                root.child(bundle.getString("key")).setValue(task);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        return view;
    }
}