package com.android.example.myapplication.Login;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public interface LoginViewPresenter {
    void prepareGoogleSignIn();
    void loginWithGoogle();
    void handleSignInResult(Task<GoogleSignInAccount> completedTask);
}
