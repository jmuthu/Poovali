package com.github.jmuthu.poovali.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.fragment.EventListFragment;
import com.github.jmuthu.poovali.model.Batch;
import com.github.jmuthu.poovali.model.BatchRepository;
import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.model.event.EventRepository;
import com.github.jmuthu.poovali.utility.Helper;

import java.text.DateFormat;

import static com.github.jmuthu.poovali.utility.Helper.DATE_FORMAT;

public class BatchDetailActivity extends AppCompatActivity {
    Batch mBatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_detail);
        String batchId = getIntent().getStringExtra(Helper.ARG_BATCH_ID);
        mBatch = BatchRepository.find(batchId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.toolbar_layout);
        if (collapsingToolbarLayout != null) {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle(mBatch.getName());
                    } else {
                        collapsingToolbarLayout.setTitle(mBatch.getPlant().getName());
                    }
                }
            });
        }
        ImageView plantIcon = (ImageView) findViewById(R.id.plant_type_icon);
        if (mBatch.getImageUri() != null) {
            plantIcon.setImageURI(mBatch.getImageUri());
        } else {
            plantIcon.setImageResource(getResources().getIdentifier(mBatch.getImageName(),
                    "drawable",
                    getPackageName()));
        }

        TextView batchStatusView = (TextView) findViewById(R.id.batch_status);
        TextView batchDescriptionView = (TextView) findViewById(R.id.description);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.batch_status_progress);
        TextView createdDateView = (TextView) findViewById(R.id.created_date);
        TextView durationView = (TextView) findViewById(R.id.duration);

        createdDateView.setText("created on " + DATE_FORMAT.format(mBatch.getCreatedDate()));
        batchStatusView.setText(mBatch.getStage().toString());

        int duration = mBatch.getDurationInDays();
        if (duration > 1) {
            durationView.setText(mBatch.getDurationInDays() + " days old");
        } else if (duration == 1) {
            durationView.setText(mBatch.getDurationInDays() + " day old");
        } else if (duration == 0) {
            durationView.setText("planted today");
        }

        if (mBatch.getDescription() == null || mBatch.getDescription().isEmpty()) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
            batchDescriptionView.setText("Batch created on " + df.format(mBatch.getCreatedDate()));
        } else {
            batchDescriptionView.setText(mBatch.getDescription());
        }
        progressBar.setProgress(mBatch.getProgress());

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(Helper.ARG_BATCH_ID, batchId);
            EventListFragment fragment = new EventListFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_list_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_plant).setVisible(false);
        menu.findItem(R.id.add_batch).setVisible(false);
        if (mBatch.getEvents().size() == 1) {
            menu.findItem(R.id.delete).setVisible(true);
        } else {
            menu.findItem(R.id.delete).setVisible(false);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView eventLabel = (TextView) findViewById(R.id.event_label);
        if (mBatch.getEvents().size() > 1) {
            eventLabel.setVisibility(View.VISIBLE);
            eventLabel.setText(getString(R.string.events) + " ("
                    + (mBatch.getEvents().size() - 1) + ")");
        } else {
            eventLabel.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_event:
                Intent intent = new Intent(this, AddEventActivity.class);
                intent.putExtra(Helper.ARG_IS_SOW_ACTIVITY, false);
                intent.putExtra(Helper.ARG_BATCH_ID, mBatch.getId());
                intent.putExtra(Helper.ARG_PLANT_ID, mBatch.getPlant().getId());
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete:
                DeleteBatchDialogFragment dialog = new DeleteBatchDialogFragment();
                dialog.setBatch(mBatch);
                dialog.show(getSupportFragmentManager(), "DeleteEvent");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DeleteBatchDialogFragment extends DialogFragment {
        Batch mBatch;

        public void setBatch(Batch batch) {
            mBatch = batch;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle(R.string.delete_batch_alert);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for (Event event : mBatch.getEvents()) {
                        EventRepository.delete(event);
                    }
                    mBatch.getPlant().deleteBatch(mBatch);
                    BatchRepository.delete(mBatch);
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
