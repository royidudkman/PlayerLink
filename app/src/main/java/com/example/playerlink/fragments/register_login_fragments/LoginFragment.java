package com.example.playerlink.fragments.register_login_fragments;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.Result;
import com.example.playerlink.databinding.FragmentLoginBinding;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.AuthRepositoryFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private AuthRepositoryFirebase authRepository;
    private static User currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
        authRepository = new AuthRepositoryFirebase();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(authRepository)).get(LoginViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.signUpTextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        viewModel.getUserSignInStatus().observe(getViewLifecycleOwner(),result ->  {
            if (result instanceof Result.Loading) {
                binding.loadingProgressbar.setVisibility(View.VISIBLE);
                binding.signUpTextBtn.setEnabled(false);

            } else if (result instanceof Result.Success) {
                currentUser = ((Result.Success<User>) result).data;
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_allUsersFragment);

            } else {
                binding.signUpTextBtn.setEnabled(true);
                Exception exception = ((Result.Error<?>) result).exception;
                String errorMessage = exception.getMessage();
                binding.errorMessageTextView.setText(errorMessage);
                binding.errorMessageTextView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(),result ->  {
            if (result instanceof Result.Loading) {
                binding.loadingProgressbar.setVisibility(View.VISIBLE);
                binding.signUpTextBtn.setEnabled(false);

            } else if (result instanceof Result.Success) {
                currentUser = ((Result.Success<User>) result).data;
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_allUsersFragment);

            } else {
                binding.loadingProgressbar.setVisibility(View.GONE);
                binding.signUpTextBtn.setEnabled(true);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.textInputEmail.getEditText().getText().toString().trim();
                String password = binding.textInputPassword.getEditText().getText().toString().trim();
                viewModel.Login(email, password);
            }
        });

    }

    public static User GetCurrentUser(){
        return currentUser;
    }
    public static void SetCurrentUser(User user){
        currentUser = user;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
