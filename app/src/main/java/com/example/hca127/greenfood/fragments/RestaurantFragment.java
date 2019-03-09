package com.example.hca127.greenfood.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hca127.greenfood.GlideApp;
import com.example.hca127.greenfood.MainActivity;
import com.example.hca127.greenfood.R;
import com.example.hca127.greenfood.objects.LocalUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class RestaurantFragment extends Fragment {
    private Button mMealOne, mMealTwo, mMealThree;
    private TextView mRestaurantTitle, mDescriptionText, mAddPhotoText, mSpeechBubbleText, mDeleteMealButton;
    private EditText mRestaurantName, mMealName, mMealDescription;
    private Spinner mMainIngredient, mCity;
    private ImageView mSaveButton, mEditMeal, mGalleryButton, mCameraButton, mResetButton, mFinalImage, mSpeechBubble;
    private Drawable mDrawable;
    private static final int mGetFromGallery = 0;
    private static final int mCameraRequest = 1;
    private String mCameraFile;
    private Uri mUri;
    private DatabaseReference mDatabase;
    private LocalUser mLocalUser;
    private FirebaseStorage mCloudStorage;
    private StorageReference mStorageReference;
    private StorageReference mImagesReference;
    private ArrayList<String> mMealReference;
    private int mCurrentMealIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mLocalUser = ((MainActivity)getActivity()).getLocalUser();
        mRestaurantTitle = view.findViewById(R.id.restaurant_title);
        mMealOne = view.findViewById(R.id.meal_1_button);
        mMealTwo = view.findViewById(R.id.meal_2_button);
        mMealThree = view.findViewById(R.id.meal_3_button);

        mCloudStorage = FirebaseStorage.getInstance();
        mStorageReference = mCloudStorage.getReference();
        mImagesReference = mStorageReference.child("images");

        mEditMeal = view.findViewById(R.id.meal_edit);
        mRestaurantName = view.findViewById(R.id.restaurant_name_edit);
        mMealName = view.findViewById(R.id.meal_name_edit);
        mDescriptionText = view.findViewById(R.id.description_text);
        mMealDescription = view.findViewById(R.id.description_edit);
        mMainIngredient = view.findViewById(R.id.main_ingredient_spinner);
        mCity = view.findViewById(R.id.location_spinner);
        mGalleryButton = view.findViewById(R.id.gallery_button);
        mCameraButton = view.findViewById(R.id.camera_button);
        mResetButton = view.findViewById(R.id.reset_button);
        mFinalImage = view.findViewById(R.id.final_photo);
        mSaveButton = view.findViewById(R.id.meal_edit_confirm);
        mAddPhotoText = view.findViewById(R.id.add_photo_text);
        mSpeechBubble = view.findViewById(R.id.speech_bubble);
        mSpeechBubbleText = view.findViewById(R.id.speech_bubble_text);
        mDeleteMealButton = view.findViewById(R.id.meal_delete);
        mDrawable = getResources().getDrawable(R.drawable.android);

        mMealReference = new ArrayList<>();
        DatabaseReference userMealData = mDatabase.child("users").child(mLocalUser.getUserId()).child("meal");
        userMealData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 0; i<3; i++){
                    mMealReference.add((String)dataSnapshot.child(String.valueOf(i)).getValue());
                }
                mCurrentMealIndex = 0;
                mMealOne.setAlpha(0.5f);
                mealViewUpdate();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        lockAll();
        checkPhoto();

        mMealOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMealOne.setAlpha(0.5f);
                mMealTwo.setAlpha(1.0f);
                mMealThree.setAlpha(1.0f);
                mCurrentMealIndex = 0;
                mealViewUpdate();
            }
        });

        mMealTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMealOne.setAlpha(1.0f);
                mMealTwo.setAlpha(0.5f);
                mMealThree.setAlpha(1.0f);
                mCurrentMealIndex = 1;
                mealViewUpdate();
            }
        });

        mMealThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMealOne.setAlpha(1.0f);
                mMealTwo.setAlpha(1.0f);
                mMealThree.setAlpha(0.5f);
                mCurrentMealIndex = 2;
                mealViewUpdate();
            }
        });

        mEditMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockAll();
                mMealOne.setVisibility(View.GONE);
                mMealTwo.setVisibility(View.GONE);
                mMealThree.setVisibility(View.GONE);
                mEditMeal.setVisibility(View.GONE);
            }
        });

        mDeleteMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRestaurantName.setText("");
                mMealName.setText("");
                mMealDescription.setText("");
                mMainIngredient.setSelection(0);
                mCity.setSelection(0);
                mFinalImage.setImageDrawable(mDrawable);
                deleteMeal();
                lockAll();
            }
        });

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                galleryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(galleryIntent, mGetFromGallery);
                }
            });

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    String[] PERMISSIONS = {android.Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, mCameraRequest);
                }

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, mCameraRequest);
                cameraIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable android = getResources().getDrawable(R.drawable.android);
                mFinalImage.setImageDrawable(android);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long currentTime = Calendar.getInstance().getTimeInMillis();
                String fileName = mLocalUser.getUserId() + "$" + currentTime;
                final StorageReference mUserUpload = mImagesReference.child(fileName + ".jpg");
                final DatabaseReference nMeal;
                if(mMealReference.get(mCurrentMealIndex).equals("")) {
                    nMeal = mDatabase.child("meals").push();
                    mMealReference.set(mCurrentMealIndex, nMeal.getKey());
                }else{
                    nMeal = mDatabase.child("meals")
                            .child(mMealReference.get(mCurrentMealIndex));
                }
                mDatabase.child("users").child(mLocalUser.getUserId()).child("meal")
                        .child(String.valueOf(mCurrentMealIndex)).setValue(nMeal.getKey());
                nMeal.child("meal name").setValue(mMealName.getText().toString());
                nMeal.child("restaurant").setValue(mRestaurantName.getText().toString());
                nMeal.child("protein").setValue(mMainIngredient.getSelectedItemPosition());
                nMeal.child("description").setValue(mMealDescription.getText().toString());
                nMeal.child("city index").setValue(mCity.getSelectedItemPosition());
                nMeal.child("imageLink").setValue("");
                // photo upload starts here
                Bitmap tempFinalImage = ((BitmapDrawable)mFinalImage.getDrawable()).getBitmap();
                Bitmap tempDrawable = ((BitmapDrawable)getResources().getDrawable(R.drawable.android)).getBitmap();
                if(!tempFinalImage.sameAs(tempDrawable)){
                    mFinalImage.setDrawingCacheEnabled(true);
                    mFinalImage.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) mFinalImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mUserUpload.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mUserUpload.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                nMeal.child("imageLink").setValue(downloadUri.toString());

                            } else {

                            }
                        }
                    });
                }else{
                    // if no image is uploaded, the default no image is chosen
                    nMeal.child("imageLink").setValue("https://firebasestorage.googleapis.com/v0/b/greenfood-a5dd0.appspot.com/o/images%2Fandroid.png?alt=media&token=a729a2b4-dea7-4b76-8e81-8188e1b2fa1d");
                }
                lockAll();
            }
        });

        return view;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==mGetFromGallery) {
            try {
                Uri selectedImage = data.getData();
                Bitmap imageFromGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                mFinalImage.setImageBitmap(imageFromGallery);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        if(requestCode==mCameraRequest){
            try {
                Bitmap imageFromCamera = (Bitmap) data.getExtras().get("data");
                mFinalImage.setImageBitmap(imageFromCamera);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public void lockAll() {
        mRestaurantName.setEnabled(false);
        mMealName.setEnabled(false);
        mMealDescription.setEnabled(false);
        mDescriptionText.setText(R.string.description_meal);
        mMainIngredient.setEnabled(false);
        mCity.setEnabled(false);
        mGalleryButton.setVisibility(View.GONE);
        mCameraButton.setVisibility(View.GONE);
        mResetButton.setVisibility(View.GONE);
        mFinalImage.setVisibility(View.VISIBLE);
        mAddPhotoText.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);
        mEditMeal.setVisibility(View.VISIBLE);
        mDeleteMealButton.setVisibility(View.GONE);
        mMealOne.setVisibility(View.VISIBLE);
        mMealTwo.setVisibility(View.VISIBLE);
        mMealThree.setVisibility(View.VISIBLE);
        checkPhoto();
    }

    public void unlockAll(){
        mRestaurantName.setEnabled(true);
        mMealName.setEnabled(true);
        mMealDescription.setEnabled(true);
        mDescriptionText.setText(R.string.description_text);
        mMainIngredient.setEnabled(true);
        mCity.setEnabled(true);
        mGalleryButton.setVisibility(View.VISIBLE);
        mCameraButton.setVisibility(View.VISIBLE);
        mResetButton.setVisibility(View.VISIBLE);
        mAddPhotoText.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.VISIBLE);
        mSpeechBubble.setVisibility(View.GONE);
        mSpeechBubbleText.setVisibility(View.GONE);
        mDeleteMealButton.setVisibility(View.VISIBLE);
    }

    public void mealViewUpdate(){
        final int[] titleIds = {
                R.string.restaurant_title1, R.string.restaurant_title2, R.string.restaurant_title3
        };
        mFinalImage.setImageResource(R.drawable.android);
        if(mCurrentMealIndex<mMealReference.size() && !mMealReference.get(mCurrentMealIndex).equals("")){
            DatabaseReference mealReference = mDatabase.child("meals")
                    .child(mMealReference.get(mCurrentMealIndex));
            mealReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mRestaurantName.setText((String) dataSnapshot.child("restaurant").getValue());
                    mMealName.setText((String) dataSnapshot.child("meal name").getValue());
                    mMealDescription.setText((String) dataSnapshot.child("description").getValue());
                    mMainIngredient.setSelection((int) (long) dataSnapshot.child("protein").getValue());
                    mCity.setSelection((int) (long) dataSnapshot.child("city index").getValue());
                    String imageLink = (String) dataSnapshot.child("imageLink").getValue();
                    if (imageLink == null || !imageLink.equals("")) {
                        StorageReference httpsReference = mCloudStorage.getReferenceFromUrl(imageLink);
                        GlideApp.with(((MainActivity) getActivity()).getApplicationContext())
                                .load(httpsReference)
                                .into(mFinalImage);
                    } else {
                        mFinalImage.setImageURI(mUri);
                    }
                    mRestaurantTitle.setText(titleIds[mCurrentMealIndex]);
                    checkPhoto();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            mRestaurantName.setText("");
            mMealName.setText("");
            mMealDescription.setText("");
            mMainIngredient.setSelection(0);
            mCity.setSelection(0);
            mFinalImage.setImageURI(mUri);
            mRestaurantTitle.setText(titleIds[mCurrentMealIndex]);
            checkPhoto();
        }
    }

    public void checkPhoto(){
        try{
            Bitmap tempFinalImage = ((BitmapDrawable)mFinalImage.getDrawable()).getBitmap();
            Bitmap tempDrawable = ((BitmapDrawable)getResources().getDrawable(R.drawable.android)).getBitmap();
            if(tempFinalImage.sameAs(tempDrawable)) {
                mSpeechBubbleText.setVisibility(View.VISIBLE);
                mSpeechBubble.setVisibility(View.VISIBLE);
                mSpeechBubbleText.setText(R.string.why_not);
            }
            else{
                mSpeechBubbleText.setVisibility(View.VISIBLE);
                mSpeechBubble.setVisibility(View.VISIBLE);
                mSpeechBubbleText.setText(R.string.nice_photo);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
            try {
                Drawable image = mFinalImage.getDrawable();
                Drawable.ConstantState cImage = image.getConstantState();
                Drawable.ConstantState cAndroid = mDrawable.getConstantState();
                if (cImage.equals(cAndroid)) {
                    mSpeechBubbleText.setVisibility(View.VISIBLE);
                    mSpeechBubble.setVisibility(View.VISIBLE);
                    mSpeechBubbleText.setText(R.string.why_not);
                } else {
                    mSpeechBubbleText.setVisibility(View.VISIBLE);
                    mSpeechBubble.setVisibility(View.VISIBLE);
                    mSpeechBubbleText.setText(R.string.nice_photo);
                }
            }catch(NullPointerException n){
                mSpeechBubbleText.setVisibility(View.VISIBLE);
                mSpeechBubble.setVisibility(View.VISIBLE);
                mSpeechBubbleText.setText(R.string.nice_photo);
            }
        }
    }

    public void deleteMeal(){
        if(!mMealReference.get(mCurrentMealIndex).equals("")) {
            mDatabase.child("meals").child(mMealReference.get(mCurrentMealIndex)).removeValue();
            mMealReference.set(mCurrentMealIndex, "");
            mDatabase.child("users").child(mLocalUser.getUserId()).child("meal").
                    child(String.valueOf(mCurrentMealIndex)).setValue("");
        }

    }
}