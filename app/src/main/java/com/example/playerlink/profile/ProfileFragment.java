package com.example.playerlink.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.Result;
import com.example.playerlink.databinding.FragmentProfileBinding;
import com.example.playerlink.databinding.FragmentRegisterBinding;
import com.example.playerlink.fragments.register_login_fragments.LoginFragment;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.AuthRepositoryFirebase;
import com.example.playerlink.repositories.ProfileRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FragmentProfileBinding binding;
    private AuthRepositoryFirebase authRepository;
    private ProfileRepositoryFirebase profileRepository;

    private User currentUser = LoginFragment.GetCurrentUser();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
        authRepository = new AuthRepositoryFirebase();
        profileRepository = new ProfileRepositoryFirebase();
        if(currentUser.getUserImage() == null){
            binding.profilePicture.setImageResource(R.mipmap.ic_launcher);
        } else{
            binding.profilePicture.setImageBitmap(currentUser.getUserImage());
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.changeUsernameBtn.setOnClickListener(v -> {changeUsername();});

        binding.updateGameBtn.setOnClickListener(v ->{ updateGames(); });

        binding.signOutBtn.setOnClickListener(v ->{ signOut(v);});

        binding.changeImageBtn.setOnClickListener(v ->{ changeProfilePicture();});

    }

    private void signOut(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.sign_out)
                .setMessage(R.string.are_you_sure_you_want_to_sign_out)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        authRepository.logout();
                        Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_loginFragment);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void updateGames(){
        Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_allGamesFragment);

    }

    private void changeUsername(){

        View dialogView = getLayoutInflater().inflate(R.layout.change_username_dialog, null);

        TextInputEditText newUsernameEditText = dialogView.findViewById(R.id.newUsername_editText);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);
        Button saveBtn = dialogView.findViewById(R.id.saveUsername_btn);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        saveBtn.setOnClickListener(v -> {
            String newUsername = newUsernameEditText.getText().toString();
            profileRepository.changeUsername(currentUser.getUserId(), newUsername, new RepositoryCallback<Void>() {
                @Override
                public void onComplete(Result<Void> result) {

                }
            });

            dialog.dismiss();
        });

        dialog.show();
    }

    public void changeProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                Uri selectedImageUri = data.getData();
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                    binding.profilePicture.setImageBitmap(bitmap);

                    currentUser.setUserImage(bitmap);
                    profileRepository.updateUserImage(currentUser.getUserId(), currentUser.getImageString(), new RepositoryCallback<Void>() {
                        @Override
                        public void onComplete(Result<Void> result) {

                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
