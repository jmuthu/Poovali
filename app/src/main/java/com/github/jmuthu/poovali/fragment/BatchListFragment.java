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
import com.github.jmuthu.poovali.activity.BatchDetailActivity;
import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.model.plant.Plant;
import com.github.jmuthu.poovali.model.plant.PlantBatch;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
import com.github.jmuthu.poovali.utility.Helper;

import java.text.DateFormat;
import java.util.List;

public class BatchListFragment extends Fragment {
    Plant mPlant = null;
    RecyclerView mRecyclerView;

    public BatchListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String plantId = getArguments().getString(Helper.ARG_PLANT_ID);
            mPlant = PlantRepository.find(plantId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_batch_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.batch_list);
        assert mRecyclerView != null;
        if (mPlant == null) {
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(PlantBatchRepository.findAll(true)));
        } else {
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mPlant.getPlantBatchList()));
        }
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPlant != null) {
            mRecyclerView.getAdapter().notifyDataSetChanged(); // For adding activity
        } else {
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(PlantBatchRepository.findAll(true)));
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        final List<PlantBatch> mValues;

        SimpleItemRecyclerViewAdapter(List<PlantBatch> values) {
            this.mValues = values;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_batch, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int batchPosition) {
            holder.mPlantBatch = mValues.get(batchPosition);
            if (mPlant == null) {
                holder.mPlantIconView.setVisibility(View.VISIBLE);
                Helper.setImageSrc(holder.mPlantIconView, holder.mPlantBatch);
                holder.mNameView.setText(holder.mPlantBatch.getName());
            } else {
                holder.mPlantIconView.setVisibility(View.GONE);
                holder.mNameView.setText(Helper.DATE_FORMAT.format(holder.mPlantBatch.getCreatedDate()));
            }

            holder.mBatchStatusView.setText(holder.mPlantBatch.getStage().toString());
            holder.mProgressBar.setProgress(holder.mPlantBatch.getProgress());
            String description = holder.mPlantBatch.getDescription();
            if (!holder.mPlantBatch.getEvents().isEmpty()) {
                Event event = holder.mPlantBatch.getEvents().get(0);
                holder.mEventIconView.setImageResource(getResources().getIdentifier(
                        Helper.getImageFileName(event.getName()),
                        "drawable",
                        holder.mPlantIconView.getContext().getPackageName()));

                DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
                String date = format.format(event.getCreatedDate());
                holder.mEventCreatedDateView.setText(date);
                description = event.getDescription();
                if (description == null || description.isEmpty()) {
                    description = event.getName();
                }
            } else {
                holder.mEventIconView.setImageResource(R.drawable.sow);
                description = getString(R.string.sow_type);
            }
            holder.mEventDescriptionView.setText(description);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, BatchDetailActivity.class);
                    intent.putExtra(Helper.ARG_BATCH_ID, holder.mPlantBatch.getId());
                    context.startActivity(intent);
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
            PlantBatch mPlantBatch;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.name);
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

