package com.capstone.espasyoadmin.auth.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.auth.viewmodels.AuthViewModel;
import com.capstone.espasyoadmin.admin.AdminMainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class LoginFragment extends Fragment {

    public static final String SHARED_PREFS = "sharedPrefsAdmin";
    public static final String USER_ROLE = "userRole";
    private int userRole;

    private final int ADMIN_CODE = 1;

    private TextInputLayout textInputEmailLayout, textInputPasswordLayout;
    private TextInputEditText textInputEmail, textInputPassword;
    private Button btnLogin;
    private TextView gotoSignUp, btnForgotPassword;
    private ProgressBar loginProgressBar;

    private AuthViewModel viewModel;
    private NavController navController;

    private FirebaseFirestore database;
    private DocumentReference userReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        database = FirebaseFirestore.getInstance();

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);

        //check each time if there is a logged user
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    if (firebaseUser.isEmailVerified()) {

                        if (getUserRole() != 0) {
                            int uRole = getUserRole();

                            //Navigate to admin module
                            if (uRole == ADMIN_CODE) {
                                Intent intent = new Intent(getActivity(), AdminMainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                viewModel.signOut();
                                resetUserRole();
                            }
                        } else {
                            //Get currentUser's UID
                            String UID = firebaseUser.getUid();
                            userReference = database.collection("users").document(UID);

                            userReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                    DocumentSnapshot user = value;

                                    if (user != null) {
                                        int userRole = user.getLong("userRole").intValue();

                                        //Navigate to admin module
                                        if (userRole == ADMIN_CODE) {
                                            saveUserRole(userRole);
                                            Intent intent = new Intent(getActivity(), AdminMainActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            viewModel.signOut();
                                            resetUserRole();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "No admin with such credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        navController.navigate(R.id.action_loginFragment_to_emailVerificationFragment);
                    }
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.auth_fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize views
        initializeViews(view);

        //Navigate to Signup Fragment
        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnLogin.setEnabled(false);

                String txtEmail = textInputEmail.getText().toString().trim();
                String txtPassword = textInputPassword.getText().toString().trim();

                if (areInputsValid(txtEmail, txtPassword)) {

                    loginProgressBar.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnLogin.setEnabled(true);
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            viewModel.signIn(txtEmail, txtPassword);
                        }
                    }, 4000);

                } else {
                    btnLogin.setEnabled(true);
                    Toast.makeText(getActivity(), "Please fill out everything", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --------------------------------------------  FUNCTIONS  -------------------------------------------------------//
    // ------ input validations -------------------------------
    public final String TAG = "TESTING";

    private boolean isEmailValid(String email) {
        if (!email.isEmpty()) {
            textInputEmailLayout.setError(null);
            Log.d(TAG, "EMAIL: NOT EMPTY");
            return true;
        } else {
            textInputEmailLayout.setError("Email address field cannot be empty");
            Log.d(TAG, "EMAIL: EMPTY");
            return false;

        }
    }

    private boolean isPasswordValid(String password) {
        if (!password.isEmpty()) {
            textInputPasswordLayout.setError(null);
            Log.d(TAG, "PASSWORD: NOT EMPTY");
            return true;
        } else {
            textInputPasswordLayout.setError("Password field cannot be empty");
            Log.d(TAG, "PASSWORD: EMPTY");
            return false;
        }
    }

    public boolean areInputsValid(String email, String password) {

        boolean emailResult = isEmailValid(email);
        boolean passwordResult = isPasswordValid(password);

        if (emailResult && passwordResult) {
            Log.d(TAG, "CAN PROCEED: TRUE");
            return true;
        } else {
            Log.d(TAG, "CAN PROCEED: FALSE");
            return false;
        }
    }

    public void initializeViews(View view) {
        // TextInputLayouts
        textInputEmailLayout = view.findViewById(R.id.text_input_email_layout_login);
        textInputPasswordLayout = view.findViewById(R.id.text_input_password_layout_login);

        //TextInputEditTexts
        textInputEmail = view.findViewById(R.id.text_input_email_login);
        textInputPassword = view.findViewById(R.id.text_input_password_login);

        gotoSignUp = view.findViewById(R.id.gotoSignUp);
        btnForgotPassword = view.findViewById(R.id.btnForgotPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        navController = Navigation.findNavController(view);
        loginProgressBar = view.findViewById(R.id.loginProgressBar);
    }

    public void resetUserRole() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(USER_ROLE, 0);
        editor.apply();
    }

    public void saveUserRole(int userRole) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Toast.makeText(getActivity(), "Saved User Role: " + userRole, Toast.LENGTH_SHORT).show();
        editor.putInt(USER_ROLE, userRole);
        editor.apply();
    }

    public int getUserRole() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        int userRole = sharedPreferences.getInt(USER_ROLE, 0);

        return userRole;
    }
}