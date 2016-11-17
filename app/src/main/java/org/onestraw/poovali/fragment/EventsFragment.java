package org.onestraw.poovali.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.onestraw.poovali.R;
import org.onestraw.poovali.model.BatchContent;
import org.onestraw.poovali.model.EventContent;
import org.onestraw.poovali.model.PlantContent;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventsFragment extends Fragment {

    RecyclerView recyclerView;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        recyclerView.setAdapter(new EventsFragment.SimpleItemRecyclerViewAdapter(EventContent.getItems(getActivity())));
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
            BatchContent.Batch batch = BatchContent.getItemMap(getActivity()).get(holder.mItem.getBatchId());

            SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
            String date = format.format(holder.mItem.getCreatedDate());
            holder.mEventCreatedDateView.setText(date);
            holder.mEventDescriptionView.setText(holder.mItem.getDescription());
            holder.mEventTypeView.setText(batch.getName());
            String imageName;
            if (batch.getPlantId().isEmpty()) {
                imageName = batch.getImageName();
            } else {
                imageName = PlantContent.getItemMap().get(batch.getPlantId()).getImageName();
            }
            holder.mPlantIconView.setImageResource(getResources().getIdentifier(imageName,
                    "drawable",
                    holder.mPlantIconView.getContext().getPackageName()));
            holder.mEventIconView.setImageResource(getResources().getIdentifier(holder.mItem.getImageName(),
                    "drawable",
                    holder.mPlantIconView.getContext().getPackageName()));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mEventTypeView;
            final TextView mEventCreatedDateView;
            final TextView mEventDescriptionView;
            final ImageView mPlantIconView;
            final ImageView mEventIconView;
            EventContent.Event mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mEventTypeView = (TextView) view.findViewById(R.id.event_type);
                mEventCreatedDateView = (TextView) view.findViewById(R.id.event_created_date);
                mEventDescriptionView = (TextView) view.findViewById(R.id.event_description);
                mEventIconView = (ImageView) view.findViewById(R.id.event_type_icon);
                mPlantIconView = (ImageView) view.findViewById(R.id.plant_type_icon);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mEventDescriptionView.getText() + "'";
            }
        }
    }


}
