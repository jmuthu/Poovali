package com.github.jmuthu.poovali.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.activity.PlantDetailActivity;
import com.github.jmuthu.poovali.model.plant.Plant;
import com.github.jmuthu.poovali.model.plant.PlantBatch;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
import com.github.jmuthu.poovali.utility.Helper;

import java.text.DateFormat;
import java.util.List;

public class PlantListFragment extends Fragment {
    RecyclerView mRecyclerView;

    public PlantListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plant_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.plant_list);
        assert mRecyclerView != null;
        mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(PlantRepository.findAll()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(new PlantListFragment.SimpleItemRecyclerViewAdapter(PlantRepository.findAll()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Plant> mValues;

        SimpleItemRecyclerViewAdapter(List<Plant> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_plant, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mPlant = mValues.get(position);

            String name = holder.mPlant.getName();
            if (!holder.mPlant.getPlantBatchList().isEmpty()) {
                name += " (" + holder.mPlant.getPlantBatchList().size() + ")";
            }

            holder.mNameView.setText(name);
            String days = String.format(getResources().getString(R.string.days),
                    holder.mPlant.getCropDuration().toString());
            holder.mContentView.setText("Duration : " + days);

            PlantBatch latestPlantBatch = holder.mPlant.getLatestBatch();
            if (latestPlantBatch != null) {
                DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
                holder.mLastBatchDateView.setText("Last sowed : " +
                        format.format(latestPlantBatch.getCreatedDate()));
            }

            Helper.setOverDueText(holder.mPlant, holder.mNextBatchDueView, Color.rgb(255, 140, 0));
            Helper.setImageSrc(holder.mIconView, holder.mPlant);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlantDetailActivity.class);
                    intent.putExtra(Helper.ARG_PLANT_ID, holder.mPlant.getId());
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
            Plant mPlant;

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
