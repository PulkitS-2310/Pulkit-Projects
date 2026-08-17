// UploadFragment.java
package com.example.androidexample.Upload.ImagePosting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidexample.databinding.FragmentUploadBinding;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 100;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment UploadFragment.
     */
    public static UploadFragment newInstance() {
        return new UploadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize View Binding
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    private void setupUI() {
        // Toggle between text and image mode
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.radioText.getId()) {
                binding.imageSelectButton.setVisibility(View.GONE);
                binding.selectedImageName.setText("");
                selectedImageUri = null;
            } else if (checkedId == binding.radioImage.getId()) {
                binding.imageSelectButton.setVisibility(View.VISIBLE);
            }
        });

        // Image selection button
        binding.imageSelectButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Upload button
        binding.uploadButton.setOnClickListener(v -> uploadPost());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.selectedImageName.setText(selectedImageUri != null ? selectedImageUri.getLastPathSegment() : "Image selected");
        }
    }

    private void uploadPost() {
        String title = binding.titleEditText.getText().toString().trim();
        String description = binding.descriptionEditText.getText().toString().trim();
        String hashtags = binding.hashtagsEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Title and description are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format hashtags
        String[] hashtagArray = hashtags.split("\\s+");
        ArrayList<String> formattedHashtagsList = new ArrayList<>();
        for (String hashtag : hashtagArray) {
            if (!hashtag.isEmpty()) {
                formattedHashtagsList.add("#" + hashtag);
            }
        }
        String formattedHashtags = String.join(" ", formattedHashtagsList);

        // Execute network operation in AsyncTask
        new UploadPostTask().execute(title, description, formattedHashtags);
    }

    private class UploadPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String description = params[1];
            // String hashtags = params[2]; // Not used in the API call, but formatted for future use

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                if (binding.radioText.isChecked()) {
                    // Text post
                    JSONObject json = new JSONObject();
                    json.put("username", "gman"); // Replace with actual username
                    json.put("title", title);
                    json.put("description", description);

                    RequestBody requestBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            json.toString()
                    );

                    Request request = new Request.Builder()
                            .url("http://coms-3090-029.class.las.iastate.edu:8080/users/gman/post")
                            .post(requestBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            return "Post uploaded successfully";
                        } else {
                            return "Upload failed: " + response.message();
                        }
                    }
                } else if (binding.radioImage.isChecked() && selectedImageUri != null) {
                    // Image post
                    File file = getFileFromUri(selectedImageUri);
                    if (file != null) {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("username", "gman") // Replace with actual username
                                .addFormDataPart("title", title)
                                .addFormDataPart("description", description)
                                .addFormDataPart(
                                        "image",
                                        file.getName(),
                                        RequestBody.create(MediaType.parse("image/*"), file)
                                );

                        RequestBody requestBody = builder.build();

                        Request request = new Request.Builder()
                                .url("http://coms-3090-029.class.las.iastate.edu:8080/users/gman/post")
                                .post(requestBody)
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            if (response.isSuccessful()) {
                                return "Image post uploaded successfully";
                            } else {
                                return "Upload failed: " + response.message();
                            }
                        }
                    } else {
                        return "Failed to process image";
                    }
                } else {
                    return "Please select an image";
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            if (result.contains("successfully")) {
                clearInputs();
            }
        }
    }

    private File getFileFromUri(Uri uri) {
        String path = uri.getPath();
        if (path == null) return null;

        File file = new File(requireContext().getCacheDir(), path.substring(path.lastIndexOf('/') + 1));
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clearInputs() {
        binding.titleEditText.setText("");
        binding.descriptionEditText.setText("");
        binding.hashtagsEditText.setText("");
        binding.selectedImageName.setText("");
        selectedImageUri = null;
        binding.radioText.setChecked(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}