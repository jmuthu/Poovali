package com.github.jmuthu.poovali.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.plant.Plant;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
import com.github.jmuthu.poovali.utility.Helper;

import static java.lang.Integer.parseInt;

public class AddPlantActivity extends AppCompatActivity {
    static final int SELECT_IMAGE_REQUEST = 1;
    ImageView mPlantIcon;
    Uri mSelectedImage;
    Plant mPlant = null;
    EditText nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        Bundle extras = getIntent().getExtras();
        int plantId = -1;
        if (extras != null) {
            plantId = extras.getInt(Helper.ARG_PLANT_ID, -1);
        }
        mPlantIcon = (ImageView) findViewById(R.id.plant_image);
        nameView = (EditText) findViewById(R.id.plant_name);
        nameView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setImageIcon();
            }
        });

        if (plantId != -1) {
            mPlant = PlantRepository.find(plantId);
            nameView.setText(mPlant.getName());
            mSelectedImage = mPlant.getImageUri();
            EditText seedling = (EditText) findViewById(R.id.seedling_days);
            seedling.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.SEEDLING).toString());
            EditText flowering = (EditText) findViewById(R.id.flowering_days);
            flowering.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.FLOWERING).toString());
            EditText fruiting = (EditText) findViewById(R.id.fruiting_days);
            fruiting.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.FRUITING).toString());
            EditText ripening = (EditText) findViewById(R.id.ripening_days);
            ripening.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.RIPENING).toString());
        }
        setImageIcon();
    }

    void setImageIcon() {
        if (mSelectedImage != null) {
            mPlantIcon.setImageURI(mSelectedImage);
        } else {
            int resId = getResources().getIdentifier(
                    Helper.getImageFileName(nameView.getText().toString().toLowerCase().trim()),
                    "drawable",
                    getPackageName());
            if (resId > 0) {
                mPlantIcon.setImageResource(resId);
            } else {
                mPlantIcon.setImageResource(R.drawable.ic_add_a_photo_black_48dp);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    mSelectedImage = imageReturnedIntent.getData();
                    mPlantIcon.setImageURI(mSelectedImage);
                }
                break;
        }
    }

    public void selectImage(View v) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, SELECT_IMAGE_REQUEST);
    }

    Integer parseText(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().isEmpty()
                || parseInt(editText.getText().toString()) == 0) {
            Helper.alertSaveFailure(this, R.string.growth_stage_invalid_days);
            return -1;
        }
        return parseInt(editText.getText().toString());
    }

    public void savePlant(View v) {
        if (nameView.getText() == null || nameView.getText().toString().trim().isEmpty()) {
            Helper.alertSaveFailure(this, R.string.invalid_name);
            return;
        }
        String plantName = nameView.getText().toString().trim();
        Plant plant = PlantRepository.findByName(plantName);

        if (plant != null && !plant.sameIdentityAs(mPlant)) {
            Helper.alertSaveFailure(this, R.string.duplicate_plant);
            return;
        }
        EditText seedling = (EditText) findViewById(R.id.seedling_days);
        EditText flowering = (EditText) findViewById(R.id.flowering_days);
        EditText fruiting = (EditText) findViewById(R.id.fruiting_days);
        EditText ripening = (EditText) findViewById(R.id.ripening_days);

        Integer seedlingDays = parseText(seedling);
        if (seedlingDays == -1) return;

        Integer floweringDays = parseText(flowering);
        if (floweringDays == -1) return;

        Integer fruitingDays = parseText(fruiting);
        if (fruitingDays == -1) return;

        Integer ripeningDays = parseText(ripening);
        if (ripeningDays == -1) return;

        if (mPlant != null) {
            mPlant.setName(plantName);
            mPlant.setImageUri(mSelectedImage);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.SEEDLING, seedlingDays);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.FLOWERING, floweringDays);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.FRUITING, fruitingDays);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.RIPENING, ripeningDays);
        } else {
            mPlant = new Plant(
                    PlantRepository.nextPlantId(),
                    plantName,
                    mSelectedImage,
                    seedlingDays,
                    floweringDays,
                    fruitingDays,
                    ripeningDays);
        }

        PlantRepository.store(mPlant);
        finish();
    }

    public void cancelAddPlant(View v) {
        finish();
    }
}
