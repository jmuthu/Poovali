package org.onestraw.poovali;

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

import java.util.List;

public class PlantsFragment extends Fragment {
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

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.plant_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(PlantContent.getItems()));
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
            holder.mItem = mValues.get(position);
            holder.mNameView.setText(holder.mItem.getName());
            String days = String.format(getResources().getString(R.string.days), holder.mItem.getCropDuration().toString());
            holder.mContentView.setText(days);

            holder.mIconView.setImageResource(getResources().getIdentifier(
                    holder.mItem.getImageName(),
                    "drawable",
                    holder.mIconView.getContext().getPackageName()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlantDetailActivity.class);
                    intent.putExtra(PlantContent.ARG_ITEM_ID, holder.mItem.getId());
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
            final ImageView mIconView;
            PlantContent.Plant mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.name);
                mContentView = (TextView) view.findViewById(R.id.content);
                mIconView = (ImageView) view.findViewById(R.id.image_icon);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }


}
