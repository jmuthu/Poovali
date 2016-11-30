package com.github.jmuthu.poovali.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.fragment.EventListFragment;
import com.github.jmuthu.poovali.model.BatchContent;
import com.github.jmuthu.poovali.utility.Helper;

public class BatchDetailActivity extends AppCompatActivity {
    BatchContent.Batch mBatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String batchId = getIntent().getStringExtra(Helper.ARG_BATCH_ID);
        mBatch = BatchContent.getBatch(batchId);

        ImageView plantIcon = (ImageView) findViewById(R.id.plant_type_icon);
        plantIcon.setImageResource(getResources().getIdentifier(mBatch.getImageName(),
                "drawable",
                getPackageName()));

        TextView nameView = (TextView) findViewById(R.id.name);
        TextView batchStatusView = (TextView) findViewById(R.id.batch_status);
        TextView batchDescriptionView = (TextView) findViewById(R.id.description);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.batch_status_progress);

        nameView.setText(mBatch.getName());
        batchStatusView.setText(mBatch.getStage().toString());
        batchDescriptionView.setText(mBatch.getDescription());
        progressBar.setProgress(mBatch.getProgress());

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
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
        /*    case R.id.delete:
                DialogFragment dialog = new EventDetailActivity.DeleteEventDialogFragment();
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
        */
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
