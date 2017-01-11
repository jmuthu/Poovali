package com.github.jmuthu.poovali.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.Plant;
import com.github.jmuthu.poovali.model.PlantRepository;

import java.util.UUID;

public class AddPlantActivity extends AppCompatActivity {
    static final int SELECT_IMAGE_REQUEST = 1;
    ImageView mPlantIcon;
    Uri mSelectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
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
        mPlantIcon = (ImageView) findViewById(R.id.plant_image);
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, SELECT_IMAGE_REQUEST);
    }

    public void savePlant(View v) {
        EditText name = (EditText) findViewById(R.id.plant_name);
        Plant plant = new Plant(
                UUID.randomUUID().toString(),
                name.getText().toString(),
                mSelectedImage,
                10, 10, 10, 10);
        PlantRepository.store(plant);
        finish();
    }

    public void cancelAddPlant(View v) {
        finish();
    }
}
