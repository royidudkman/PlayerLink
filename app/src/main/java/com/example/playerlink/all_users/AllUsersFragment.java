package com.example.playerlink.all_users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.Result;
import com.example.playerlink.chat.ChatFragment;
import com.example.playerlink.databinding.FragmentAllUsersBinding;
import com.example.playerlink.models.Chat;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ProfileRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllUsersFragment extends Fragment implements AllUsersAdapter.OnUserClickListener {

    private FragmentAllUsersBinding binding;
    private AllUsersViewModel viewModel;
    private AllUsersAdapter adapter;
    private ProfileRepositoryFirebase repository = new ProfileRepositoryFirebase();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllUsersBinding.inflate(inflater, container, false);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AllUsersViewModel.class);

        // Get current logged-in user from ViewModel
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Initialize adapter with current user
                adapter = new AllUsersAdapter(requireContext(), new ArrayList<>(), user, AllUsersFragment.this);
                binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recycler.setAdapter(adapter);

                // Observe users
                observeUsers();
            }
        });
    }

    private String makeChatId(String userId1, String userId2){
        String chatId;

        if (userId1.compareTo(userId2) < 0) {
            chatId = userId1 + "_with_" + userId2;
        } else {
            chatId = userId2 + "_with_" + userId1;
        }
        return chatId;
    }

    @Override
    public void onUserClick(User currentUser, User otherUser) {
        String chatId = makeChatId(currentUser.getUserId(), otherUser.getUserId());
        Bundle args = new Bundle();
        args.putSerializable("currentUser", currentUser);
        args.putSerializable("otherUser", otherUser);
        args.putString("chatId", chatId);

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(args);

        NavHostFragment.findNavController(this).navigate(R.id.action_allUsersFragment_to_chatFragment,args);
    }

    private void observeUsers() {
        viewModel.getUsersLiveData().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setUserList(users);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
