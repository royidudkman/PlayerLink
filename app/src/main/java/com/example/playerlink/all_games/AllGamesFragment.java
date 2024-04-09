package com.example.playerlink.all_games;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.RAWG_api.DataService;
import com.example.playerlink.Result;
import com.example.playerlink.fragments.register_login_fragments.LoginFragment;
import com.example.playerlink.models.Game;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ProfileRepository;
import com.example.playerlink.repositories.ProfileRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AllGamesFragment extends Fragment {

    private RecyclerView recyclerView;
    private GameAdapter gameAdapter;
    private ProfileRepository profileRepository = new ProfileRepositoryFirebase();
    private User currentUser = LoginFragment.GetCurrentUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
        ArrayList<Game> allGames = DataService.getAllGames();
        gameAdapter = new GameAdapter(allGames);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(gameAdapter);

        Button saveGameBtn = view.findViewById(R.id.save_games_btn);
        saveGameBtn.setOnClickListener(v -> saveSelectedGames());

    }


    private void saveSelectedGames() {
        String userId = currentUser.getUserId();

        List<String> selectedGames = new ArrayList<>();
        for (Game game : gameAdapter.getGames()) {
            if (game.isSelected()) {
                selectedGames.add(game.getName());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.save_games);
        builder.setMessage(R.string.are_you_sure_you_want_to_save_the_selected_games_you_can_change_it_later_in_your_profile);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profileRepository.updateGamesList(userId, selectedGames, new RepositoryCallback<Void>() {
                    @Override
                    public void onComplete(Result<Void> result) {
                        if (result instanceof Result.Success) {
                            // Handle success
                            Toast.makeText(requireContext(), R.string.games_saved, Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.action_allGamesFragment_to_allUsersFragment2);
                        } else if (result instanceof Result.Error) {
                            // Handle error
                            Toast.makeText(requireContext(), R.string.error_saving_games, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
