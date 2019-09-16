package com.android.example.myapplication.UI.EventsList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.example.myapplication.GoogleCalenderViewModel.GoogleCalenderViewModel;
import com.android.example.myapplication.LocalDatabase.EventsDB;
import com.android.example.myapplication.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventListActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";


    /**
     * The dummy content this fragment is presenting.
     */
    private EventsDB mItem;

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
    private Unbinder unbinder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            GoogleCalenderViewModel googleCalenderViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GoogleCalenderViewModel.class);
            mItem = googleCalenderViewModel.getEvent(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getSummary());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        SimpleDateFormat format1 = new SimpleDateFormat("EEEE, MMM d", Locale.US);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        txtCreator.setText(mItem.getOrganizerEmail());
        try {
            if(mItem.getEventEndDate().equals(mItem.getEventStartDate())) {
                txtEventDate.setText(String.format("%s • %s - %s",
                        format1.format(format2.parse(mItem.getEventStartDate())),
                        mItem.getEventStartTime().substring(0, 5), mItem.getEventEndTime().substring(0, 5)));
            }
            else
                txtEventDate.setText(String.format("%s • %s \n%s • %s",
                        format1.format(format2.parse(mItem.getEventStartDate())), mItem.getEventStartTime().substring(0, 5),
                        format1.format(format2.parse(mItem.getEventEndDate())), mItem.getEventEndTime().substring(0, 5)));
        }
        catch (ParseException e) {
            if(mItem.getEventEndDate().equals(mItem.getEventStartDate())) {
                txtEventDate.setText(String.format("%s • %s - %s",
                        (mItem.getEventStartDate()),
                        mItem.getEventStartTime().substring(0, 5), mItem.getEventEndTime().substring(0, 5)));
            }
            else
                txtEventDate.setText(String.format("%s • %s \n%s • %s",
                        mItem.getEventStartDate(), mItem.getEventStartTime().substring(0, 5),
                        mItem.getEventEndDate(), mItem.getEventEndTime().substring(0, 5)));
        }
        txtLocation.setText(mItem.getLocation());
        txtStatus.setText(mItem.getStatus().replace("accepted","Accepted")
                .replace("tentative", "Pending").replace("needsAction", "Pending"));
        txtSummary.setText(mItem.getSummary());

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
