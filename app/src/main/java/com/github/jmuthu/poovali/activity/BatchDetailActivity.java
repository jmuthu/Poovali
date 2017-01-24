package com.github.jmuthu.poovali.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.model.event.EventRepository;
import com.github.jmuthu.poovali.model.plant.PlantBatch;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.utility.Helper;

import java.text.DateFormat;
import java.text.MessageFormat;

import static com.github.jmuthu.poovali.utility.Helper.DATE_FORMAT;

public class BatchDetailActivity extends AppCompatActivity {
    private PlantBatch mPlantBatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_detail);
        int batchId = getIntent().getIntExtra(Helper.ARG_BATCH_ID, -1);
        mPlantBatch = PlantBatchRepository.find(batchId);

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
                        collapsingToolbarLayout.setTitle(mPlantBatch.getName());
                    } else {
                        collapsingToolbarLayout.setTitle(mPlantBatch.getPlant().getName());
                    }
                }
            });
        }
        ImageView plantIcon = (ImageView) findViewById(R.id.plant_type_icon);
        Helper.setImageSrc(plantIcon, mPlantBatch);

        setEditableContent();

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(Helper.ARG_BATCH_ID, batchId);
            EventListFragment fragment = new EventListFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_list_container, fragment)
                    .commit();
        }
    }

    private void setEditableContent() {
        TextView batchStatusView = (TextView) findViewById(R.id.batch_status);
        TextView batchDescriptionView = (TextView) findViewById(R.id.description);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.batch_status_progress);
        TextView createdDateView = (TextView) findViewById(R.id.created_date);
        TextView durationView = (TextView) findViewById(R.id.duration);

        createdDateView.setText(getString(R.string.created_on) + " " +
                DATE_FORMAT.format(mPlantBatch.getCreatedDate()));
        batchStatusView.setText(Helper.getLocalizedString(R.array.plant_growth_stages, mPlantBatch.getStage().getValue()));

        int duration = mPlantBatch.getDurationInDays();
        String fmt = getResources().getText(R.string.days_due).toString();
        durationView.setText(MessageFormat.format(fmt, duration));


        if (mPlantBatch.getDescription() == null || mPlantBatch.getDescription().isEmpty()) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
            batchDescriptionView.setText(getString(R.string.created_on) + " " +
                    df.format(mPlantBatch.getCreatedDate()));
        } else {
            batchDescriptionView.setText(mPlantBatch.getDescription());
        }
        progressBar.setProgress(mPlantBatch.getProgress());
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
        if (mPlantBatch.getEvents().isEmpty()) {
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
        if (mPlantBatch.getEvents().isEmpty()) {
            eventLabel.setVisibility(View.GONE);
        } else {
            eventLabel.setVisibility(View.VISIBLE);
            eventLabel.setText(getString(R.string.events) + " ("
                    + (mPlantBatch.getEvents().size()) + ")");
        }
        invalidateOptionsMenu();
        setEditableContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_event:
                Intent intent = new Intent(this, AddEventActivity.class);
                intent.putExtra(Helper.ARG_BATCH_ID, mPlantBatch.getId());
                intent.putExtra(Helper.ARG_PLANT_ID, mPlantBatch.getPlant().getId());
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit:
                intent = new Intent(this, AddPlantBatchActivity.class);
                intent.putExtra(Helper.ARG_BATCH_ID, mPlantBatch.getId());
                startActivity(intent);
                return true;
            case R.id.delete:
                DeleteBatchDialogFragment dialog = new DeleteBatchDialogFragment();
                dialog.setBatch(mPlantBatch);
                dialog.show(getSupportFragmentManager(), "DeleteEvent");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DeleteBatchDialogFragment extends DialogFragment {
        PlantBatch mPlantBatch;

        public void setBatch(PlantBatch plantBatch) {
            mPlantBatch = plantBatch;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), R.style.AlertDialogTheme);
            builder.setTitle(R.string.delete_batch_alert);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for (Event event : mPlantBatch.getEvents()) {
                        EventRepository.delete(event);
                    }
                    mPlantBatch.getPlant().deleteBatch(mPlantBatch);
                    PlantBatchRepository.delete(mPlantBatch);
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
