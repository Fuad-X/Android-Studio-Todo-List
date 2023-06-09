package com.example.todo_list;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class AddNewTaskInputForm extends Fragment {
    View view;
    int hour, minute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_new_task_input_form, container, false);

        EditText titeditText = view.findViewById(R.id.titleEditTextId);
        EditText deseditText = view.findViewById(R.id.descriptionEditTextId);
        Button btn = view.findViewById(R.id.addBtnId);
        Button selectTimeBtn = view.findViewById(R.id.chooseTimeBtnId);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titeditText.getText().toString();
                String description = deseditText.getText().toString();
                String deadline = selectTimeBtn.getText().toString();
                Tasks task = new Tasks(title, description, deadline, false);

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference root = db.getReference("Tasks");

                String unique_id = root.push().getKey();

                if (unique_id != null) {
                    root.child(unique_id).setValue(task);
                    //root.child(unique_id).child("Title").setValue(title);
                    //root.child(unique_id).child("Description").setValue(description);
                }
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        selectTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        selectTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute, false);
                timePickerDialog.setTitle("Select your deadline");
                timePickerDialog.show();
            }
        });

        return view;
    }
}