//finished. 2015/01/18
package com.example.croppersample;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.edmodo.cropper.CropImageView;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class MainActivity extends Activity {

	// Static final constants
	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
	private static final int ROTATE_NINETY_DEGREES = 90;
	private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
	private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
	private static final int ON_TOUCH = 1;

	// Instance variables
	private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
	private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

	// choose pic
	private static final String TAG = "FileChooserExampleActivity";
	private static final int REQUEST_CODE = 6384; // onActivityResult request
													// code

	Bitmap croppedImage;

	private String picPath;
	CropImageView cropImageView;

	// Saves the state upon rotating the screen/restarting the activity
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
		bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
	}

	// Restores the state upon rotating the screen/restarting the activity
	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
		mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
	}

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// Sets fonts for all
		Typeface mFont = Typeface.createFromAsset(getAssets(),
				"Roboto-Thin.ttf");
		ViewGroup root = (ViewGroup) findViewById(R.id.mylayout);
		setFont(root, mFont);

		// Initialize components of the app
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);
		final SeekBar aspectRatioXSeek = (SeekBar) findViewById(R.id.aspectRatioXSeek);
		final SeekBar aspectRatioYSeek = (SeekBar) findViewById(R.id.aspectRatioYSeek);
		final ToggleButton fixedAspectRatioToggle = (ToggleButton) findViewById(R.id.fixedAspectRatioToggle);


		// Sets sliders to be disabled until fixedAspectRatio is set
		aspectRatioXSeek.setEnabled(false);
		aspectRatioYSeek.setEnabled(false);


		// Sets the rotate button
		final Button rotateButton = (Button) findViewById(R.id.Button_rotate);
		rotateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
			}
		});

		// Sets fixedAspectRatio
		fixedAspectRatioToggle
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						cropImageView.setFixedAspectRatio(isChecked);
						if (isChecked) {
							aspectRatioXSeek.setEnabled(true);
							aspectRatioYSeek.setEnabled(true);
						} else {
							aspectRatioXSeek.setEnabled(false);
							aspectRatioYSeek.setEnabled(false);
						}
					}
				});

		// Sets initial aspect ratio to 10/10, for demonstration purposes
		cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES,
				DEFAULT_ASPECT_RATIO_VALUES);

		// Sets aspectRatioX
		final TextView aspectRatioX = (TextView) findViewById(R.id.aspectRatioX);

		aspectRatioXSeek
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar aspectRatioXSeek,
							int progress, boolean fromUser) {
						try {
							mAspectRatioX = progress;
							cropImageView.setAspectRatio(progress,
									mAspectRatioY);
							aspectRatioX.setText(" " + progress);
						} catch (IllegalArgumentException e) {
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});

		// Sets aspectRatioY
		final TextView aspectRatioY = (TextView) findViewById(R.id.aspectRatioY);

		aspectRatioYSeek
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar aspectRatioYSeek,
							int progress, boolean fromUser) {
						try {
							mAspectRatioY = progress;
							cropImageView.setAspectRatio(mAspectRatioX,
									progress);
							aspectRatioY.setText(" " + progress);
						} catch (IllegalArgumentException e) {
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});

		final Button cropButton = (Button) findViewById(R.id.Button_crop);
		cropButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				croppedImage = cropImageView.getCroppedImage();
				// ImageView croppedImageView = (ImageView)
				// findViewById(R.id.croppedImageView);
				// croppedImageView.setImageBitmap(croppedImage);
				cropImageView.setImageBitmap(croppedImage);
			}
		});

		// Sets the save button
		final Button saveButton = (Button) findViewById(R.id.Button_save);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (picPath != null) {
					try {
						//Bitmap savePic = Utils.decodeBase64(base64);
						croppedImage = cropImageView.getCroppedImage();
						
						// Create an image file name
						String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss")
								.format(new Date());
						String imageFileName = "Screenshots/Edited_" + timeStamp + "_";
			            File image = File.createTempFile(
			                    imageFileName,  /* prefix */
			                    ".png",         /* suffix */
			                    Environment.getExternalStoragePublicDirectory(
			                            Environment.DIRECTORY_PICTURES)      /* directory */
			                );

			            // output image to file
			            FileOutputStream fos = new FileOutputStream(image);
			            croppedImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			            fos.close();
			            
			            // display saved message
						Toast.makeText(getApplicationContext(), "Saved in the ScreenShot folder: "+image,
								Toast.LENGTH_LONG).show();

						//invoke media scanner
						galleryAddPic(image.getAbsolutePath());

					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Pic not saved.");
					}

				}
			}
		});

		// Sets the reset button
		/*
		 * final Button resetButton = (Button) findViewById(R.id.Button_reset);
		 * resetButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // XX do something } });
		 */
	}

	/*
	 * Sets the font on all TextViews in the ViewGroup. Searches recursively for
	 * all inner ViewGroups as well. Just add a check for any other views you
	 * want to set as well (EditText, etc.)
	 */
	public void setFont(ViewGroup group, Typeface font) {
		int count = group.getChildCount();
		View v;
		for (int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if (v instanceof TextView || v instanceof EditText
					|| v instanceof Button) {
				((TextView) v).setTypeface(font);
			} else if (v instanceof ViewGroup)
				setFont((ViewGroup) v, font);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void choose_pic(View view) {
		// Use the GET_CONTENT intent from the utility class
		Intent target = FileUtils.createGetContentIntent();
		// Create the chooser Intent
		Intent intent = Intent.createChooser(target,
				getString(R.string.choose_pic));
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			// If the file selection was successful
			if (resultCode == RESULT_OK) {
				if (data != null) {
					// Get the URI of the selected file
					final Uri uri = data.getData();
					Log.i(TAG, "Uri = " + uri.toString());
					try {
						// Get the file path from the URI
						picPath = FileUtils.getPath(this, uri);
						 Toast.makeText(this, "File Selected: " + picPath,
						 Toast.LENGTH_LONG).show();
						 System.out.println("File Selected: " + picPath);
						cropImageView.setImageBitmap(BitmapFactory
								.decodeFile(picPath));
					} catch (Exception e) {
						Log.e("FileSelectorTestActivity", "File select error",
								e);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 * invoke the system's media scanner to add your photo 
	 * to the Media Provider's database, making it available 
	 * in the Android Gallery application and to other apps
	 */
	private void galleryAddPic(String mCurrentPhotoPath) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
}
