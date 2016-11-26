package org.onestraw.poovali.fragment;

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
import android.widget.TextView;

import org.onestraw.poovali.PlantDetailActivity;
import org.onestraw.poovali.R;
import org.onestraw.poovali.model.BatchContent;
import org.onestraw.poovali.model.PlantContent;
import org.onestraw.poovali.utility.Helper;

import java.text.DateFormat;
import java.util.List;

public class PlantsFragment extends Fragment {
    RecyclerView recyclerView;
    public PlantsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.plants_fragment, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.plant_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(PlantContent.getItems()));
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged(); // For adding activity
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PlantContent.Plant> mValues;

        SimpleItemRecyclerViewAdapter(List<PlantContent.Plant> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.plant_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final int plantId = position;
            holder.mItem = mValues.get(position);

            Integer batchCount = BatchContent.getNoOfItems(holder.mItem.getId());
            String name = holder.mItem.getName();
            if (batchCount > 0) {
                name += " (" + batchCount + ")";
            }

            holder.mNameView.setText(name);
            String days = String.format(getResources().getString(R.string.days), holder.mItem.getCropDuration().toString());
            holder.mContentView.setText("Duration : " + days);

            Integer overDue = BatchContent.pendingSowDays(holder.mItem.getId());
            BatchContent.Batch lastBatch = BatchContent.getLastBatch(holder.mItem.getId());
            if (lastBatch != null) {
                DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
                holder.mLastBatchDateView.setText("Last sowed : " +
                        format.format(lastBatch.getCreatedDate()));
            }
            if (overDue != null) {
                if (overDue == 0) {
                    holder.mNextBatchDueView.setText("Sow today");
                } else if (overDue > 0) {
                    String text = overDue == 1 ? overDue + " day overdue" : overDue + " days overdue";
                    holder.mNextBatchDueView.setText(text);
                    holder.mNextBatchDueView.setTextColor(getResources().getColor(R.color.textWarn));
                } else {
                    holder.mNextBatchDueView.setText("Sow in " + overDue * -1 + " days");
                }
            }


            holder.mIconView.setImageResource(getResources().getIdentifier(
                    holder.mItem.getImageName(),
                    "drawable",
                    holder.mIconView.getContext().getPackageName()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlantDetailActivity.class);
                    intent.putExtra(Helper.ARG_PLANT_ID, plantId);
                    context.startActivity(intent);
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
            final TextView mContentView;
            final TextView mNextBatchDueView;
            final TextView mLastBatchDateView;
            final ImageView mIconView;
            PlantContent.Plant mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.name);
                mContentView = (TextView) view.findViewById(R.id.content);
                mNextBatchDueView = (TextView) view.findViewById(R.id.next_batch_due);
                mLastBatchDateView = (TextView) view.findViewById(R.id.last_batch_date);
                mIconView = (ImageView) view.findViewById(R.id.image_icon);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }


}
