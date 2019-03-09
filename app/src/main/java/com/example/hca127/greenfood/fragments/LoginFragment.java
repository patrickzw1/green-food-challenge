package com.example.hca127.greenfood.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hca127.greenfood.MainActivity;
import com.example.hca127.greenfood.R;
import com.example.hca127.greenfood.objects.LocalUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hca127.greenfood.MainActivity.hideKeyboard;


public class LoginFragment extends Fragment {
    private FirebaseUser mUser;
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabase;
    private TextView mStatusText;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private Button mSignUpButton;
    private Button mLoginButton;
    private LocalUser mLocalUser;
    ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuthentication = FirebaseAuth.getInstance();
        mStatusText = view.findViewById(R.id.statusText);
        mEmailInput = view.findViewById(R.id.emailInput);
        mEmailInput.requestFocus();
        mPasswordInput = view.findViewById(R.id.passwordInput);
        mSignUpButton = view.findViewById(R.id.signUpButton);
        mLoginButton = view.findViewById(R.id.loginButton);

        mLocalUser = ((MainActivity)getActivity()).getLocalUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mUser = mAuthentication.getCurrentUser();
        updateUser(mUser);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
                hideKeyboard(getActivity());
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SignUpFragment()).addToBackStack(null).commit();
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        return view;
    }

    private void updateUser(FirebaseUser user) {
        if(user != null) {
            mLocalUser.setUserEmail(user.getEmail());
            mLocalUser.setUserId(user.getUid());
            DatabaseReference userDatabase = mDatabase.child(mLocalUser.getUserId());
            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mLocalUser.setName((String) dataSnapshot.child("name").getValue());
                    double temp_pledge = (double)dataSnapshot.child("pledge").getValue();
                    mLocalUser.setPledge(temp_pledge);

                    mLocalUser.setCity((int)(long) dataSnapshot.child("city").getValue());
                    mLocalUser.setProfileIcon((int)(long)dataSnapshot.child("icon_index").getValue());

                    ((MainActivity)getActivity()).setLocalUser(mLocalUser);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            String dialog = String.format(getResources().getString(R.string.logged_in),user.getEmail());
            mStatusText.setText(dialog);
        } else {
            mStatusText.setText(R.string.logged_out);
        }
    }

    private void logIn() {

        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        if (email.isEmpty()) {
            mEmailInput.setError("Email is required");
            mEmailInput.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailInput.setError("Please enter a valid email");
            mEmailInput.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            mPasswordInput.setError("Password is required");
            mPasswordInput.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuthentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    updateUser(mAuthentication.getCurrentUser());
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new CommunityFragment()).addToBackStack(null).commit();
                    NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
                    navigationView.setCheckedItem(R.id.menu_community);

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
