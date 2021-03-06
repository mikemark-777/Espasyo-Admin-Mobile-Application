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

public class SetInappropriateContentDetailsDialog extends DialogFragment {

    private ConfirmSetInappropriateContentDetailsListener listener;
    private LayoutInflater inflater;

    private CheckBox inappropriateDetail1, inappropriateDetail2;
    private EditText editTextOtherInappropriateDetails;

    //for buttons of the dialog
    private Button btnCancel, btnConfirm;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_set_inappropriate_content_details_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        initializeDialogUI(view);


        AlertDialog createdSetInappropriateContentDetailsDialog = builder.create();
        createdSetInappropriateContentDetailsDialog.setView(view);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentDetails = getInappropriateContentDetails();
                if(!isInputEmpty(contentDetails)) {
                    listener.getConfirmedInappropriateContentDetails(contentDetails);
                    createdSetInappropriateContentDetailsDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please specify inappropriate content details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdSetInappropriateContentDetailsDialog.dismiss();
                listener.cancelSetInappropriateContentDetails();
            }
        });
        return createdSetInappropriateContentDetailsDialog;
    }

    public void initializeDialogUI(View view) {

        //checkboxes
        inappropriateDetail1 = view.findViewById(R.id.inappropriateDetail1);
        inappropriateDetail2 = view.findViewById(R.id.inappropriateDetail2);
        editTextOtherInappropriateDetails = view.findViewById(R.id.editTextOtherInappropriateDetails);
        //buttons
        btnConfirm = view.findViewById(R.id.btnConfirmSetInappropriateContentDetails);
        btnCancel = view.findViewById(R.id.cancelSetInappropriateContentDetails);
    }

    // input validations

    public boolean isInputEmpty(String inappropriateContentDetails) {
        if(inappropriateContentDetails.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public String getInappropriateContentDetails() {
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
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmSetInappropriateContentDetailsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+
            "must implement ConfirmedLocationDialogListener");
        }
    }

    public interface ConfirmSetInappropriateContentDetailsListener {
        void getConfirmedInappropriateContentDetails(String inappropriateContentDetails);
        void cancelSetInappropriateContentDetails();
    }
}
