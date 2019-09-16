package com.android.example.myapplication.UI.EventsList;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.myapplication.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a RelativeLayout with BottomSheetBehavior representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EventListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvEvents)
    RecyclerView rvEvents;
    RelativeLayout eventLayout;
    public BottomSheetBehavior bottomSheetBehavior;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.txtEventDate)
    TextView txtEventDate;
    @BindView(R.id.txtLocation)
    TextView txtLocation;
    @BindView(R.id.txtCreator)
    TextView txtCreator;
    @BindView(R.id.txtSummary)
    TextView txtSummary;
    @BindView(R.id.txtStatus)
    TextView txtStatus;
    private EventsListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        presenter = new EventsListPresenter(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EventsListPresenter.RC_AUTHORIZE_CALENDER
                || requestCode == EventsListPresenter.RC_REAUTHORIZE) {
            if (resultCode == RESULT_OK) {
                presenter.reAssignLiveDataObserver();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.logout) {
            presenter.logout();
        }
        return false;
    }
}
