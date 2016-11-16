package org.onestraw.poovali;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
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
        recyclerView.setAdapter(new EventsFragment.SimpleItemRecyclerViewAdapter(EventContent.getEventList(getContext())));
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
            holder.mEventTypeView.setText(holder.mItem.getType().toString());
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            String date = df.format(holder.mItem.getCreatedDate());
            holder.mEventCreatedDateView.setText(date);
            holder.mEventDescriptionView.setText(holder.mItem.getDescription());

            holder.mIconView.setImageResource(getResources().getIdentifier(Helper.getImageFileName(holder.mItem.getType().toString()),
                    "drawable",
                    holder.mIconView.getContext().getPackageName()));
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
            final ImageView mIconView;
            EventContent.Event mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mEventTypeView = (TextView) view.findViewById(R.id.event_type);
                mEventCreatedDateView = (TextView) view.findViewById(R.id.event_created_date);
                mEventDescriptionView = (TextView) view.findViewById(R.id.event_description);
                mIconView = (ImageView) view.findViewById(R.id.event_type_icon);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mEventDescriptionView.getText() + "'";
            }
        }
    }


}
