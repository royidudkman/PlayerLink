package com.example.playerlink.repositories;

import androidx.annotation.NonNull;

import com.example.playerlink.Result;
import com.example.playerlink.models.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepositoryFirebase implements AuthRepository {
    static ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Executor executor;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    public AuthRepositoryFirebase() {
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        this.executor = executorService;
    }

    public AuthRepositoryFirebase(Executor executor) {
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        this.executor = executor;
    }

    @Override
    public void CurrentUser(final RepositoryCallback<User> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        userRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    callback.onComplete(new Result.Success<>(user));
                                } else {
                                    callback.onComplete(new Result.Error<>(new Exception("User data not found")));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                callback.onComplete(new Result.Error<>(databaseError.toException()));
                            }
                        });
                    } else {
                        callback.onComplete(new Result.Error<>(new Exception("Firebase user is null")));
                    }
                } catch (Exception e) {
                    callback.onComplete(new Result.Error<>(e));
                }
            }
        });
    }






    @Override
    public void Login(String email, String password, final RepositoryCallback<User> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<User> result = synchronousLogin(email, password);
                    callback.onComplete(result);
                } catch (Exception e) {
                    Result<User> errorResult = new Result.Error<>(e);
                    callback.onComplete(errorResult);
                }
            }
        });
    }


    private Result<User> synchronousLogin(String email, String password) {
        try {
            AuthResult authResult = Tasks.await(mAuth.signInWithEmailAndPassword(email, password));
            if (authResult != null && authResult.getUser() != null) {
                FirebaseUser firebaseUser = authResult.getUser();
                User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
                return new Result.Success<>(user);
            } else {
                return new Result.Error<>(new Exception("Login failed"));
            }
        } catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    @Override
    public void Register(String email, String password, String displayName, final RepositoryCallback<User> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<User> result = synchronousRegister(email, password, displayName);
                    callback.onComplete(result);
                } catch (Exception e) {
                    Result<User> errorResult = new Result.Error<>(e);
                    callback.onComplete(errorResult);
                }
            }
        });
    }


    private Result<User> synchronousRegister(String email, String password, String displayName) {
        try {
            AuthResult authResult = Tasks.await(mAuth.createUserWithEmailAndPassword(email, password));
            if (authResult != null && authResult.getUser() != null) {
                FirebaseUser firebaseUser = authResult.getUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName).build();
                firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    } else {
                    }
                });
                DatabaseReference newUserRef = userRef.child(firebaseUser.getUid());
                User user = new User(firebaseUser.getUid(), displayName, firebaseUser.getEmail());
                newUserRef.setValue(user);

                return new Result.Success<>(user);
            } else {
                return new Result.Error<>(new Exception("User registration failed"));
            }
        } catch (Exception e) {
            return new Result.Error<>(e);
        }
    }



    @Override
    public void logout() {
        mAuth.signOut();
    }
}
