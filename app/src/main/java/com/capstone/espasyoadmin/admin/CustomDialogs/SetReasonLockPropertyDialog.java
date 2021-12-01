package com.capstone.espasyoadmin.admin.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

public class SetReasonLockPropertyDialog extends DialogFragment{

    private ConfirmSetReasonLockPropertyListener listener;
    private LayoutInflater inflater;

    private CheckBox lockedReason1, lockedReason2, lockedReason3;
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
                Toast.makeText(getActivity(), "btn Confirm", Toast.LENGTH_SHORT).show();
                /*String contentDetails = getInappropriateContentDetails();
                if(!isInputEmpty(contentDetails)) {
                    listener.getConfirmedReasonLockProperty(contentDetails);
                    createdSetReasonLockPropertyDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please specify reason why this property will be locked", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdSetReasonLockPropertyDialog.dismiss();
                Toast.makeText(getActivity(), "btn Cancel", Toast.LENGTH_SHORT).show();
                //listener.cancelSetReasonLockProperty();
            }
        });
        return createdSetReasonLockPropertyDialog;
    }

    public void initializeDialogUI(View view) {

        //checkboxes
        lockedReason1 = view.findViewById(R.id.lockedReason1);
        lockedReason2 = view.findViewById(R.id.lockedReason2);
        lockedReason3 = view.findViewById(R.id.lockedReason3);
        editTextOtherlockedReason= view.findViewById(R.id.editTextOtherLockedReason);
        //buttons
        btnConfirm = view.findViewById(R.id.btnConfirmSetReasonLockProperty);
        btnCancel = view.findViewById(R.id.btnCancelSetReasonLockProperty);
    }

    // input validations

    public boolean isInputEmpty(String inappropriateContentDetails) {
        if(inappropriateContentDetails.equals("")) {
            return true;
        } else {
            return false;
        }
    }

   /* public String getInappropriateContentDetails() {
        String inappropriateContentDetails = "";
        if(inappropriateDetail1.isChecked()) {
            inappropriateContentDetails += "- \t" + inappropriateDetail1.getText().toString() + "\n";
        }

        if(inappropriateDetail2.isChecked()) {
            inappropriateContentDetails += "- \t" +  inappropriateDetail2.getText().toString() + "\n";
        }
        String otherInappropriateContentDetails = editTextOtherInappropriateDetails.getText().toString();
        if (!otherInappropriateContentDetails.equals("")) {
            return inappropriateContentDetails += "- \t" +  otherInappropriateContentDetails;
        } else {
            return inappropriateContentDetails;
        }
    }*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmSetReasonLockPropertyListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+
            "must implement ConfirmedLocationDialogListener");
        }
    }

    public interface ConfirmSetReasonLockPropertyListener {
        void getConfirmedReasonLockProperty(String inappropriateContentDetails);
        void cancelSetReasonLockProperty();
    }
}
