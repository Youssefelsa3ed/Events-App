package com.android.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.myapplication.GoogleCalenderViewModel.GoogleCalenderRepository;
import com.android.example.myapplication.GoogleCalenderViewModel.GoogleCalenderViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    RelativeLayout eventLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    public BottomSheetBehavior bottomSheetBehavior;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private GoogleCalenderViewModel calenderViewModel;
    private EventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        } else {
            mTwoPane = false;
            eventLayout = findViewById(R.id.eventLayout);
            bottomSheetBehavior = BottomSheetBehavior.from(eventLayout);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }


        calenderViewModel = ViewModelProviders.of(this).get(GoogleCalenderViewModel.class);
        eventsAdapter = new EventsAdapter();
        calenderViewModel.getAllEvents().observe(this, events -> eventsAdapter.submitList(events));
        AtomicInteger count = new AtomicInteger(0);
        eventsAdapter.setOnItemClickListener((event, position) -> {
            calenderViewModel.updateEvent(event.setStatus("confirmed").setDescription("desc231" + count.getAndIncrement()));
            eventsAdapter.notifyItemChanged(position);
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ItemDetailFragment.ARG_ITEM_ID, event.getId());
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                /*Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, event.getId());

                startActivity(intent);
*/
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GoogleCalenderRepository.RC_AUTHORIZE_CALENDER
                || requestCode == GoogleCalenderRepository.RC_REAUTHORIZE) {
            if (resultCode == RESULT_OK) {
                calenderViewModel.getAllEvents().removeObservers(this);
                calenderViewModel.getAllEvents().observe(this, events -> eventsAdapter.submitList(events));
            }
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(eventsAdapter);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        Snackbar.make(fab, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
