package com.example.sdaassign32022;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;


/*
 * A simple {@link Fragment} subclass.
 * @author Stephen Corri 2022
 */
public class OrderTshirt extends Fragment {


    private File imgFile;
    private Switch mSwitchSelf;
    private TextView mEditCollect;

    public OrderTshirt() {
        // Required empty public constructor
    }

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    //class wide variables
    private String mPhotoPath;
    private Spinner mSpinner;
    private EditText mCustomerName;
    private EditText meditDelivery;
    private ImageView mCameraImage;

    //static keys
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final String TAG = "OrderTshirt";
    private boolean isSwChecked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment get the root view.
        final View root = inflater.inflate(R.layout.fragment_order_tshirt, container, false);

        mCustomerName = root.findViewById(R.id.editCustomer);
        meditDelivery = root.findViewById(R.id.editDeliver);
        mEditCollect = root.findViewById(R.id.editCollect);

        mCameraImage = root.findViewById(R.id.imageView);
        mSwitchSelf = root.findViewById(R.id.swCollectSelf);
        Button mSendButton = root.findViewById(R.id.sendButton);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

        //set a listener on the the camera image
        mCameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(v);
            }
        });

        //set a listener to start the email intent.
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mCustomerName.getText().toString();
                String address = meditDelivery.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your name.", Toast.LENGTH_SHORT).show();
                } else if (!isSwChecked && address.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your delivery address.", Toast.LENGTH_SHORT).show();
                } else {
                    sendEmail();
                }
            }
        });

        mSwitchSelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSwChecked = isChecked;
                if (isChecked) {
                    mEditCollect.setVisibility(View.VISIBLE);
                    mSpinner.setVisibility(View.VISIBLE);
                    meditDelivery.setVisibility(View.GONE);
                } else {
                    mEditCollect.setVisibility(View.GONE);
                    mSpinner.setVisibility(View.GONE);
                    meditDelivery.setVisibility(View.VISIBLE);
                }
            }
        });


        //initialise spinner using the integer array
        mSpinner = root.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.ui_time_entries, R.layout.spinner_days);
        mSpinner.setAdapter(adapter);
        mSpinner.setEnabled(true);

        return root;
    }


    //Take a photo note the view is being passed so we can get context because it is a fragment.
    //update this to save the image so it can be sent via email
    private void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/sdaassign32022";
            File myDir = new File(root);
            myDir.mkdirs();
            Date date = new Date();
            String fname = "Image-" + date.getTime() + ".png";
            imgFile = new File(myDir, fname);
            System.out.println(imgFile.getAbsolutePath());
            if (imgFile.exists()) imgFile.delete();
            Log.i("LOAD", root + fname);
            try {
                FileOutputStream out = new FileOutputStream(imgFile);
                photo.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(requireActivity(), new String[]{imgFile.getPath()}, new String[]{"image/jpeg"}, null);
            mCameraImage.setImageBitmap(photo);

        }
    }

    /*
     * Returns the Email Body Message, update this to handle either collection or delivery
     */
    private String createOrderSummary() {
        String orderMessage = "";
        String deliveryInstruction = meditDelivery.getText().toString();
        String customerName = getString(R.string.customer_name) + " " + mCustomerName.getText().toString();

        orderMessage += customerName + "\n" + "\n" + getString(R.string.order_message_1);
        if (!isSwChecked) {
            orderMessage += "\n" + "Deliver my order to the following address: ";
            orderMessage += "\n" + deliveryInstruction;
        } else {
            orderMessage += "\n" + getString(R.string.order_message_collect) + mSpinner.getSelectedItem().toString() + "days";
        }
        orderMessage += "\n\n" + getString(R.string.order_message_end) + "\n" + mCustomerName.getText().toString();

        return orderMessage;
    }

    //Update me to send an email
    private void sendEmail() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"my-tshirt@sda.ie"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Order Request");
        if (imgFile != null) {
            Uri imageUri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.example.sdaassign32022.provider",
                    imgFile);
            i.putExtra(Intent.EXTRA_STREAM, imageUri);
        }
        i.putExtra(Intent.EXTRA_TEXT, createOrderSummary());
        i.setType("image/png");
        startActivity(Intent.createChooser(i, "Sharing Request"));
    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, requestCode);
        } else {
            //Toast.makeText(requireActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
