package com.capstone.espasyoadmin.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.models.VerificationRequest;

import java.util.ArrayList;

public class VerificationRequestAdapter extends RecyclerView.Adapter<VerificationRequestAdapter.VerificationRequestViewHolder> {

    private Context context;
    private ArrayList<VerificationRequest> propertyVerifications;
    private OnVerificationRequestListener onVerificationRequestListener;

    public VerificationRequestAdapter(Context context, ArrayList<VerificationRequest> propertyVerifications, OnVerificationRequestListener onVerificationRequestListener) {
        this.context = context;
        this.propertyVerifications = propertyVerifications;
        this.onVerificationRequestListener = onVerificationRequestListener;
    }

    @NonNull
    @Override
    public VerificationRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate((R.layout.admin_verification_request_item), parent, false);
        return  new VerificationRequestViewHolder(view, onVerificationRequestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VerificationRequestViewHolder holder, int position) {
        VerificationRequest verificationRequest = propertyVerifications.get(position);
        String propertyName = verificationRequest.getPropertyName();
        String dateSubmitted = verificationRequest.getDateSubmitted();
        String dateVerified = verificationRequest.getDateVerified();
        String status = verificationRequest.getStatus();
        String classification = verificationRequest.getClassification();

        if(status.equals("verified")) {
            holder.verifiedIconDisplay.setImageResource(R.drawable.icon_verified);
        } else if(status.equals("unverified")){
            holder.verifiedIconDisplay.setImageResource(R.drawable.icon_unverified);
        } else if(status.equals("declined")) {
            holder.verifiedIconDisplay.setImageResource(R.drawable.icon_declined);
        }

        holder.dateVerified.setText(dateVerified);
        holder.propertyName.setText(propertyName);
        holder.dateSubmitted.setText(dateSubmitted);

        if(classification.equals("new")) {
            holder.classification.setText("New");
        } else if(classification.equals("renew")) {
            holder.classification.setText("Renew");
        }
    }


    @Override
    public int getItemCount() {
        return propertyVerifications.size();
    }

    public static class  VerificationRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView propertyName, dateSubmitted, dateVerified, classification;
        ImageView verifiedIconDisplay;
        OnVerificationRequestListener onVerificationRequestListener;

        public VerificationRequestViewHolder(@NonNull View itemView, OnVerificationRequestListener onVerificationRequestListener) {
            super(itemView);
            propertyName = itemView.findViewById(R.id.propertyName_verification);
            dateSubmitted = itemView.findViewById(R.id.dateSubmitted_verification);
            dateVerified = itemView.findViewById(R.id.dateVerified_verification);
            verifiedIconDisplay = itemView.findViewById(R.id.verifiedIconDisplay);
            classification = itemView.findViewById(R.id.classification_verification);
            this.onVerificationRequestListener = onVerificationRequestListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onVerificationRequestListener.onVerificationRequestClick(getAdapterPosition());
        }
    }

    public interface OnVerificationRequestListener {
        void onVerificationRequestClick(int position);
    }

}
