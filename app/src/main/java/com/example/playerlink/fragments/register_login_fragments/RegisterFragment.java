package com.example.playerlink.fragments.register_login_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.Result;
import com.example.playerlink.models.User;
import com.example.playerlink.databinding.FragmentRegisterBinding;
import com.example.playerlink.repositories.AuthRepositoryFirebase;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel viewModel;
    private AuthRepositoryFirebase authRepository;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
        authRepository = new AuthRepositoryFirebase();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(authRepository)).get(RegisterViewModel.class);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signInTextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), new Observer<Result<User>>() {
            @Override
            public void onChanged(Result<User> result) {
                if (result instanceof Result.Success) {
                    LoginFragment.SetCurrentUser(((Result.Success<User>) result).data);
                    Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_allGamesFragment);
                } else if(result instanceof Result.Loading) {
                    binding.loadingProgressbar.setVisibility(View.VISIBLE);
                } else {
                    Exception exception = ((Result.Error<User>) result).exception;
                    String errorMessage = exception.getMessage();
                    binding.errorMessageTextView.setText(errorMessage);
                    binding.errorMessageTextView.setVisibility(View.VISIBLE);
                }
            }
        });


        binding.registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = binding.textInputEmail.getEditText().getText().toString().trim();
                String password = binding.textInputPassword.getEditText().getText().toString().trim();
                String displayName = binding.textInputName.getEditText().getText().toString().trim();
                viewModel.Register(email, password, displayName);
            }
        });

    }








    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
