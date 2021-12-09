package com.capstone.espasyoadmin.auth.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.auth.viewmodels.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;


public class EmailVerificationFragment extends Fragment {

    private AuthViewModel viewModel;

    private Button btnLoginToYourAccount;
    private NavController navController;
    private TextView gotoUpdateEmailFragment;
    private TextView txtEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);

        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser != null) {
                    String email = firebaseUser.getEmail();
                    txtEmail.setText(email);
                    if(firebaseUser.isEmailVerified()) {
                        viewModel.signOut();
                        navController.navigate(R.id.action_emailVerificationFragment_to_loginFragment);
                    }
                } else  {
                    // by default will navigate to login fragment if firebaseUser is null
                    navController.navigate(R.id.action_emailVerificationFragment_to_loginFragment);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.auth_fragment_email_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        btnLoginToYourAccount = view.findViewById(R.id.btnLoginToYourAccount);
        txtEmail = view.findViewById(R.id.txtEmail);
        //gotoUpdateEmailFragment = view.findViewById(R.id.gotoUpdateEmailFragment);

        btnLoginToYourAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.signOut();
                navController.navigate(R.id.action_emailVerificationFragment_to_loginFragment);
            }
        });

/*        gotoUpdateEmailFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_emailVerificationFragment_to_updateEmailFragment);
            }
        });*/
    }
}