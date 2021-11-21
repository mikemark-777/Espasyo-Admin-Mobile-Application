package com.capstone.espasyoadmin.auth.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.auth.viewmodels.AuthViewModel;
import com.capstone.espasyoadmin.models.Admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;


public class UpdateEmailFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextInputLayout textInputCurrentEmailLayout,
            textInputNewEmailLayout,
            textInputPasswordLayout;

    private TextInputEditText textInputCurrentEmailAddress,
            textInputNewEmailAddress,
            textInputPassword;

    private Button btnChangeEmail,
            btnCancelChangeEmail;
    private ProgressBar updateEmailProgressBar;
    private AuthViewModel viewModel;
    private NavController navController;
    private Boolean isEmailChanged = false;

    private boolean currentEmailExists = false;
    private boolean newEmailExists = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);

        //check each time if there is a logged user
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {

                    currentUser = firebaseUser;

                    if (isEmailChanged) {
                        isEmailChanged = false;
                        navController.navigate(R.id.action_updateEmailFragment_to_emailVerificationFragment);
                    }
                } else {
                    // by default will navigate to login fragment if firebaseUser is null
                    navController.navigate(R.id.action_updateEmailFragment_to_loginFragment);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.auth_fragment_update_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        // TextInputLayouts
        textInputCurrentEmailLayout = view.findViewById(R.id.text_input_currentemail_layout_updateemail);
        textInputNewEmailLayout = view.findViewById(R.id.text_input_newemail_layout_updateemail);
        textInputPasswordLayout = view.findViewById(R.id.text_input_password_layout_updateemail);

        //TextInputEditTexts
        textInputCurrentEmailAddress = view.findViewById(R.id.text_input_currentemail_updateemail);
        textInputNewEmailAddress = view.findViewById(R.id.text_input_newemail_updateemail);
        textInputPassword = view.findViewById(R.id.text_input_password_updateemail);

        btnChangeEmail = view.findViewById(R.id.btnChangeEmail);
        btnCancelChangeEmail = view.findViewById(R.id.btnCancelChangeEmail);
        //progress bar
        updateEmailProgressBar = view.findViewById(R.id.updateEmailProgressBar);

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentEmailAddress = textInputCurrentEmailAddress.getText().toString();
                String newEmailAddress = textInputNewEmailAddress.getText().toString();
                String password = textInputPassword.getText().toString();

                if (confirmInput(currentEmailAddress, newEmailAddress, password)) {
                    btnChangeEmail.setEnabled(false);
                    checkIfCurrentEmailExist(currentEmailAddress);
                    checkIfNewEmailExist(newEmailAddress);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnChangeEmail.setEnabled(true);
                            if (currentEmailExists) {
                                if (!newEmailExists) {
                                    //check if newEmail already exists
                                    updateEmailProgressBar.setVisibility(View.VISIBLE);
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateEmailProgressBar.setVisibility(View.INVISIBLE);
                                            viewModel.updateEmailAddress(currentUser, currentEmailAddress, newEmailAddress, password);
                                            isEmailChanged = true;
                                        }
                                    }, 4000);
                                } else {
                                    textInputNewEmailLayout.setError("Email already exist");
                                }
                            } else {
                                textInputCurrentEmailLayout.setError("Current Email do not exist");
                            }
                        }
                    }, 5000);

                } else {
                    Toast.makeText(getActivity(), "Please fill out everything", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnCancelChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_updateEmailFragment_to_emailVerificationFragment);
            }
        });

    }

    // Functions
    // ------ input validations -------------------------------
    public final String TAG = "TESTING";

    private boolean isCurrentEmailAddressEmpty(String email) {
        if (email.isEmpty()) {
            textInputCurrentEmailLayout.setError("Current Email Address field cannot be empty");
            Log.d(TAG, "CURRENT EMAIL: EMPTY");
            return false;
        } else {
            textInputCurrentEmailLayout.setError(null);
            Log.d(TAG, "CURRENT EMAIL: NOT EMPTY");
            return true;
        }
    }

    private boolean isNewEmailAddressEmpty(String email) {
        if (email.isEmpty()) {
            textInputNewEmailLayout.setError("New Email Address field cannot be empty");
            Log.d(TAG, "NEW EMAIL: EMPTY");
            return false;
        } else {
            textInputNewEmailLayout.setError(null);
            Log.d(TAG, "NEW EMAIL: NOT EMPTY");
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            textInputPasswordLayout.setError("Password field cannot be empty");
            Log.d(TAG, "PASSWORD: EMPTY");
            return false;
        } else {
            textInputPasswordLayout.setError(null);
            Log.d(TAG, "PASSWORD: NOT EMPTY");
            return true;
        }
    }

    private boolean confirmInput(String currentEmailAddress, String newEmailAddress, String password) {
        boolean currentEmailAddressResult = isCurrentEmailAddressEmpty(currentEmailAddress);
        boolean newEmailAddressResult = isNewEmailAddressEmpty(newEmailAddress);
        boolean passwordResult = validatePassword(password);

        if (currentEmailAddressResult == true && newEmailAddressResult == true && passwordResult == true) {
            Log.d(TAG, "CAN PROCEED: TRUE");
            return true;
        } else {
            Log.d(TAG, "CAN PROCEED: FALSE");
            return false;
        }
    }

    public void checkIfCurrentEmailExist(String currentEmail) {
        //check if newEmail already exists
        firebaseAuth.fetchSignInMethodsForEmail(currentEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().size() == 0) {
                        currentEmailExists = false;
                    } else {
                        currentEmailExists = true;
                        //textInputCurrentEmailLayout.setError("Email already exists");
                    }
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void checkIfNewEmailExist(String newEmail) {
        firebaseAuth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().size() == 0) {
                        newEmailExists = false;
                    } else {
                        newEmailExists = true;
                        //textInputNewEmailLayout.setError("Email already exists");
                    }
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void resetInputs() {
        textInputCurrentEmailAddress.setText("");
        textInputNewEmailAddress.setText("");
        textInputPassword.setText("");
    }

}