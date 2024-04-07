package com.example.playerlink.all_users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.example.playerlink.RAWG_api.DataService;
import com.example.playerlink.chat.ChatFragment;
import com.example.playerlink.databinding.FragmentAllUsersBinding;
import com.example.playerlink.fragments.register_login_fragments.LoginFragment;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ProfileRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;
import com.example.playerlink.Result;

import java.util.ArrayList;
import java.util.List;

public class AllUsersFragment extends Fragment implements AllUsersAdapter.OnUserClickListener {

    private FragmentAllUsersBinding binding;
    private AllUsersViewModel viewModel;
    private AllUsersAdapter adapter;
    private ProfileRepositoryFirebase profileRepository = new ProfileRepositoryFirebase();
    private ArrayList<String> allGames;
    ;
    private User currentUser = LoginFragment.GetCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllUsersBinding.inflate(inflater, container, false);

        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
        if (allGames == null || allGames.isEmpty()) {
            allGames = new ArrayList<>();
            allGames = DataService.getAllGamesNames();
            if (!allGames.contains("Filter by game")) {
                allGames.add(0, "Filter by game");
            }
        }
        Spinner spinnerGames = binding.spinner;
        ArrayAdapter<String> gamesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, allGames);
        gamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGames.setAdapter(gamesAdapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AllUsersViewModel.class);

        // Observe current user LiveData

        adapter = new AllUsersAdapter(requireContext(), new ArrayList<>(), currentUser, AllUsersFragment.this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe LiveData from ViewModel
        viewModel.getUsersLiveData().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                // Update UI with users data
                adapter.setUserList(users);
            }
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGame = parent.getItemAtPosition(position).toString();
                if (selectedGame.equals("Filter by game")) {
                    // Fetch all users without filtering
                    observeUsers();
                } else {
                    // Fetch users based on the selected game
                    profileRepository.getUsersByGame(selectedGame, currentUser.getUserId(), new RepositoryCallback<List<User>>() {
                        @Override
                        public void onComplete(Result<List<User>> result) {
                            if (result instanceof Result.Success) {
                                List<User> users = ((Result.Success<List<User>>) result).data;
                                adapter.setUserList(users);
                            } else if (result instanceof Result.Error) {
                                // Handle error
                                Toast.makeText(requireContext(), "Error fetching users", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
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

    @Override
    public void onUserClick(User currentUser, User otherUser) {
        String chatId = makeChatId(currentUser.getUserId(), otherUser.getUserId());
        Bundle args = new Bundle();
        args.putSerializable("currentUser", currentUser);
        args.putSerializable("otherUser", otherUser);
        args.putString("chatId", chatId);

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(args);

        NavHostFragment.findNavController(this).navigate(R.id.action_allUsersFragment_to_chatFragment, args);
    }

    private String makeChatId(String userId1, String userId2) {
        String chatId;

        if (userId1.compareTo(userId2) < 0) {
            chatId = userId1 + "_with_" + userId2;
        } else {
            chatId = userId2 + "_with_" + userId1;
        }
        return chatId;
    }
}
