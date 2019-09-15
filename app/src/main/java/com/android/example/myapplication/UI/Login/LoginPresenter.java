package com.android.example.myapplication.UI.Login;

import android.content.Intent;
import android.util.Log;

import com.android.example.myapplication.UI.EventsList.EventListActivity;
import com.android.example.myapplication.Utilities.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginPresenter implements LoginViewPresenter{
    private LoginActivity context;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "Login Activity";
    static final int RC_SIGN_IN=1;

    LoginPresenter(LoginActivity context) {
        this.context = context;
        prepareGoogleSignIn();
    }

    @Override
    public void prepareGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    @Override
    public void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        context.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                SharedPrefManager.getInstance(context).setLoginStatus(true);
                context.startActivity(new Intent(context, EventListActivity.class));
                context.finish();
            }
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
