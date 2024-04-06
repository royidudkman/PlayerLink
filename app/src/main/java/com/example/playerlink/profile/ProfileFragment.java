package com.example.playerlink.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.databinding.FragmentProfileBinding;
import com.example.playerlink.databinding.FragmentRegisterBinding;
import com.example.playerlink.repositories.AuthRepositoryFirebase;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthRepositoryFirebase authRepository;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
        authRepository = new AuthRepositoryFirebase();
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

    }

    private void changeUsername(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
