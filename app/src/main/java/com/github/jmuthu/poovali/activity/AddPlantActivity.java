package com.github.jmuthu.poovali.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.plant.Plant;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
import com.github.jmuthu.poovali.utility.Helper;

import java.util.UUID;

import static java.lang.Integer.parseInt;

public class AddPlantActivity extends AppCompatActivity {
    static final int SELECT_IMAGE_REQUEST = 1;
    ImageView mPlantIcon;
    Uri mSelectedImage;
    Plant mPlant = null;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        Bundle extras = getIntent().getExtras();
        String plantId = null;
        if (extras != null) {
            plantId = extras.getString(Helper.ARG_PLANT_ID);
        }
        mPlantIcon = (ImageView) findViewById(R.id.plant_image);
        name = (EditText) findViewById(R.id.plant_name);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setImageIcon();
                }
            }
        });
        if (plantId != null) {
            mPlant = PlantRepository.find(plantId);
            name.setText(mPlant.getName());
            mSelectedImage = mPlant.getImageUri();
            EditText seedling = (EditText) findViewById(R.id.seedling_days);
            seedling.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.SEEDLING).toString());
            EditText flowering = (EditText) findViewById(R.id.flowering_days);
            flowering.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.FLOWERING).toString());
            EditText fruiting = (EditText) findViewById(R.id.fruiting_days);
            fruiting.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.FRUITING).toString());
            EditText ripening = (EditText) findViewById(R.id.ripening_days);
            ripening.setText(mPlant.getGrowthStageMap().get(Plant.GrowthStage.RIPENING).toString());
            setImageIcon();
        }
    }

    void setImageIcon() {
        if (mSelectedImage != null) {
            mPlantIcon.setImageURI(mSelectedImage);
        } else {
            int resId = getResources().getIdentifier(
                    name.getText().toString().toLowerCase(),
                    "drawable",
                    getPackageName());
            if (resId > 0) {
                mPlantIcon.setImageResource(resId);
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
            saveFailedAlert(getResources().getString(R.string.growth_stage_invalid_days));
            return -1;
        }
        return parseInt(editText.getText().toString());
    }

    void saveFailedAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setMessage(message);
        builder.setTitle(R.string.save_failed);
        builder.setPositiveButton(android.R.string.ok, null);

        builder.show();
    }

    public void savePlant(View v) {
        name = (EditText) findViewById(R.id.plant_name);
        if (name.getText() == null || name.getText().toString().isEmpty()) {
            saveFailedAlert(getResources().getString(R.string.invalid_name));
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
            mPlant.setName(name.getText().toString());
            mPlant.setImageUri(mSelectedImage);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.SEEDLING, seedlingDays);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.FLOWERING, floweringDays);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.FRUITING, fruitingDays);
            mPlant.getGrowthStageMap().put(Plant.GrowthStage.RIPENING, ripeningDays);
        } else {
            mPlant = new Plant(
                    UUID.randomUUID().toString(),
                    name.getText().toString(),
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
