package com.example.playerlink.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class ProfileFragment extends Fragment {

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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.changeUsernameBtn.setOnClickListener(v -> {changeUsername();});

        binding.updateGameBtn.setOnClickListener(v ->{ updateGames(); });

        binding.signOutBtn.setOnClickListener(v ->{ signOut(v);});

    }

    private void signOut(View v){
        authRepository.logout();
        Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_loginFragment);
    }

    private void updateGames(){
        Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_allGamesFragment);

    }

    private void changeUsername(){

//        profileRepository.changeUsername(currentUser.getUserId(), newUserName, new RepositoryCallback<Void>() {
//            @Override
//            public void onComplete(Result<Void> result) {
//
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
