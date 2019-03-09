package com.example.hca127.greenfood.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hca127.greenfood.MainActivity;
import com.example.hca127.greenfood.R;
import com.example.hca127.greenfood.objects.LocalUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private DatabaseReference mDatabase;
    private ImageView name_pencil;
    private ImageView mNameCheck;
    private EditText mDisplayName;
    private TextView mEmail;
    private Spinner mCityChoice;
    private ImageView mCityCheck;
    private ImageView mCityPencil;
    private LocalUser mLocalUser;
    private ImageView mProfilePicture;
    private static final int mPickedPicture = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mLocalUser = ((MainActivity)getActivity()).getLocalUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mNameCheck = view.findViewById(R.id.edit_display_name_button_check);
        mNameCheck.setVisibility(ImageView.GONE);

        mCityCheck = view.findViewById(R.id.edit_city_spinner_check);
        mCityCheck.setVisibility(ImageView.GONE);

        mCityChoice = view.findViewById(R.id.edit_city_spinner_choice);
        mCityChoice.setSelection(mLocalUser.getCity());
        mCityChoice.setEnabled(false);

        final SharedPreferences googleStuff = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        mCityChoice.setSelection(mLocalUser.getCity());
        mDisplayName = (EditText) view.findViewById(R.id.display_name);
        mDisplayName.setText(mLocalUser.getName());
        mDisplayName.setEnabled(false);
        mEmail = (TextView) view.findViewById(R.id.email);
        mEmail.setText(mLocalUser.getUserEmail());

        mProfilePicture = (ImageView)view.findViewById(R.id.profilePicture);
        int[] pictureIds = {
                R.drawable.tree, R.drawable.sunglasses, R.drawable.dog,
                R.drawable.cat, R.drawable.monkey, R.drawable.ghost
        };
        mProfilePicture.setImageResource(pictureIds[mLocalUser.getProfileIcon()]);
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UserIconFragment()).addToBackStack(null).commit();
            }
        });


        name_pencil = (ImageView) view.findViewById(R.id.edit_display_name_button);
        name_pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisplayName.setEnabled(true);
                mDisplayName.setCursorVisible(true);
                mDisplayName.setFocusableInTouchMode(true);
                mDisplayName.setInputType(InputType.TYPE_CLASS_TEXT);
                mDisplayName.requestFocus();
                name_pencil.setVisibility(ImageView.GONE);
                mNameCheck.setVisibility(ImageView.VISIBLE);
            }
        });

        mNameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisplayName.setEnabled(false);
                String newName = mDisplayName.getText().toString().trim();
                SharedPreferences.Editor editor = googleStuff.edit();
                editor.putString("google_account_name",newName);
                editor.apply();

                mLocalUser.setName(newName);
                mDatabase.child("users").child(mLocalUser.getUserId()).child("name").setValue(newName);
                ((MainActivity)getActivity()).setLocalUser(mLocalUser);

                mNameCheck.setVisibility(ImageView.GONE);
                name_pencil.setVisibility(ImageView.VISIBLE);
            }
        });


        mCityPencil = (ImageView) view.findViewById(R.id.edit_city_spinner_button);
        mCityPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCityChoice.setEnabled(true);
                mCityPencil.setVisibility(ImageView.GONE);
                mCityCheck.setVisibility(ImageView.VISIBLE);

            }
        });

        mCityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCityChoice.setEnabled(false);
                mCityPencil.setVisibility(ImageView.VISIBLE);
                mCityCheck.setVisibility(ImageView.GONE);

                DatabaseReference userDatabase = mDatabase;
                userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int city = mCityChoice.getSelectedItemPosition();
                        if(city != mLocalUser.getCity()) {
                            int participantTemp = (int) (long) dataSnapshot.child("Community pledge")
                                    .child(Integer.toString(mLocalUser.getCity())).child("participant").getValue();
                            dataSnapshot.child("Community pledge").child(Integer.toString(mLocalUser.getCity()))
                                    .child("participant").getRef().setValue(participantTemp - 1);
                            double pledgeTemp = (double) dataSnapshot.child("Community pledge").child(
                                    Integer.toString(mLocalUser.getCity())).child("pledge").getValue();
                            dataSnapshot.child("Community pledge").child(Integer.toString(mLocalUser.getCity()))
                                    .child("pledge").getRef().setValue(pledgeTemp - mLocalUser.getPledge());

                            participantTemp = (int) (long) dataSnapshot.child("Community pledge").child(
                                    Integer.toString(city)).child("participant").getValue();
                            dataSnapshot.child("Community pledge").child(Integer.toString(city)).child("participant")
                                    .getRef().setValue(participantTemp + 1);
                            pledgeTemp = (double) dataSnapshot.child("Community pledge").child(Integer.toString(
                                    city)).child("pledge").getValue();
                            dataSnapshot.child("Community pledge").child(Integer.toString(city)).child("pledge")
                                    .getRef().setValue(pledgeTemp + mLocalUser.getPledge());
                            mLocalUser.setCity(city);
                            dataSnapshot.child("users").child(mLocalUser.getUserId()).child("city")
                                    .getRef().setValue(mLocalUser.getCity());
                            ((MainActivity) getActivity()).setLocalUser(mLocalUser);
                            SharedPreferences.Editor editor = googleStuff.edit();
                            editor.putInt("mCityChoice", city);
                            editor.apply();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return view;
    }
}
