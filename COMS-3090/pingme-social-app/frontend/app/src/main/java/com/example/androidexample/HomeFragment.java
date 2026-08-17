package com.example.androidexample;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeFragment extends Fragment {
    private static final String ARG_USERNAME = "USERNAME";
    private static final String UPLOAD_URL = "http://10.48.158.99:8081/images/upload";
    private static final String TAG = "HomeFragment";
    
    private String username;
    private TextView welcomeText;
    private ImageView imagePreview;
    private Button selectImageBtn;
    private Button uploadImageBtn;
    private ProgressBar uploadProgress;
    private Uri selectedImageUri;
    
    private ActivityResultLauncher<String> imagePickerLauncher;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String username) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
        }
        
        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imagePreview.setImageURI(uri);
                    uploadImageBtn.setEnabled(true);
                }
            }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize views
        welcomeText = view.findViewById(R.id.usernameText);
        imagePreview = view.findViewById(R.id.imagePreview);
        selectImageBtn = view.findViewById(R.id.selectImageBtn);
        uploadImageBtn = view.findViewById(R.id.uploadImageBtn);
        uploadProgress = view.findViewById(R.id.uploadProgress);
        
        if (username != null) {
            welcomeText.setText("Welcome " + username + "!");
        }

        // Set up click listeners
        selectImageBtn.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        uploadImageBtn.setOnClickListener(v -> uploadImage());

        return view;
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Starting image upload process");
        Log.d(TAG, "Selected image URI: " + selectedImageUri.toString());

        // Show progress and disable buttons
        uploadProgress.setVisibility(View.VISIBLE);
        selectImageBtn.setEnabled(false);
        uploadImageBtn.setEnabled(false);

        // Convert image to bytes
        byte[] imageData = convertImageUriToBytes(selectedImageUri);
        if (imageData == null) {
            handleUploadError("Failed to process image");
            Log.e(TAG, "Failed to convert image to bytes");
            return;
        }

        Log.d(TAG, "Image converted to bytes, size: " + imageData.length);

        // Create multipart request
        MultipartRequest multipartRequest = new MultipartRequest(
            Request.Method.POST,
            UPLOAD_URL,
            imageData,
            response -> {
                Log.d(TAG, "Upload successful, response: " + response);
                // Handle success
                uploadProgress.setVisibility(View.GONE);
                selectImageBtn.setEnabled(true);
                uploadImageBtn.setEnabled(true);
                Toast.makeText(requireContext(), "Upload successful!", Toast.LENGTH_LONG).show();
                
                // Clear the preview
                imagePreview.setImageResource(android.R.color.darker_gray);
                selectedImageUri = null;
                uploadImageBtn.setEnabled(false);
            },
            error -> {
                Log.e(TAG, "Upload failed", error);
                handleUploadError("Upload failed: " + error.getMessage());
            }
        );

        Log.d(TAG, "Sending request to: " + UPLOAD_URL);
        // Add request to queue
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(multipartRequest);
    }

    private void handleUploadError(String message) {
        uploadProgress.setVisibility(View.GONE);
        selectImageBtn.setEnabled(true);
        uploadImageBtn.setEnabled(true);
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        Log.e("Upload", message);
    }

    private byte[] convertImageUriToBytes(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}