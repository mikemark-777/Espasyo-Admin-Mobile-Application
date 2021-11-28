package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.models.Room;

public class RoomDetailsActivity extends AppCompatActivity {

    //room object
    private Room chosenRoom;
    private TextView roomName, roomPrice, roomNumberOfPerson, roomIsAvailable;

    private ImageView hasBathroomImageView, hasKitchenImageView;
    private CardView btnLeaveRoom;
    final String AVAILABLE = "Available";
    final String UNAVAILABLE = "Unavailable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_room_details);

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

        btnLeaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initializeViews() {
        roomName = findViewById(R.id.roomName_display);
        roomPrice = findViewById(R.id.roomPrice_display);
        roomNumberOfPerson = findViewById(R.id.roomNumberOfPerson_display);
        roomIsAvailable = findViewById(R.id.roomAvailability_display);

        hasBathroomImageView = findViewById(R.id.icon_hasBathroom);
        hasKitchenImageView = findViewById(R.id.icon_hasKitchen);

        btnLeaveRoom = findViewById(R.id.btnLeaveRoom);
    }


    public void getDataFromIntent(Intent intent) {
        chosenRoom = intent.getParcelableExtra("chosenRoom");

        String name = chosenRoom.getRoomName();
        int price = chosenRoom.getPrice();
        int numberOfPerson = chosenRoom.getNumberOfPersons();
        boolean isAvailable = chosenRoom.getIsAvailable();
        boolean hasBathroom = chosenRoom.getHasBathRoom();
        boolean hasKitchen = chosenRoom.getHasKitchen();

        roomName.setText(name);
        roomPrice.setText(String.valueOf(price));
        roomNumberOfPerson.setText(String.valueOf(numberOfPerson));

        if (!isAvailable) {
            roomIsAvailable.setText(UNAVAILABLE);
            roomIsAvailable.setTextColor(this.getResources().getColor(R.color.espasyo_red_200));
        } else {
            roomIsAvailable.setText(AVAILABLE);
            roomIsAvailable.setTextColor(this.getResources().getColor(R.color.espasyo_green_200));
        }

        if (!hasBathroom) {
            hasBathroomImageView.setImageResource(R.drawable.icon_no_bathroom);
        }

        if (!hasKitchen) {
            hasKitchenImageView.setImageResource(R.drawable.icon_no_kitchen);
        }

    }
}