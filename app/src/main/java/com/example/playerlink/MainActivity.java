package com.example.playerlink;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import com.example.playerlink.RAWG_api.DataService;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.AuthRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AuthRepositoryFirebase authRepository = new AuthRepositoryFirebase();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataService.loadAllGames();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.search_players) {
                navController.navigate(R.id.allUsersFragment);
                return true;
            } else if (item.getItemId() == R.id.chats) {
                navController.navigate(R.id.myChatsFragment);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                navController.navigate(R.id.profileFragment);
                return true;
            }
            return false;
        });

    }

    public void setBottomNavigationVisibility(int visibility) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(visibility);
    }
}
