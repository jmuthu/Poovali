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

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.onestraw.poovali.GardenEvent.items;

/**
 * Created by mike on 12/11/16.
 */

public class EventsFragment extends Fragment {
    public static final String EVENTS_FILE = "poovali_events.json";
    public static final List<GardenEvent> ITEMS = new ArrayList<GardenEvent>();

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

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.activities_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        try {
            File file = new File(getContext().getFilesDir(), EVENTS_FILE);
            if (file.isFile()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                GardenEvent obj = (GardenEvent) ois.readObject();
                items.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(new EventsFragment.SimpleItemRecyclerViewAdapter(items));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<EventsFragment.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<GardenEvent> mValues;

        public SimpleItemRecyclerViewAdapter(List<GardenEvent> items) {
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
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy h:mm a");
            String date = format.format(holder.mItem.getCreatedDate());
            holder.mEventCreatedDateView.setText(date);
            holder.mEventDescriptionView.setText(holder.mItem.getDescription());

            /*holder.mIconView.setImageResource(getResources().getIdentifier(holder.mItem.imageName,
                   holder.mIconView.getContext().getPackageName()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlantDetailActivity.class);
                    intent.putExtra(PlantContent.ARG_ITEM_ID, holder.mItem.id);
                    context.startActivity(intent);
                }
            });
            */
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mEventTypeView;
            public final TextView mEventCreatedDateView;
            public final TextView mEventDescriptionView;
            public final ImageView mIconView;
            public GardenEvent mItem;

            public ViewHolder(View view) {
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
