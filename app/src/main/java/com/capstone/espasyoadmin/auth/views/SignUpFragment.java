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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.auth.viewmodels.AuthViewModel;
import com.capstone.espasyoadmin.models.Admin;
import com.capstone.espasyoadmin.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AuthViewModel viewModel;
    private NavController navController;

    //UI Elements
    private TextInputLayout textInputEmailLayout,
            textInputFirstNameLayout,
            textInputLastNameLayout,
            textInputPasswordLayout,
            textInputConfirmPasswordLayout,
            textInputRoleLayout;

    private TextInputEditText textInputEmail,
            textInputFirstName,
            textInputLastName,
            textInputPassword,
            textInputConfirmPassword;

    private CheckBox agreeToTermsAndConditions;

    //navigate to Login using text
    private TextView gotoLogin;
    private ProgressBar signUpProgressBar;
    private Button btnSignUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);

        //check each time if there is a logged user
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {

                    if (firebaseUser.isEmailVerified()) {
                        //TODO:must include logout user for them to try login in their verified credentials
                        viewModel.signOut();
                        navController.navigate(R.id.action_emailVerificationFragment_to_loginFragment);
                    } else {
                        navController.navigate(R.id.action_signUpFragment_to_emailVerificationFragment);
                    }
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.auth_fragment_sign_up, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TextInputLayouts
        textInputEmailLayout = view.findViewById(R.id.text_input_email_layout_signup);
        textInputFirstNameLayout = view.findViewById(R.id.text_input_firstname_layout_signup);
        textInputLastNameLayout = view.findViewById(R.id.text_input_lastname_layout_signup);
        textInputPasswordLayout = view.findViewById(R.id.text_input_password_layout_signup);
        textInputConfirmPasswordLayout = view.findViewById(R.id.text_input_confirmpassword_layout_signup);

        //TextInputEditText
        textInputFirstName = view.findViewById(R.id.text_input_firstname_signup);
        textInputLastName = view.findViewById(R.id.text_input_lastname_signup);
        textInputEmail = view.findViewById(R.id.text_input_email_signup);
        textInputPassword = view.findViewById(R.id.text_input_password_signup);
        textInputConfirmPassword = view.findViewById(R.id.text_input_confirmpassword_signup);

        //Checkbox
        agreeToTermsAndConditions = view.findViewById(R.id.agreeToTermsAndConditions);

        //progress bar
        signUpProgressBar = view.findViewById(R.id.signUpProgressBar);

        btnSignUp = view.findViewById(R.id.btnSignUp);
        gotoLogin = view.findViewById(R.id.gotoLogin);
        navController = Navigation.findNavController(view);

        btnSignUp.setEnabled(false);

        //Navigate to Login Fragment
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });

        agreeToTermsAndConditions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    btnSignUp.setEnabled(false);
                } else {
                    btnSignUp.setEnabled(true);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = textInputFirstName.getText().toString().trim();
                String lastName = textInputLastName.getText().toString().trim();
                String email = textInputEmail.getText().toString().trim();
                String password = textInputPassword.getText().toString().trim();
                String confirmPassword = textInputConfirmPassword.getText().toString().trim();

                if (areInputsValid(firstName, lastName, email, password, confirmPassword)) {

                    //check if email already exists
                    firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getSignInMethods().size() == 0) {

                                    int userRole = 3;
                                    //todo: must include set a code that only the admin knows to be able to create a new account
                                    Admin newAdmin = new Admin("", firstName, lastName, email, password, userRole);
                                    signUpProgressBar.setVisibility(View.VISIBLE);
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            signUpProgressBar.setVisibility(View.INVISIBLE);
                                            viewModel.registerAdmin(newAdmin);
                                        }
                                    }, 4000);
                                } else {
                                    textInputEmailLayout.setError("Email already exists");
                                }
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });

    }

    // Functions
    // ------ input validations -------------------------------
    public final String TAG = "TESTING";

    //TODO: Check logic errors on isEmpty validations

    /* Check if firstName is empty */
    private Boolean isFirstNameValid(String firstName) {
        if (!firstName.isEmpty()) {
            textInputFirstNameLayout.setError(null);
            Log.d(TAG, "FIRSTNAME: NOT EMPTY");
            return true;
        } else {
            textInputFirstNameLayout.setError("First Name required");
            Log.d(TAG, "FIRSTNAME: EMPTY");
            return false;
        }
    }

    /* Check if lastName is empty */
    private Boolean isLastNameValid(String lastName) {
        if (!lastName.isEmpty()) {
            textInputLastNameLayout.setError(null);
            Log.d(TAG, "LASTNAME: NOT EMPTY");
            return true;
        } else {
            textInputLastNameLayout.setError("Last Name required");
            Log.d(TAG, "LASTNAME: EMPTY");
            return false;
        }
    }

    /* Check if email is empty */
    private Boolean isEmailValid(String email) {
        //TODO: Must include validations if email exist in firebase auth and database
        if (!email.isEmpty()) {
            textInputEmailLayout.setError(null);
            Log.d(TAG, "EMAIL: NOT EMPTY");
            return true;
        } else {
            textInputEmailLayout.setError("Email required");
            Log.d(TAG, "EMAIL: EMPTY");
            return false;
        }
    }


    /* Check if password and confirmPassword is empty */
    private Boolean arePasswordsEmpty(String password, String confirmPassword) {

        boolean isPasswordEmpty = false;
        boolean isConfirmPasswordEmpty = false;

        //check if password is empty
        if (!password.isEmpty()) {
            textInputPasswordLayout.setError(null);
            Log.d(TAG, "PASSWORD: NOT EMPTY");
            isPasswordEmpty = true;
        } else {
            textInputPasswordLayout.setError("Password required");
            Log.d(TAG, "PASSWORD: EMPTY");
            isPasswordEmpty = false;
        }
        //check if confirmPassword is empty
        if (!confirmPassword.isEmpty()) {
            textInputConfirmPasswordLayout.setError(null);
            Log.d(TAG, "CONFIRM PASSWORD: NOT EMPTY");
            isConfirmPasswordEmpty = true;
        } else {
            textInputConfirmPasswordLayout.setError("Confirm Password required");
            Log.d(TAG, "CONFIRM PASSWORD: EMPTY");
            isConfirmPasswordEmpty = false;
        }

        if (isPasswordEmpty && isConfirmPasswordEmpty) {
            return true;
        } else {
            return false;
        }
    }

    /* Check if password1 and password2 are not empty and match*/
    private Boolean arePasswordsValid(String password1, String password2) {

        if (arePasswordsEmpty(password1, password2)) {
            if (password1.length() > 5 && password2.length() > 5) {

                Log.d(TAG, "PASSWORD COUNT: GREATER THAN 5");
                if (matchPassword(password1, password2)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                textInputPasswordLayout.setError("Password must be 6-15 characters");
                textInputConfirmPasswordLayout.setError("Password must be 6-15 characters");
                Log.d(TAG, "PASSWORD COUNT: LESS THAN 6");
                return false;
            }

        } else {
            return false;
        }
    }

    /* Check if password1 and password2 are match*/
    private Boolean matchPassword(String password, String confirmPassword) {

        if (password.equals(confirmPassword)) {
            Log.d(TAG, "PASSWORD MATCHING: MATCH");
            return true;
        } else {
            textInputPasswordLayout.setError("Password Not Match");
            textInputConfirmPasswordLayout.setError("Password Not Match");
            Log.d(TAG, "PASSWORD MATCHING: NOT MATCH");
            return false;
        }
    }

    private Boolean areInputsValid(String firstName, String lastName, String email, String password, String confirmPassword) {

        boolean firstNameResult = isFirstNameValid(firstName);
        boolean lastNameResult = isLastNameValid(lastName);
        boolean emailResult = isEmailValid(email);
        boolean passwordResult = arePasswordsValid(password, confirmPassword);

        if (firstNameResult && lastNameResult && emailResult && passwordResult) {
            Log.d(TAG, "CAN PROCEED: TRUE");
            return true;
        } else {
            Log.d(TAG, "CAN PROCEED: FALSE");
            return false;
        }
    }

}