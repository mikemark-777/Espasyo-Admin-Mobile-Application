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
import com.capstone.espasyoadmin.models.Property;

import java.util.ArrayList;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private Context context;
    private ArrayList<Property> ownedPropertyList;
    private OnPropertyListener onPropertyListener;

    public PropertyAdapter(Context context, ArrayList<Property> propertyList, OnPropertyListener onPropertyListener) {
        this.context = context;
        this.ownedPropertyList = propertyList;
        this.onPropertyListener = onPropertyListener;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        return new PropertyViewHolder(view, onPropertyListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = ownedPropertyList.get(position);
        holder.propertyName.setText(property.getName());
        holder.propertyAddress.setText(property.getAddress());
        holder.propertyType.setText(property.getPropertyType());
        holder.landlordName.setText(property.getLandlordName());
        holder.landlordContactNumber.setText(property.getLandlordPhoneNumber());
        holder.minimumPrice.setText(String.valueOf(property.getMinimumPrice()));
        holder.maximumPrice.setText(String.valueOf(property.getMaximumPrice()));


        if(property.getIsVerified()) {
            holder.verifiedIcon.setImageResource(R.drawable.icon_verified);
        } else {
            holder.verifiedIcon.setImageResource(R.drawable.icon_unverified);
        }

    }

    @Override
    public int getItemCount() {
        return ownedPropertyList.size();
    }


    public static class PropertyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        TextView propertyName, propertyAddress, propertyType, landlordName, landlordContactNumber, minimumPrice, maximumPrice;
        ImageView verifiedIcon;
        OnPropertyListener onPropertyListener;

        public PropertyViewHolder(@NonNull View itemView, OnPropertyListener onPropertyListener) {
            super(itemView);
            propertyName = itemView.findViewById(R.id.propertyName);
            propertyAddress = itemView.findViewById(R.id.propertyAddress);
            propertyType = itemView.findViewById(R.id.propertyType);
            landlordName = itemView.findViewById(R.id.landlordName);
            landlordContactNumber = itemView.findViewById(R.id.landlordContactNumber);
            minimumPrice = itemView.findViewById(R.id.minimumPrice_propertyItem);
            maximumPrice = itemView.findViewById(R.id.maximumPrice_propertyItem);
            verifiedIcon = itemView.findViewById(R.id.propertyItem_iconVerified);
            this.onPropertyListener = onPropertyListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPropertyListener.onPropertyClick(getAdapterPosition());
        }
    }

    public interface OnPropertyListener {
        void onPropertyClick(int position);
    }

}