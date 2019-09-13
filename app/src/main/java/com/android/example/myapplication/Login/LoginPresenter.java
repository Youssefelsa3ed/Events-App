package com.android.example.myapplication.Login;

import android.content.Intent;
import android.util.Log;

import com.android.example.myapplication.ItemListActivity;
import com.android.example.myapplication.Models.UserModel.User;
import com.android.example.myapplication.Utilities.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.MessageFormat;

public class LoginPresenter implements LoginViewPresenter {
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
                Log.i("googleEmail", MessageFormat.format("DisplayName : {0}\nFamilyName : {1}\nGivenName : {2}\nEmail: {3}" +
                                "\nId: {4}\nIdToken: {5}\nPhotoUri: {6}\n",
                        account.getDisplayName(), account.getFamilyName(), account.getGivenName(), account.getEmail(), account.getId(), account.getIdToken(), account.getPhotoUrl()));
                User tempUser = new User(account.getId(), account.getEmail(), account.getDisplayName(), account.getPhotoUrl());
                SharedPrefManager.getInstance(context).setLoginStatus(true);
                SharedPrefManager.getInstance(context).setUserData(tempUser);
                context.startActivity(new Intent(context, ItemListActivity.class));
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
}
