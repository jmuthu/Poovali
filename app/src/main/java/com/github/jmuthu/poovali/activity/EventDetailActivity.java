package com.github.jmuthu.poovali.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.model.event.EventRepository;
import com.github.jmuthu.poovali.model.plant.PlantBatch;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;

public class EventDetailActivity extends AppCompatActivity {
    PlantBatch mPlantBatch;
    Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int batchId = getIntent().getIntExtra(Helper.ARG_BATCH_ID, -1);
            int eventId = getIntent().getIntExtra(Helper.ARG_EVENT_ID, -1);
            mPlantBatch = PlantBatchRepository.find(batchId);
            mEvent = EventRepository.find(eventId);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(mEvent.getName());
        ImageView eventIconView = (ImageView) findViewById(R.id.event_type_icon);
        eventIconView.setImageResource(getResources().getIdentifier(
                Helper.getImageFileName(mEvent.getName()),
                "drawable",
                getPackageName()));

        TextView batchView = (TextView) findViewById(R.id.name);
        batchView.setText(mPlantBatch.getName());

        ImageView imageView = (ImageView) findViewById(R.id.plant_type_icon);
        Helper.setImageSrc(imageView, mPlantBatch);

        setupUpdatableViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUpdatableViews();
    }

    public void setupUpdatableViews() {
        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(Helper.DATE_FORMAT.format(mEvent.getCreatedDate()));
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(Helper.TIME_FORMAT.format(mEvent.getCreatedDate()));
        TextView descriptionView = (TextView) findViewById(R.id.event_description);
        if (mEvent.getDescription() == null || mEvent.getDescription().isEmpty()) {
            descriptionView.setVisibility(View.GONE);
            findViewById(R.id.event_description_icon).setVisibility(View.GONE);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            findViewById(R.id.event_description_icon).setVisibility(View.VISIBLE);
            descriptionView.setText(mEvent.getDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                DeleteEventDialogFragment dialog = new DeleteEventDialogFragment();
                dialog.setBatch(mPlantBatch);
                dialog.setEvent(mEvent);
                dialog.show(getSupportFragmentManager(), "DeleteEvent");
                return true;
            case R.id.edit:
                Intent intent = new Intent(this, AddEventActivity.class);
                intent.putExtra(Helper.ARG_EVENT_ID, mEvent.getId());
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DeleteEventDialogFragment extends DialogFragment {
        PlantBatch mPlantBatch;
        Event mEvent;

        public void setBatch(PlantBatch plantBatch) {
            mPlantBatch = plantBatch;
        }

        public void setEvent(Event event) {
            mEvent = event;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), R.style.AlertDialogTheme);
            builder.setTitle(R.string.delete_event_alert);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mPlantBatch.deleteEvent(mEvent);
                    EventRepository.delete(mEvent);
                    (getActivity()).finish();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
