package org.onestraw.poovali.fragment;

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

import org.onestraw.poovali.R;
import org.onestraw.poovali.activity.ViewEventActivity;
import org.onestraw.poovali.model.BatchContent;
import org.onestraw.poovali.model.EventContent;
import org.onestraw.poovali.model.PlantContent;
import org.onestraw.poovali.utility.Helper;

import java.text.DateFormat;

import static org.onestraw.poovali.R.id.batch;


public class BatchFragment extends Fragment {
    PlantContent.Plant plant = null;
    RecyclerView recyclerView;

    public BatchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int plantId = getArguments().getInt(Helper.ARG_PLANT_ID);
            plant = PlantContent.getItems().get(plantId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.batch_fragment, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.batch_list);
        assert recyclerView != null;
        if (plant != null) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(plant.getId()));
        } else {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(null));
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged(); // For adding activity
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        final String plantId;

        SimpleItemRecyclerViewAdapter(String plantId) {
            this.plantId = plantId;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.batch_list_item, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {
            if (plantId == null) {
                holder.mItem = BatchContent.getItems().get(position);
                holder.mPlantIconView.setVisibility(View.VISIBLE);
                holder.mPlantIconView.setImageResource(getResources().getIdentifier(
                        holder.mItem.getImageName(),
                        "drawable",
                        holder.mPlantIconView.getContext().getPackageName()));
                holder.mNameView.setText(holder.mItem.getName());
            } else {
                holder.mPlantIconView.setVisibility(View.GONE);
                holder.mItem = BatchContent.getBatchList(plantId).get(position);
                holder.mNameView.setText(Helper.DATE_FORMAT.format(holder.mItem.getCreatedDate()));
            }

            if (holder.mItem.getPlant() != null) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.mBatchStatusView.setText(holder.mItem.getPlant().getStage(holder.mItem.getCreatedDate()).toString());
                holder.mProgressBar.setProgress(holder.mItem.getPlant().getProgress(holder.mItem.getCreatedDate()));
            } else {
                holder.mProgressBar.setVisibility(View.GONE);
            }

            if (holder.mItem.getEvents() != null) {
                final EventContent.Event event = holder.mItem.getEvents().get(0);
                holder.mEventIconView.setImageResource(getResources().getIdentifier(event.getImageName(),
                        "drawable",
                        holder.mPlantIconView.getContext().getPackageName()));

                DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
                String date = format.format(event.getCreatedDate());
                holder.mEventCreatedDateView.setText(date);
                String description = event.getDescription();
                if (description.isEmpty()) {
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
                            if (plantId != null) {
                                intent.putExtra(Helper.ARG_BATCH_ID, BatchContent.getItems().indexOf(holder.mItem));
                            } else {
                                intent.putExtra(Helper.ARG_BATCH_ID, position);
                            }
                            intent.putExtra(Helper.ARG_EVENT_ID, 0);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (plantId == null) {
                return BatchContent.getItems().size();
            }
            return BatchContent.getNoOfItems(plantId);
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
            BatchContent.Batch mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(batch);
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

