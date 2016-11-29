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
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.activity.ViewEventActivity;
import com.github.jmuthu.poovali.model.BatchContent;
import com.github.jmuthu.poovali.model.EventContent;
import com.github.jmuthu.poovali.utility.Helper;

import java.text.DateFormat;
import java.util.List;

public class EventFragment extends Fragment {
    BatchContent.Batch mBatch = null;
    RecyclerView recyclerView;

    public EventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String batchId = getArguments().getString(Helper.ARG_BATCH_ID);
            mBatch = BatchContent.getBatch(batchId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.events_fragment, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.activities_list);
        assert recyclerView != null;
        recyclerView.setAdapter(new EventFragment.SimpleItemRecyclerViewAdapter(mBatch.getEvents()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<EventFragment.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<EventContent.Event> mValues;

        SimpleItemRecyclerViewAdapter(List<EventContent.Event> items) {
            mValues = items;
        }

        @Override
        public EventFragment.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_list_item, parent, false);
            return new EventFragment.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            if (position == mValues.size() - 1) { //ignore the sow activity
                return;
            }
            holder.mItem = mValues.get(position);

            DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
            String date = format.format(holder.mItem.getCreatedDate());

            holder.mEventNameView.setText(holder.mItem.getName());
            holder.mEventCreatedDateView.setText(date);
            String description = holder.mItem.getDescription();
            holder.mEventDescriptionView.setText(description);
            holder.mEventIconView.setImageResource(getResources().getIdentifier(holder.mItem.getImageName(),
                    "drawable",
                    holder.mEventIconView.getContext().getPackageName()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mItem.getClass() == EventContent.BatchActivityEvent.class) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ViewEventActivity.class);
                        intent.putExtra(Helper.ARG_EVENT_ID, holder.mItem.getId());
                        intent.putExtra(Helper.ARG_BATCH_ID, mBatch.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size() - 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mEventNameView;
            final TextView mEventCreatedDateView;
            final TextView mEventDescriptionView;
            final ImageView mEventIconView;
            EventContent.Event mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mEventNameView = (TextView) view.findViewById(R.id.event_name);
                mEventCreatedDateView = (TextView) view.findViewById(R.id.event_created_date);
                mEventDescriptionView = (TextView) view.findViewById(R.id.event_description);
                mEventIconView = (ImageView) view.findViewById(R.id.event_type_icon);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mEventNameView.getText() + "'";
            }
        }
    }
}
