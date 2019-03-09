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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hca127.greenfood.MainActivity;
import com.example.hca127.greenfood.R;
import com.example.hca127.greenfood.objects.LocalUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.example.hca127.greenfood.MainActivity.hideKeyboard;


public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserData;
    private DatabaseReference mCommunity;
    private DataSnapshot dataSnapshot;

    private ImageView mSignUp;
    private EditText mUserName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private LocalUser mLocalUser;
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mUserName = (EditText) view.findViewById(R.id.create_username_input);
        mEmail = (EditText) view.findViewById(R.id.create_email_input);
        mPassword = (EditText) view.findViewById(R.id.create_password_input);
        mPasswordConfirm = (EditText) view.findViewById(R.id.create_confirm_password);
        mSignUp = (ImageView) view.findViewById(R.id.sign_up_button);
        mLocalUser = new LocalUser();
        mAuth = FirebaseAuth.getInstance();
        mUserData = FirebaseDatabase.getInstance().getReference().child("users");
        mCommunity = FirebaseDatabase.getInstance().getReference().child("Community pledge");

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                hideKeyboard(getActivity());
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);


        return view;
    }

    private void registerUser() {
        final String userName = mUserName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String passwordConfirm = mPasswordConfirm.getText().toString().trim();

        if (email.isEmpty()) {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            mPasswordConfirm.setError("Passwords do not match!");
            mPasswordConfirm.requestFocus();
            return;
        }
        if (password.length()<6) {
            mPassword.setError("Minimum length of password should be 6");
            mPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    mLocalUser.setUserEmail(user.getEmail());
                    mLocalUser.setUserId(user.getUid());
                    mLocalUser.setName(userName);
                    DatabaseReference thisUser = mUserData.child(mLocalUser.getUserId());
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                    String strDate = formatter.format(date);
                    thisUser.child("name").setValue(userName);
                    thisUser.child("city").setValue(0);
                    thisUser.child("email").setValue(user.getEmail());
                    thisUser.child("pledge").setValue(-0.00001);
                    HashMap<String, Object> emission = new HashMap<>();

                    thisUser.child("meal").child("0").setValue("");
                    thisUser.child("meal").child("1").setValue("");
                    thisUser.child("meal").child("2").setValue("");
                    thisUser.child("icon_index").setValue(mLocalUser.getProfileIcon());

                    mCommunity.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long temp = (long)dataSnapshot.child("0").child("participant").getValue();
                            dataSnapshot.child("0").child("participant").getRef().setValue(temp+1);
                            temp = (long)dataSnapshot.child("total").child("participant").getValue();
                            dataSnapshot.child("total").child("participant").getRef().setValue(temp+1);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    ((MainActivity)getActivity()).setLocalUser(mLocalUser);
                    String dialog = String.format(getResources().getString(R.string.logged_in),user.getEmail());

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new CommunityFragment()).addToBackStack(null).commit();
                    NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
                    navigationView.setCheckedItem(R.id.menu_community);

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getActivity().getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}
