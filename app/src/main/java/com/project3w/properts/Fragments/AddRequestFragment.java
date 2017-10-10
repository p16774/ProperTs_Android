package com.project3w.properts.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.project3w.properts.Helpers.FirebaseDataHelper;
import com.project3w.properts.Objects.Request;
import com.project3w.properts.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Nate on 10/8/17.
 */

public class AddRequestFragment extends Fragment {

    // class variables
    EditText requestTitleView, requestContentView;
    ImageView requestPictureView;
    Spinner requestUrgencySpinner;
    String mCurrentPhotoPath;
    public static final int REQUEST_IMAGE_CAPTURE = 0x01001;


    public AddRequestFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set to be able to replace menu for the fragment
        View view = inflater.inflate(R.layout.add_request, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // grab our references to get values when user clicks save
        requestTitleView = getActivity().findViewById(R.id.new_request_title);
        requestContentView = getActivity().findViewById(R.id.new_request_content);
        requestUrgencySpinner = getActivity().findViewById(R.id.new_request_urgency);
        requestPictureView = getActivity().findViewById(R.id.new_request_picture);
        requestPictureView.setImageResource(R.drawable.placeholder);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.add_request_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pull our id of item selected
        int id = item.getItemId();

        if (id == R.id.action_take_picture) {
            dispatchTakePictureIntent();
        } else if (id == R.id.action_save_request) {
            boolean success = submitNewRequest();
        }

        return super.onOptionsItemSelected(item);

    }

    public boolean submitNewRequest() {

        // check for null fields and assign necessary values to others
        if (requestTitleView.getText().toString().trim().equals("") || requestContentView.getText().toString().trim().equals("")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must fill out all fields.",
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else {

            // get our firebase helper reference
            FirebaseDataHelper firebaseDataHelper = new FirebaseDataHelper(getActivity());

            // pull our data and create our request item
            String title = requestTitleView.getText().toString();
            String content = requestContentView.getText().toString();
            String urgency = requestUrgencySpinner.getSelectedItem().toString();
            String status = "New";

            // convert our date object for swift iOS implementation
            SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss aaa", Locale.US);
            Date currentDate = new Date();
            String date = fmt.format(currentDate);
            String picture = mCurrentPhotoPath;

            // create our Request Object
            Request newRequest = new Request(title,content,urgency,date,status,picture);
            boolean submitted = firebaseDataHelper.submitMaintenanceRequest(newRequest);

            System.out.println("REQUEST WAS SUBMITTED AND RESULT WAS: " + submitted);

            return true;

        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("ERROR IN CREATING FILE");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.project3w.properts.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic(); // add picture to the gallery
            File image = new File(mCurrentPhotoPath); // grab reference to our image
            Uri imageUri = Uri.fromFile(image); // pull the URI to set our image
            requestPictureView.setImageURI(imageUri); // set our image
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = System.currentTimeMillis() + "";
        String imageFileName = "ITEM_" + timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ProperTs_Images");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
            System.out.println("Directory doesn't exist");
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}