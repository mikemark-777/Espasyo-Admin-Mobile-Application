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
    private ArrayList<Property> propertyList;
    private OnPropertyListener onPropertyListener;

    public PropertyAdapter(Context context, ArrayList<Property> propertyList, OnPropertyListener onPropertyListener) {
        this.context = context;
        this.propertyList = propertyList;
        this.onPropertyListener = onPropertyListener;
    }

    @NonNull
    @Override
    public PropertyAdapter.PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        return new PropertyViewHolder(view, onPropertyListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyAdapter.PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.propertyName.setText(property.getName());
        holder.propertyAddress.setText(property.getAddress());
        holder.propertyType.setText(property.getPropertyType());
        holder.minimumPrice.setText(String.valueOf(property.getMinimumPrice()));
        holder.maximumPrice.setText(String.valueOf(property.getMaximumPrice()));


        if(property.isVerified()) {
            holder.verifiedIcon.setImageResource(R.drawable.icon_verified);
        } else {
            holder.verifiedIcon.setImageResource(R.drawable.icon_unverified);
        }

    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public int getTotalApartments() {
        int totalApartments = 0;
        for(Property propertyObj : propertyList) {
            if(propertyObj.getPropertyType().equals("Apartment")) {
                totalApartments+=1;
            }
        }

        return totalApartments;
    }

    public static class PropertyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        TextView propertyName, propertyAddress, propertyType, minimumPrice, maximumPrice;
        ImageView verifiedIcon;
        OnPropertyListener onPropertyListener;

        public PropertyViewHolder(@NonNull View itemView, OnPropertyListener onPropertyListener) {
            super(itemView);
            propertyName = itemView.findViewById(R.id.propertyName);
            propertyAddress = itemView.findViewById(R.id.propertyAddress);
            propertyType = itemView.findViewById(R.id.propertyType);
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