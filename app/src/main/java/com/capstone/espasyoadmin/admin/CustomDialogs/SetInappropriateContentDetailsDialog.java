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


        //buttons
        btnConfirm = view.findViewById(R.id.btnConfirmSetInappropriateContentDetails);
        btnCancel = view.findViewById(R.id.SetInappropriateContentDetails);
    }

    // input validations

   /* public String getInappropriateContentDetails() {

    }*/

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
