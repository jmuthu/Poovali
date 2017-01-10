package com.github.jmuthu.poovali.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
        plantIcon.setImageResource(getResources().getIdentifier(mBatch.getImageName(),
                "drawable",
                getPackageName()));

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
        menu.findItem(R.id.add_plant).setVisible(false);
        menu.findItem(R.id.add_batch).setVisible(false);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}