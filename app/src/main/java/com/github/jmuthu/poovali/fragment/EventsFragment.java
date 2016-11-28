package com.github.jmuthu.poovali.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.activity.ViewEventActivity;
import com.github.jmuthu.poovali.model.BatchContent;
import com.github.jmuthu.poovali.model.EventContent;
import com.github.jmuthu.poovali.utility.Helper;

import java.text.DateFormat;
import java.util.List;

public class EventsFragment extends Fragment {
    BatchContent.Batch batch = null;
    RecyclerView recyclerView;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String batchId = getArguments().getString(Helper.ARG_BATCH_ID);
            batch = BatchContent.getBatch(batchId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.events_fragment, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.activities_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged(); // For adding activity
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new EventsFragment.SimpleItemRecyclerViewAdapter(batch.getEvents()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<EventsFragment.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<EventContent.Event> mValues;

        SimpleItemRecyclerViewAdapter(List<EventContent.Event> items) {
            mValues = items;
        }

        @Override
        public EventsFragment.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_list_item, parent, false);
            return new EventsFragment.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventsFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
            String date = format.format(holder.mItem.getCreatedDate());
            holder.mEventCreatedDateView.setText(date);
            String description = holder.mItem.getDescription();
            if (description.isEmpty()) {
                holder.mEventDescriptionView.setText(holder.mItem.getName());
            } else {
                holder.mEventDescriptionView.setText(description);
            }

            holder.mBatchNameView.setText(batch.getName());
            if (batch.getPlant() != null) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.mBatchStatusView.setText(batch.getStage().toString());
                holder.mProgressBar.setProgress(batch.getProgress());
            } else {
                holder.mProgressBar.setVisibility(View.GONE);
            }

            holder.mPlantIconView.setImageResource(getResources().getIdentifier(batch.getImageName(),
                    "drawable",
                    holder.mPlantIconView.getContext().getPackageName()));
            holder.mEventIconView.setImageResource(getResources().getIdentifier(holder.mItem.getImageName(),
                    "drawable",
                    holder.mPlantIconView.getContext().getPackageName()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mItem.getClass() == EventContent.BatchActivityEvent.class) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ViewEventActivity.class);
                        intent.putExtra(Helper.ARG_EVENT_ID, holder.mItem.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mBatchNameView;
            final TextView mEventCreatedDateView;
            final TextView mEventDescriptionView;
            final TextView mBatchStatusView;
            final ImageView mPlantIconView;
            final ImageView mEventIconView;
            final ProgressBar mProgressBar;
            EventContent.Event mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mBatchNameView = (TextView) view.findViewById(R.id.batch);
                mEventCreatedDateView = (TextView) view.findViewById(R.id.event_created_date);
                mEventDescriptionView = (TextView) view.findViewById(R.id.event_description);
                mBatchStatusView = (TextView) view.findViewById(R.id.batch_status);
                mEventIconView = (ImageView) view.findViewById(R.id.event_type_icon);
                mPlantIconView = (ImageView) view.findViewById(R.id.plant_type_icon);
                mProgressBar = (ProgressBar) view.findViewById(R.id.batch_status_progress);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mEventDescriptionView.getText() + "'";
            }
        }
    }
}
