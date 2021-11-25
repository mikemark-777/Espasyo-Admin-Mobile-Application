package com.capstone.espasyoadmin.auth.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.auth.viewmodels.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordFragment extends Fragment {

    private FirebaseConnection firebaseConnection;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;

    private AuthViewModel viewModel;
    private NavController navController;

    public static final String SHARED_PREFS = "sharedPrefsAdmin";
    public static final String USER_ROLE = "userRole";
    public static final String ADMIN_RESET_PASSWORD = "adminResetPassword";

    private Button btnSendLink, btnCancelResetPassword;
    private ProgressBar resetPasswordProgressBar;

    private TextInputLayout textInputEmailLayout;
    private TextInputEditText textInputEmail;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseConnection = FirebaseConnection.getInstance();
        firebaseAuth = firebaseConnection.getFirebaseAuthInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.auth_fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        btnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textInputEmail.getText().toString();
                if(isEmailValid(email)) {
                    btnSendLink.setEnabled(false);
                    resetPasswordProgressBar.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetPasswordProgressBar.setVisibility(View.INVISIBLE);
                            viewModel.sendResetPasswordLink(email);
                            saveDidResetPassword(true);
                            navController.navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
                            btnSendLink.setEnabled(true);
                        }
                    }, 4000);
                }
            }
        });

        btnCancelResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
            }
        });
    }

    // --------------------------------------------  FUNCTIONS  -------------------------------------------------------//


    public void initializeViews(View view) {
        textInputEmailLayout = view.findViewById(R.id.text_input_email_layout_resetPassword);
        textInputEmail = view.findViewById(R.id.text_input_email_resetPassword);

        btnSendLink = view.findViewById(R.id.btnSendLinkToEmail);
        btnCancelResetPassword = view.findViewById(R.id.btnCancelResetPassword);
        resetPasswordProgressBar = view.findViewById(R.id.resetPasswordProgressBar);
        navController = Navigation.findNavController(view);
    }

    // ------ input validations -------------------------------

    private boolean isEmailValid(String email) {
        if (!email.isEmpty()) {
            textInputEmailLayout.setError(null);
            return true;
        } else {
            textInputEmailLayout.setError("Email address field cannot be empty");
            return false;
        }
    }

    //save sharedPreference to inform login fragment that the user has resetted his password
    public void saveDidResetPassword(boolean didReset) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(ADMIN_RESET_PASSWORD, didReset);
        editor.apply();
    }
}