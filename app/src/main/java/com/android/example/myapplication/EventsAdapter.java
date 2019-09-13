package com.android.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.calendar.model.Event;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsAdapter extends ListAdapter<Event, EventsAdapter.ViewHolder> {
    private OnItemClickListener listener;

    public EventsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(Event oldItem, Event newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(Event oldItem, Event newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getStart().getDate().equals(newItem.getStart().getDate()) &&
                    oldItem.getEnd().getDate().equals(newItem.getEnd().getDate()) &&
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
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtLocation)
        TextView txtLocation;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
