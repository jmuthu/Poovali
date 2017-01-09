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
import com.github.jmuthu.poovali.model.Batch;
import com.github.jmuthu.poovali.model.BatchRepository;
import com.github.jmuthu.poovali.model.EventContent;
import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;

public class EventDetailActivity extends AppCompatActivity {
    static Batch batch;
    static EventContent.Event event;

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
            String batchId = getIntent().getStringExtra(Helper.ARG_BATCH_ID);
            String eventId = getIntent().getStringExtra(Helper.ARG_EVENT_ID);
            batch = BatchRepository.getBatch(batchId);
            event = EventContent.getEvent(eventId);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(event.getName());
        ImageView eventIconView = (ImageView) findViewById(R.id.event_type_icon);
        eventIconView.setImageResource(getResources().getIdentifier(event.getImageName(),
                "drawable",
                getPackageName()));

        TextView batchView = (TextView) findViewById(R.id.name);
        batchView.setText(batch.getName());

        TextView descriptionView = (TextView) findViewById(R.id.event_description);
        if (event.getDescription() == null || event.getDescription().isEmpty()) {
            descriptionView.setVisibility(View.GONE);
            findViewById(R.id.event_description_icon).setVisibility(View.GONE);
        } else {
            descriptionView.setText(event.getDescription());
        }

        ImageView imageView = (ImageView) findViewById(R.id.plant_type_icon);
        imageView.setImageResource(getResources().getIdentifier(
                batch.getImageName(),
                "drawable",
                getPackageName()));
        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(Helper.DATE_FORMAT.format(event.getCreatedDate()));
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(Helper.TIME_FORMAT.format(event.getCreatedDate()));

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
                DialogFragment dialog = new DeleteEventDialogFragment();
                dialog.show(getSupportFragmentManager(), "DeleteEvent");
                return true;
            case R.id.edit:
                Intent intent = new Intent(this, AddEventActivity.class);
                intent.putExtra(Helper.ARG_EVENT_ID, event.getId());
                intent.putExtra(Helper.ARG_BATCH_ID, batch.getId());
                intent.putExtra(Helper.ARG_IS_SOW_ACTIVITY, false);
                startActivity(intent);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DeleteEventDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle(R.string.delete_alert);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    batch.deleteEvent(getActivity(), event);
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
