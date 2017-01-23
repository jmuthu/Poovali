package com.github.jmuthu.poovali.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.plant.Plant;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.EnumMap;

import static java.lang.Integer.parseInt;

public class AddPlantActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE_REQUEST = 1;
    private ImageView mPlantIcon;
    private Uri mSelectedImage;
    private Plant mPlant = null;
    private EditText nameView;

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
            EnumMap<Plant.GrowthStage, Integer> growthStageMap = mPlant.getGrowthStageMap();
            NumberFormat numberFormat = NumberFormat.getInstance();
            EditText seedling = (EditText) findViewById(R.id.seedling_days);
            seedling.setText(numberFormat.format(growthStageMap.get(Plant.GrowthStage.SEEDLING)));
            EditText flowering = (EditText) findViewById(R.id.flowering_days);
            flowering.setText(numberFormat.format(growthStageMap.get(Plant.GrowthStage.FLOWERING)));
            EditText fruiting = (EditText) findViewById(R.id.fruiting_days);
            fruiting.setText(numberFormat.format(growthStageMap.get(Plant.GrowthStage.FRUITING)));
            EditText ripening = (EditText) findViewById(R.id.ripening_days);
            ripening.setText(numberFormat.format(growthStageMap.get(Plant.GrowthStage.RIPENING)));
        }
        setImageIcon();
    }

    private void setImageIcon() {
        File file = null;
        if (mSelectedImage != null) {
            file = new File(mSelectedImage.getPath());
            if (file.exists()) {
                mPlantIcon.setImageURI(mSelectedImage);
            } else {
                file = null;
            }
        }
        if (file == null) {
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
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK) {
            String plantName = nameView.getText().toString().trim();
            FileOutputStream fos = null;

            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);

            File outFile = new File(path, plantName + ".png");
            try {
                path.mkdirs();
                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        imageReturnedIntent.getData());
                int px = Math.round(Helper.dipToPixels(this, 64));
                photo = Bitmap.createScaledBitmap(photo, px, px, false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 50, bytes);

                if (!outFile.exists()) {
                    outFile.createNewFile();
                }
                fos = new FileOutputStream(outFile);
                fos.write(bytes.toByteArray());

                bytes.close();
                photo.recycle();

            } catch (IOException e) {
                Log.e(imageReturnedIntent.getData().toString(), "Unable to read image file", e);
                MyExceptionHandler.alertAndCloseApp(this, null);
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    MyExceptionHandler.alertAndCloseApp(this, null);
                }
            }
            mSelectedImage = Uri.fromFile(outFile);
            mPlantIcon.setImageDrawable(null); // Forcing refresh after editing plant image
            mPlantIcon.setImageURI(mSelectedImage);
        }
    }

    public void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_plant_image)), SELECT_IMAGE_REQUEST);
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
