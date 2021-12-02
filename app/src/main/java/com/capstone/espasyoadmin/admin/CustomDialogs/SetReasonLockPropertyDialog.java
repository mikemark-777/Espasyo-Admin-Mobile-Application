package com.capstone.espasyoadmin.admin.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.espasyoadmin.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SetReasonLockPropertyDialog extends DialogFragment {

    private ConfirmSetReasonLockPropertyListener listener;
    private LayoutInflater inflater;

    private CheckBox lockedReason1, lockedReason2, lockedReason3, lockedReason4, lockedReason5;
    private EditText editTextOtherlockedReason;

    //for buttons of the dialog
    private Button btnCancel, btnConfirm;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_set_reason_lock_property_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        initializeDialogUI(view);

        AlertDialog createdSetReasonLockPropertyDialog = builder.create();
        createdSetReasonLockPropertyDialog.setView(view);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> reasonLocked = getReasonLockedProperty();
                if(!reasonLocked.isEmpty()) {
                    listener.getConfirmedReasonLockProperty(reasonLocked);
                    createdSetReasonLockPropertyDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please specify reason why this property will be locked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdSetReasonLockPropertyDialog.dismiss();
                listener.cancelSetReasonLockProperty();
            }
        });

        return createdSetReasonLockPropertyDialog;
    }

    public void initializeDialogUI(View view) {

        //checkboxes
        lockedReason1 = view.findViewById(R.id.lockedReason1);
        lockedReason2 = view.findViewById(R.id.lockedReason2);
        lockedReason3 = view.findViewById(R.id.lockedReason3);
        lockedReason4 = view.findViewById(R.id.lockedReason4);
        lockedReason5 = view.findViewById(R.id.lockedReason5);

        editTextOtherlockedReason = view.findViewById(R.id.editTextOtherLockedReason);
        //buttons
        btnConfirm = view.findViewById(R.id.btnConfirmSetReasonLockProperty);
        btnCancel = view.findViewById(R.id.btnCancelSetReasonLockProperty);
    }

    public ArrayList<String> getReasonLockedProperty() {

        ArrayList<String> reasonLocked = new ArrayList<>();

        if (lockedReason1.isChecked()) {
            reasonLocked.add(lockedReason1.getText().toString());
        }

        if (lockedReason2.isChecked()) {
            reasonLocked.add(lockedReason2.getText().toString());
        }

        if (lockedReason3.isChecked()) {
            reasonLocked.add(lockedReason3.getText().toString());
        }

        if (lockedReason4.isChecked()) {
            reasonLocked.add(lockedReason4.getText().toString());
        }

        if (lockedReason5.isChecked()) {
            reasonLocked.add(lockedReason5.getText().toString());
        }
        String otherLockedReason = editTextOtherlockedReason.getText().toString();
        if (!otherLockedReason.equals("")) {
            reasonLocked.add(otherLockedReason);
        }

        return reasonLocked;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmSetReasonLockPropertyListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ConfirmedLocationDialogListener");
        }
    }

    public interface ConfirmSetReasonLockPropertyListener {
        void getConfirmedReasonLockProperty(ArrayList<String> reasonLocked);
        void cancelSetReasonLockProperty();
    }

}
