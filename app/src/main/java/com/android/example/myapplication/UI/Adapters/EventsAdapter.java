package com.android.example.myapplication.UI.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.myapplication.LocalDatabase.EventsDB;
import com.android.example.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsAdapter extends ListAdapter<EventsDB, EventsAdapter.ViewHolder> {
    private setOnChipClickListener chipListener;
    private setOnItemClickListener itemListener;

    public EventsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<EventsDB> DIFF_CALLBACK = new DiffUtil.ItemCallback<EventsDB>() {
        @Override
        public boolean areItemsTheSame(EventsDB oldItem, EventsDB newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(EventsDB oldItem, EventsDB newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getStatus().equals(newItem.getStatus());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtLocation.setText(getItem(holder.getAdapterPosition()).getLocation());
        holder.txtStatus.setText(getItem(holder.getAdapterPosition()).getStatus().replace("accepted","Accepted")
                .replace("tentative", "Pending").replace("needsAction", "Pending"));

        holder.txtEventDate.setText(String.format("%s : %s",
                getItem(holder.getAdapterPosition()).getEventStartDate(),
                getItem(holder.getAdapterPosition()).getEventStartTime().substring(0, 5)));

        holder.txtHumidity.setText(String.format(Locale.US, "%d", getItem(holder.getAdapterPosition()).getHumidity()));
        holder.txtWindSpeed.setText(String.format(Locale.US, "%.2f", getItem(holder.getAdapterPosition()).getWindSpeed()));
        holder.txtTemperature.setText(String.format(Locale.US, "%.0f - %.0f°C",
                getItem(holder.getAdapterPosition()).getTemperatureMin(),
                getItem(holder.getAdapterPosition()).getTemperatureMax()));

        if (getItem(position).getWeatherIcon().equals(""))
            holder.weatherLayout.setVisibility(View.GONE);
        else {
            holder.weatherLayout.setVisibility(View.VISIBLE);
            holder.ivWeatherIcon.setImageURI(getItem(holder.getAdapterPosition()).getWeatherIcon());
        }

        if (getItem(holder.getAdapterPosition()).getStatus().equals("tentative") || getItem(holder.getAdapterPosition()).getStatus().equals("needsAction"))
            holder.chipGroup.setVisibility(View.VISIBLE);
        else
            holder.chipGroup.setVisibility(View.GONE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtLocation)
        TextView txtLocation;
        @BindView(R.id.txtEventDate)
        TextView txtEventDate;
        @BindView(R.id.txtStatus)
        TextView txtStatus;
        @BindView(R.id.ivWeatherIcon)
        SimpleDraweeView ivWeatherIcon;
        @BindView(R.id.txtTemperature)
        TextView txtTemperature;
        @BindView(R.id.txtHumidity)
        TextView txtHumidity;
        @BindView(R.id.txtWindSpeed)
        TextView txtWindSpeed;
        @BindView(R.id.weatherLayout)
        LinearLayout weatherLayout;
        @BindView(R.id.acceptChip)
        Chip acceptChip;
        @BindView(R.id.refuseChip)
        Chip refuseChip;
        @BindView(R.id.chipGroup)
        ChipGroup chipGroup;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            acceptChip.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (chipListener != null && position != RecyclerView.NO_POSITION) {
                    chipListener.onItemClick(getItem(position), position, "accepted");
                }
            });

            refuseChip.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (chipListener != null && position != RecyclerView.NO_POSITION) {
                    chipListener.onItemClick(getItem(position), position, "declined");
                }
            });
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (itemListener != null && position != RecyclerView.NO_POSITION) {
                    itemListener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface setOnChipClickListener {
        void onItemClick(EventsDB event, int position, String action);
    }

    public void setOnChipClickListener(setOnChipClickListener listener) {
        this.chipListener = listener;
    }

    public interface setOnItemClickListener {
        void onItemClick(EventsDB event);
    }

    public void setOnItemClickListener(setOnItemClickListener listener) {
        this.itemListener = listener;
    }
}
