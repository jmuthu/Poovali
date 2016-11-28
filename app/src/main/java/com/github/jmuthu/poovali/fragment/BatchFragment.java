package com.github.jmuthu.poovali.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.github.jmuthu.poovali.model.PlantContent;
import com.github.jmuthu.poovali.utility.Helper;

import java.text.DateFormat;
import java.util.List;

public class BatchFragment extends Fragment {
    PlantContent.Plant mPlant = null;
    RecyclerView mRecyclerView;

    public BatchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String plantId = getArguments().getString(Helper.ARG_PLANT_ID);
            mPlant = PlantContent.getPlant(plantId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.batch_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.batch_list);
        assert mRecyclerView != null;
        if (mPlant == null) {
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(BatchContent.getBatchList()));
        } else {
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mPlant.getBatchList()));
        }
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPlant != null) {
            mRecyclerView.getAdapter().notifyDataSetChanged(); // For adding activity
        } else {
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(BatchContent.getBatchList()));
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        final List<BatchContent.Batch> mValues;

        SimpleItemRecyclerViewAdapter(List<BatchContent.Batch> values) {
            this.mValues = values;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.batch_list_item, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int batchPosition) {
            holder.mBatch = mValues.get(batchPosition);
            if (mPlant == null) {
                holder.mPlantIconView.setVisibility(View.VISIBLE);
                holder.mPlantIconView.setImageResource(getResources().getIdentifier(
                        holder.mBatch.getImageName(),
                        "drawable",
                        holder.mPlantIconView.getContext().getPackageName()));
                holder.mNameView.setText(holder.mBatch.getName());
            } else {
                holder.mPlantIconView.setVisibility(View.GONE);
                holder.mNameView.setText(Helper.DATE_FORMAT.format(holder.mBatch.getCreatedDate()));
            }

            holder.mBatchStatusView.setText(holder.mBatch.getStage().toString());
            holder.mProgressBar.setProgress(holder.mBatch.getProgress());

            final EventContent.Event event = holder.mBatch.getEvents().get(0);
            holder.mEventIconView.setImageResource(getResources().getIdentifier(event.getImageName(),
                    "drawable",
                    holder.mPlantIconView.getContext().getPackageName()));

            DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
            String date = format.format(event.getCreatedDate());
            holder.mEventCreatedDateView.setText(date);
            String description = event.getDescription();
            if (description == null || description.isEmpty()) {
                holder.mEventDescriptionView.setText(event.getName());
            } else {
                holder.mEventDescriptionView.setText(description);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (event.getClass() == EventContent.BatchActivityEvent.class) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ViewEventActivity.class);
                        intent.putExtra(Helper.ARG_BATCH_ID, holder.mBatch.getId());
                        intent.putExtra(Helper.ARG_EVENT_ID, holder.mBatch.getEvents().get(0).getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mValues == null) {
                return 0;
            }
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mNameView;
            final TextView mBatchStatusView;
            final ProgressBar mProgressBar;
            final TextView mEventCreatedDateView;
            final TextView mEventDescriptionView;
            final ImageView mPlantIconView;
            final ImageView mEventIconView;
            BatchContent.Batch mBatch;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.batch);
                mBatchStatusView = (TextView) view.findViewById(R.id.batch_status);
                mProgressBar = (ProgressBar) view.findViewById(R.id.batch_status_progress);
                mEventCreatedDateView = (TextView) view.findViewById(R.id.event_created_date);
                mEventDescriptionView = (TextView) view.findViewById(R.id.event_description);
                mEventIconView = (ImageView) view.findViewById(R.id.event_type_icon);
                mPlantIconView = (ImageView) view.findViewById(R.id.plant_type_icon);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }


}
