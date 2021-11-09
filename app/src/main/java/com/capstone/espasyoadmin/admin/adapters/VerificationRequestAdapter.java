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
    private ArrayList<VerificationRequest> ownedPropertyVerifications;
    private OnVerificationRequestListener onVerificationRequestListener;

    public VerificationRequestAdapter(Context context, ArrayList<VerificationRequest> ownedPropertyVerifications, OnVerificationRequestListener onVerificationRequestListener) {
        this.context = context;
        this.ownedPropertyVerifications = ownedPropertyVerifications;
        this.onVerificationRequestListener = onVerificationRequestListener;
    }

    @NonNull
    @Override
    public VerificationRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate((R.layout.landlord_verification_request_item), parent, false);
        return  new VerificationRequestViewHolder(view, onVerificationRequestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VerificationRequestViewHolder holder, int position) {
        VerificationRequest verificationRequest = ownedPropertyVerifications.get(position);
        String propertyName = verificationRequest.getPropertyName();
        String dateSubmitted = verificationRequest.getDateSubmitted();
        String dateVerified = verificationRequest.getDateVerified();
        boolean isVerified = verificationRequest.isVerified();

        if(isVerified == false) {
            holder.verifiedIconDisplay.setImageResource(R.drawable.icon_unverified);
        } else {
            holder.verifiedIconDisplay.setImageResource(R.drawable.icon_verified);
        }

        holder.dateVerified.setText(dateVerified);
        holder.propertyName.setText(propertyName);
        holder.dateSubmitted.setText(dateSubmitted);
    }


    @Override
    public int getItemCount() {
        return ownedPropertyVerifications.size();
    }

    public static class  VerificationRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView propertyName, propertyAddress, propertyType, dateSubmitted, dateVerified;
        ImageView verifiedIconDisplay;
        OnVerificationRequestListener onVerificationRequestListener;

        public VerificationRequestViewHolder(@NonNull View itemView, OnVerificationRequestListener onVerificationRequestListener) {
            super(itemView);
            propertyName = itemView.findViewById(R.id.propertyName_verification);
            propertyAddress = itemView.findViewById(R.id.propertyAddress_verification);
            propertyType = itemView.findViewById(R.id.propertyType_verification);
            dateSubmitted = itemView.findViewById(R.id.dateSubmitted_verification);
            dateVerified = itemView.findViewById(R.id.dateVerified_verification);
            verifiedIconDisplay = itemView.findViewById(R.id.verifiedIconDisplay);
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
