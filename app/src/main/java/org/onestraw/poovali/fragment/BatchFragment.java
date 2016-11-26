package org.onestraw.poovali.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.onestraw.poovali.R;
import org.onestraw.poovali.model.BatchContent;
import org.onestraw.poovali.model.PlantContent;
import org.onestraw.poovali.utility.Helper;

import java.util.List;

import static org.onestraw.poovali.R.id.batch;


public class BatchFragment extends Fragment {
    PlantContent.Plant plant;
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

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(BatchContent.getBatchList(getActivity(), plant.getId())));
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged(); // For adding activity
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<BatchContent.Batch> mValues;

        SimpleItemRecyclerViewAdapter(List<BatchContent.Batch> items) {
            mValues = items;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.batch_list_item, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            final int plantId = position;
            holder.mItem = mValues.get(position);

            holder.mNameView.setText(Helper.DATE_FORMAT.format(holder.mItem.getCreatedDate()));

            Integer overDue = BatchContent.pendingSowDays(holder.mItem.getId());
            holder.mBatchStatusView.setText(holder.mItem.getPlant().getStage(holder.mItem.getCreatedDate()).toString());
            holder.mProgressBar.setProgress(holder.mItem.getPlant().getProgress(holder.mItem.getCreatedDate()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/*
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlantDetailActivity.class);
                    intent.putExtra(Helper.ARG_PLANT_ID, plantId);
                    context.startActivity(intent);
*/
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mNameView;
            final TextView mBatchStatusView;
            final ProgressBar mProgressBar;
            BatchContent.Batch mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(batch);
                mBatchStatusView = (TextView) view.findViewById(R.id.batch_status);
                mProgressBar = (ProgressBar) view.findViewById(R.id.batch_status_progress);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }


}

