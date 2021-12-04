package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.CustomDialogs.SetReasonLockPropertyDialog;
import com.capstone.espasyoadmin.admin.adapters.RoomAdapter;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.admin.widgets.RoomRecyclerView;
import com.capstone.espasyoadmin.models.ImageFolder;
import com.capstone.espasyoadmin.models.Landlord;
import com.capstone.espasyoadmin.models.Property;
import com.capstone.espasyoadmin.models.Room;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PropertyDetailsActivity extends AppCompatActivity implements RoomAdapter.OnRoomListener, SetReasonLockPropertyDialog.ConfirmSetReasonLockPropertyListener {

    //i have added comment here

    private FirebaseConnection firebaseConnection;
    private FirebaseAuth fAuth;
    private FirebaseFirestore database;

    //property id and object
    private String propertyID;
    private Property property;

    private RoomRecyclerView roomRecyclerView;
    private RoomAdapter roomAdapter;
    private ArrayList<Room> propertyRooms;
    private View roomRecylerViewEmptyState;
    private View showAllRooms;
    private ImageView imageButtonViewPropertyOnMap;

    //for property image
    private ImageFolder propertyImageFolder;
    private ImageSlider propertyImageSlider;
    private CustomProgressDialog progressDialog;
    private ArrayList<String> downloadedURLs = new ArrayList<>();
    private ImageView btnZoomImage;
    private int imageIndex = 0;
    private ImageView emptyImagesDisplay;

    //for locking property
    private ConstraintLayout lockPropertyLinearLayout;
    private SwitchCompat lockPropertySwitch;
    private ImageView lockedImageDisplay;
    private TextView btnViewLockedDetails;

    private boolean willShowLockDialog = true;
    private boolean willShowUnlockDialog = true;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_property_details);

        //Initialize FirebaseConnection, FirebaseAuth and FirebaseFirestore
        firebaseConnection = FirebaseConnection.getInstance();

        fAuth = firebaseConnection.getFirebaseAuthInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();
        propertyRooms = new ArrayList<>();

        initRoomRecyclerView();
        loadPropertyData();
        fetchPropertyRooms();

        showAllRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyDetailsActivity.this, ShowAllRoomsActivity.class);
                intent.putExtra("propertyID", propertyID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        imageButtonViewPropertyOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyDetailsActivity.this, ViewPropertyOnMapActivity.class);
                intent.putExtra("chosenProperty", property);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        propertyImageSlider.setItemChangeListener(new ItemChangeListener() {
            @Override
            public void onItemChanged(int i) {
                imageIndex = i;
            }
        });

        btnZoomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadedURLs.size() > 0) {
                    Intent intent = new Intent(PropertyDetailsActivity.this, PreviewImageActivity.class);
                    intent.putExtra("previewImage", downloadedURLs.get(imageIndex));
                    startActivity(intent);
                }
            }
        });

        lockPropertySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(willShowLockDialog) {
                        showSetInappropriateContentDetailsDialog();
                    }
                    displayLockedUI();
                } else {
                    if(willShowUnlockDialog) {
                        showConfirmUnlockPropertyDialog(property);
                    }
                    displayUnlockedUI();
                }
            }
        });

        btnViewLockedDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyDetailsActivity.this, ViewReasonLockedPropertyActivity.class);
                intent.putExtra("property", property);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    //Load Property Details
    public void loadPropertyData() {
        //get data from intent
        Intent intent = getIntent();
        property = intent.getParcelableExtra("property");
        getImageFolderOf(property);

        propertyID = property.getPropertyID();
        String landlordID = property.getOwner();
        boolean isVerified = property.isVerified();
        boolean isLocked = property.isLocked();

        //get landlord data
        getLandlord(landlordID);

        String name = property.getName();
        String propertyType = property.getPropertyType();
        String address = property.getAddress();
        int minimumPrice = property.getMinimumPrice();
        int maximumPrice = property.getMaximumPrice();
        boolean isElectricityIncluded = property.isElectricityIncluded();
        boolean isWaterIncluded = property.isWaterIncluded();
        boolean isInternetIncluded = property.isInternetIncluded();
        boolean isGarbageCollectionIncluded = property.isGarbageCollectionIncluded();

        TextView propName = findViewById(R.id.propertyNameDisplay);
        TextView propType = findViewById(R.id.propertyTypeDisplay);
        TextView propAddress = findViewById(R.id.propertyAddressDisplay);
        TextView propMinimumPrice = findViewById(R.id.propertyMinimumPriceDisplay);
        TextView propMaximumPrice = findViewById(R.id.propertyMaximumPriceDisplay);

        ImageView electricityImageView = findViewById(R.id.icon_electricity);
        ImageView waterImageView = findViewById(R.id.icon_water);
        ImageView internetImageView = findViewById(R.id.icon_internet);
        ImageView garbageCollectionImageView = findViewById(R.id.icon_garbage);

        if(isLocked) {
            displayLockedUI();
        } else {
            displayUnlockedUI();
        }

        if (!isElectricityIncluded) {
            electricityImageView.setImageResource(R.drawable.icon_no_electricity);
        }
        if (!isWaterIncluded) {
            waterImageView.setImageResource(R.drawable.icon_no_water);
        }
        if (!isInternetIncluded) {
            internetImageView.setImageResource(R.drawable.icon_no_internet);
        }
        if (!isGarbageCollectionIncluded) {
            garbageCollectionImageView.setImageResource(R.drawable.icon_no_garbage);
        }

        propName.setText(name);
        propType.setText(propertyType);
        propAddress.setText(address);
        propMinimumPrice.setText(Integer.toString(minimumPrice));
        propMaximumPrice.setText(Integer.toString(maximumPrice));

    }

    //initialize roomRecyclerView, layoutManager, and roomAdapter
    public void initRoomRecyclerView() {
        roomRecyclerView = (RoomRecyclerView) findViewById(R.id.roomsRecyclerView);
        roomRecylerViewEmptyState = findViewById(R.id.empty_room_state_propertyDetailsActivity_PDA);
        showAllRooms = findViewById(R.id.showAllRooms_propertyDetails);
        roomRecyclerView.showIfEmpty(roomRecylerViewEmptyState);
        roomRecyclerView.showIfRoomsAreGreaterThanSeven(showAllRooms);
        roomRecyclerView.setHasFixedSize(true);
        LinearLayoutManager roomLayoutManager = new LinearLayoutManager(PropertyDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        roomRecyclerView.setLayoutManager(roomLayoutManager);
        roomAdapter = new RoomAdapter(PropertyDetailsActivity.this, propertyRooms, this);
        roomRecyclerView.setAdapter(roomAdapter);

        //initialize views except from recyclerview
        btnZoomImage = findViewById(R.id.btnZoomImage_admin);
        propertyImageSlider = findViewById(R.id.image_slider_propertyDetails);
        progressDialog = new CustomProgressDialog(this);
        imageButtonViewPropertyOnMap =findViewById(R.id.imageButtonViewPropertyOnMap);
        emptyImagesDisplay = findViewById(R.id.emptyImagesDisplay_adminPropertyDetails);

        //for locking property
        lockPropertyLinearLayout = findViewById(R.id.lockPropertyLinearLayout);
        lockPropertySwitch = findViewById(R.id.lockPropertySwitch);
        lockedImageDisplay = findViewById(R.id.lockedImageDisplay);
        btnViewLockedDetails = findViewById(R.id.btnViewLockedDetails);
    }

    public void fetchPropertyRooms() {
        String ownerPropertyID = propertyID;
        CollectionReference roomsCollection = database.collection("properties").document(ownerPropertyID)
                .collection("rooms");
        roomsCollection
                .orderBy("roomName", Query.Direction.ASCENDING)
                .limit(7)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        propertyRooms.clear();
                        for (QueryDocumentSnapshot room : queryDocumentSnapshots) {
                            Room roomObj = room.toObject(Room.class);
                            propertyRooms.add(roomObj);
                        }
                        roomAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void getLandlord(String landlordID) {
        DocumentReference landlordDocRef = database.collection("landlords").document(landlordID);
        landlordDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Landlord landlord = documentSnapshot.toObject(Landlord.class);
                displayLandlordDetails(landlord);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PropertyDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void displayLandlordDetails(Landlord landlord) {
        TextView propLandlordName = findViewById(R.id.propertyLandlordNameDisplay);
        TextView propLandlordPhoneNumber = findViewById(R.id.propertyLandlordPhoneNumberDisplay);

        String landlordName = landlord.getFirstName() + " " + landlord.getLastName();
        String landlordPhoneNumber = landlord.getPhoneNumber();

        propLandlordName.setText(landlordName);
        propLandlordPhoneNumber.setText("+63" + landlordPhoneNumber);
    }

    //fetch the imageFolder from the property
    public void getImageFolderOf(Property property) {
        String imageFolderID = property.getImageFolder();
        //will check if the property has imageFolder, if not create an imageFolder
        if (imageFolderID != null) {
            DocumentReference imageFolderDocRef = database.collection("imageFolders").document(imageFolderID);
            progressDialog.showProgressDialog("Loading images...", false);
            imageFolderDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    propertyImageFolder = documentSnapshot.toObject(ImageFolder.class);
                    displayImagesFrom(propertyImageFolder);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PropertyDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PropertyDetailsActivity.this, "imageFolder of property is null", Toast.LENGTH_SHORT).show();
        }
    }

    //will display images from imageFolder of the property
    public void displayImagesFrom(ImageFolder imageFolder) {
        ArrayList<SlideModel> imageSlides = new ArrayList<>();
        propertyImageSlider.setImageList(imageSlides);
        if (imageFolder != null) {
            downloadedURLs = imageFolder.getImages();

            if (!downloadedURLs.isEmpty()) {
                emptyImagesDisplay.setVisibility(View.GONE);
                for (String url : downloadedURLs) {
                    imageSlides.add(new SlideModel(url, ScaleTypes.CENTER_INSIDE));
                }
                propertyImageSlider.setImageList(imageSlides);
                progressDialog.dismissProgressDialog();
            } else {
                emptyImagesDisplay.setVisibility(View.VISIBLE);
                progressDialog.dismissProgressDialog();
            }
        } else {
            Toast.makeText(PropertyDetailsActivity.this, "NULL", Toast.LENGTH_SHORT).show();
        }
    }

    //lock and unlock functions --------------------------------------------------------------------

    public void showSetInappropriateContentDetailsDialog() {
        SetReasonLockPropertyDialog setReasonLockPropertyDialog = new SetReasonLockPropertyDialog();
        setReasonLockPropertyDialog.setCancelable(false);
        setReasonLockPropertyDialog.show(getSupportFragmentManager(), "setReasonLockPropertyDialog");
    }

    public void showConfirmLockPropertyDialog(Property property ,ArrayList<String> reasonLocked) {
        LayoutInflater inflater = LayoutInflater.from(PropertyDetailsActivity.this);
        View view = inflater.inflate(R.layout.admin_confirm_lock_property, null);

        Button btnConfirmLockProperty = view.findViewById(R.id.btnConfirmLockProperty);
        Button btnCancelLockProperty = view.findViewById(R.id.btnCancelLockProperty);

        AlertDialog confirmLockDialog = new AlertDialog.Builder(PropertyDetailsActivity.this).setView(view).create();

        btnConfirmLockProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockProperty(property, reasonLocked);
                willShowUnlockDialog = true;
                confirmLockDialog.dismiss();
            }
        });
        btnCancelLockProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                willShowUnlockDialog = false;
                displayUnlockedUI();
                confirmLockDialog.dismiss();
            }
        });
        confirmLockDialog.show();
    }

    public void showConfirmUnlockPropertyDialog(Property property) {
        LayoutInflater inflater = LayoutInflater.from(PropertyDetailsActivity.this);
        View view = inflater.inflate(R.layout.admin_confirm_unlock_property, null);

        Button btnConfirmUnlockProperty = view.findViewById(R.id.btnConfirmUnlockProperty);
        Button btnCancelUnlockProperty = view.findViewById(R.id.btnCancelUnlockProperty);

        AlertDialog confirmUnlockDialog = new AlertDialog.Builder(PropertyDetailsActivity.this).setView(view).create();

        btnConfirmUnlockProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockProperty(property);
                willShowLockDialog = true;
                confirmUnlockDialog.dismiss();
            }
        });
        btnCancelUnlockProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                willShowLockDialog = false;
                displayLockedUI();
                confirmUnlockDialog.dismiss();
            }
        });
        confirmUnlockDialog.show();
    }

    public void lockProperty(Property property, ArrayList<String> reasonLocked) {
        progressDialog.showProgressDialog("Locking Property...", false);
        //lock the property and set the reason why property is locked
        property.setLocked(true);
        property.setReasonLocked(reasonLocked);

        String propertyID = property.getPropertyID();
        DocumentReference propertyDocRef = database.collection("properties").document(propertyID);

        propertyDocRef.set(property).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismissProgressDialog();
                Toast.makeText(PropertyDetailsActivity.this, "Property has been locked", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismissProgressDialog();
                Toast.makeText(PropertyDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unlockProperty(Property property) {
        progressDialog.showProgressDialog("Unlocking Property...", false);
        //lock the property and set the reason why property is locked
        property.setLocked(false);
        property.setReasonLocked(null);

        String propertyID = property.getPropertyID();
        DocumentReference propertyDocRef = database.collection("properties").document(propertyID);

        propertyDocRef.set(property).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismissProgressDialog();
                Toast.makeText(PropertyDetailsActivity.this, "Property has been unlocked", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismissProgressDialog();
                Toast.makeText(PropertyDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayLockedUI() {
        lockPropertySwitch.setChecked(true);
        lockPropertySwitch.setText("Unlock Property  ");
        lockPropertyLinearLayout.setBackgroundColor(getResources().getColor(R.color.espasyo_red_200));
        lockedImageDisplay.setImageResource(R.drawable.icon_property_locked);
        btnViewLockedDetails.setVisibility(View.VISIBLE);
    }

    public void displayUnlockedUI() {
        lockPropertySwitch.setChecked(false);
        lockPropertySwitch.setText("Lock Property  ");
        lockPropertyLinearLayout.setBackgroundColor(getResources().getColor(R.color.espasyo_blue_700));
        lockedImageDisplay.setImageResource(R.drawable.icon_property_unlocked);
        btnViewLockedDetails.setVisibility(View.INVISIBLE);
    }


    //propertyDetailActivity Lifecycle -------------------------------------------------------------

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchPropertyRooms();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRoomClick(int position) {
        Intent intent = new Intent(PropertyDetailsActivity.this, RoomDetailsActivity.class);
        intent.putExtra("chosenRoom", propertyRooms.get(position));
        startActivity(intent);
    }

    @Override
    public void getConfirmedReasonLockProperty(ArrayList<String> reasonLocked) {
        showConfirmLockPropertyDialog(property, reasonLocked);
    }

    @Override
    public void cancelSetReasonLockProperty() {
        willShowUnlockDialog = false;
        displayUnlockedUI();
    }

}