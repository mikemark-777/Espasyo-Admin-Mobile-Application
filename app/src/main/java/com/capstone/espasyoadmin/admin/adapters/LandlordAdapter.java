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
import com.capstone.espasyoadmin.models.Landlord;
import com.capstone.espasyoadmin.models.Property;

import java.util.ArrayList;

public class LandlordAdapter extends RecyclerView.Adapter<LandlordAdapter.LandlordViewHolder>{

    private Context context;
    private ArrayList<Landlord> landlordList;
    private LandlordAdapter.OnLandlordListener onLandlordListener;

    public LandlordAdapter(Context context, ArrayList<Landlord> landlordList, LandlordAdapter.OnLandlordListener onLandlordListener) {
        this.context = context;
        this.landlordList = landlordList;
        this.onLandlordListener = onLandlordListener;
    }

    @NonNull
    @Override
    public LandlordAdapter.LandlordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.landlord_item, parent, false);
        return new LandlordAdapter.LandlordViewHolder(view, onLandlordListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LandlordAdapter.LandlordViewHolder holder, int position) {
        Landlord landlord = landlordList.get(position);
        holder.landlordName.setText(landlord.getFirstName() + " " + landlord.getLastName());
        holder.landlordEmail.setText(landlord.getEmail());
    }

    @Override
    public int getItemCount() {
        return landlordList.size();
    }

    public static class LandlordViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        TextView landlordName, landlordEmail;
        LandlordAdapter.OnLandlordListener onLandlordListener;

        public LandlordViewHolder(@NonNull View itemView, LandlordAdapter.OnLandlordListener onLandlordListener) {
            super(itemView);
            landlordName = itemView.findViewById(R.id.landlordName_landlordItem);
            landlordEmail = itemView.findViewById(R.id.landlordEmail_landlordItem);
            this.onLandlordListener = onLandlordListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onLandlordListener.onLandlordClick(getAdapterPosition());
        }
    }

    public interface OnLandlordListener {
        void onLandlordClick(int position);
    }

}
