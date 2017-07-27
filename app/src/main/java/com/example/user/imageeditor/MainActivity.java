package com.example.user.imageeditor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.adobe.creativesdk.aviary.internal.filters.ToolLoaderFactory;
import com.adobe.creativesdk.aviary.internal.headless.utils.MegaPixels;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static ImageButton camera,gallery;
    private static ImageView tempImage;
    private final int SELECT_PICTURE = 123;
    Thread cam,gal;

    /*class sync implements Runnable{
        @Override
        public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        syncSDCard();
                    }
                });

        }

        public void syncSDCard() {
            imageFiles = getImages(Environment.getExternalStorageDirectory());
            Collections.sort(imageFiles, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    if(lhs.lastModified() > rhs.lastModified()) {
                        return -1;
                    }
                    else if(lhs.lastModified() < rhs.lastModified()) {
                        return 1;
                    }
                    else
                        return 0;
                }
            });

            files = new String[imageFiles.size()];
            for(int i = 0; i < imageFiles.size(); i++) {
                files[i] = imageFiles.get(i).getName().replace(".jpg", "");
            }
            ImageAdapter imageAdapter = new ImageAdapter(MainActivity.this, imageFiles);
            imageList.setAdapter(imageAdapter);
        }
        public ArrayList<File> getImages(File root) {
            ArrayList<File> al = new ArrayList<File>();
            File[] files = root.listFiles();
            for(File sf : files) {
                if(sf.isDirectory() && !sf.isHidden()) {
                    al.addAll(getImages(sf));
                }
                else {
                    if(sf.getName().endsWith(".jpg"))
                        al.add(sf);
                }
            }

            return al;
        }
    };*/

    class getCameraImage implements Runnable{
        @Override
        public void run() {
            final Calendar cal = Calendar.getInstance();
            cameraFileName = ""+cal.get(Calendar.DAY_OF_MONTH) + "-"
                    + (cal.get(Calendar.MONTH) + 1) + "-"
                    + cal.get(Calendar.YEAR)+ "-"
                    + cal.get(Calendar.HOUR)+ "-"
                    + cal.get(Calendar.MINUTE)+ "-"
                    + cal.get(Calendar.SECOND)+".jpg";
            String tempUri = Environment.getExternalStorageDirectory().getPath() + "/PhotoX/temp-" + cameraFileName;
            cameraUri = Uri.fromFile(new File(tempUri));
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            //getCameraImage.getThreadGroup().interrupt();

        }
    }
    class getGalleryImage implements Runnable{
        @Override
        public void run() {
            final Calendar cal = Calendar.getInstance();
            cameraFileName = ""+cal.get(Calendar.DAY_OF_MONTH) + "-"
                    + (cal.get(Calendar.MONTH) + 1) + "-"
                    + cal.get(Calendar.YEAR)+ "-"
                    + cal.get(Calendar.HOUR)+ "-"
                    + cal.get(Calendar.MINUTE)+ "-"
                    + cal.get(Calendar.SECOND)+".jpg";
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        }
    }

    String cameraFileName;
    Uri cameraUri;

    private final Integer REQUEST_IMAGE_CAPTURE = 2, REQUEST_SELECT_IMAGE = 3;
    //private static GridView imageList;
    //ArrayList<File> imageFiles;
    //String[] files;
    private ToolLoaderFactory.Tools[] mTools = {ToolLoaderFactory.Tools.EFFECTS
            , ToolLoaderFactory.Tools.CROP
            , ToolLoaderFactory.Tools.ADJUST
            , ToolLoaderFactory.Tools.COLOR
            , ToolLoaderFactory.Tools.LIGHTING
            , ToolLoaderFactory.Tools.ORIENTATION
            , ToolLoaderFactory.Tools.TEXT
            , ToolLoaderFactory.Tools.SHARPNESS};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = (ImageButton)findViewById(R.id.btn_cam);
        gallery = (ImageButton)findViewById(R.id.btn_gal);
        tempImage = (ImageView)findViewById(R.id.imageView);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Toast.makeText(MainActivity.this, "No camera found.", Toast.LENGTH_SHORT).show();
                }
                else {
                    cam = null;
                    cam = new Thread(new getCameraImage());
                    cam.start();
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gal = null;
                gal = new Thread(new getGalleryImage());
                gal.start();
            }
        });


/*        ActionBar ac = getSupportActionBar();
        ac.setIcon(R.drawable.photox_logo);
        ac.setDisplayUseLogoEnabled(true);
        //imageList = (GridView)findViewById(R.id.image_gridView);

        //View fab = (View)findViewById(R.id.fab);
        /*if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                        Toast.makeText(MainActivity.this, "No camera found.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        cam = null;
                        cam = new Thread(new getCameraImage());
                        cam.start();
                    }
                }
            });
        }*/

        /*s = new Thread(new sync());
        s.start();*/
        //syncSDCard();
        /*imageFiles = getImages(Environment.getExternalStorageDirectory());

        files = new String[imageFiles.size()];
        for(int i = 0; i < imageFiles.size(); i++) {
            files[i] = imageFiles.get(i).getName().replace(".jpg", "");
        }
        imageList.setAdapter(new ImageAdapter(this,imageFiles));*/

        /*b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "Download/images.jpg");
                Intent intent = new AdobeImageIntent.Builder(getApplicationContext()).setData(uri).withToolList(mTools).build();
                startActivityForResult(intent, 1);
            }
        });*/

        /*imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse(imageFiles.get(position).getPath());
                //Uri uriOutput = Uri.parse(Environment.getExternalStorageDirectory() + "/PhotoX/"+imageFiles.get(position).getName());
                //Toast.makeText(MainActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                File photoFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "PhotoX");
                if(!photoFolder.exists()) {
                    photoFolder.mkdir();
                }
                Intent intent = new AdobeImageIntent.Builder(getApplicationContext())
                        .setData(uri)
                        .withOutput(Uri.parse("file://" + Environment.getExternalStorageDirectory()
                                + "/PhotoX/"+imageFiles.get(position).getName()))
                        .withToolList(mTools)
                        .withOutputFormat(Bitmap.CompressFormat.JPEG)
                        .withOutputSize(MegaPixels.Mp5)
                        .withOutputQuality(90)
                        .build();
                startActivityForResult(intent, 1);
            }
        });
    }
    public ArrayList<File> getImages2(File root) {
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File sf : files) {
            if(sf.isDirectory() && !sf.isHidden()) {
                al.addAll(getImages2(sf));
            }
            else {
                if(sf.getName().endsWith(".jpg"))
                        al.add(sf);
            }
        }

        return al;
    }
    public void syncSDCard2() {
        imageFiles = getImages2(Environment.getExternalStorageDirectory());

        Collections.sort(imageFiles, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if(lhs.lastModified() > rhs.lastModified()) {
                    return -1;
                }
                else if(lhs.lastModified() < rhs.lastModified()) {
                    return 1;
                }
                else
                    return 0;
            }
        });

        files = new String[imageFiles.size()];
        for(int i = 0; i < imageFiles.size(); i++) {
            files[i] = imageFiles.get(i).getName().replace(".jpg", "");
        }
        imageList.setAdapter(new ImageAdapter(this,imageFiles));
    }

/*    public void bindGridView() {
        ImageAdapter imageAdapter = new ImageAdapter(MainActivity.this, imageFiles);
        imageList.setAdapter(imageAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_id:
                if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Toast.makeText(MainActivity.this, "No camera found.", Toast.LENGTH_SHORT).show();
                }
                else {
                    cam = null;
                    cam = new Thread(new getCameraImage());
                    cam.start();
                    /*final Calendar cal = Calendar.getInstance();
                    cameraFileName = ""+cal.get(Calendar.DAY_OF_MONTH) + "-"
                            + (cal.get(Calendar.MONTH) + 1) + "-"
                            + cal.get(Calendar.YEAR)+ "-"
                            + cal.get(Calendar.HOUR)+ "-"
                            + cal.get(Calendar.MINUTE)+ "-"
                            + cal.get(Calendar.SECOND)+".jpg";
                    String tempUri = Environment.getExternalStorageDirectory().getPath() + "/PhotoX/temp-" + cameraFileName;
                    cameraUri = Uri.fromFile(new File(tempUri));
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case R.id.sync_id:
                Toast.makeText(MainActivity.this, "Syncing SD Card", Toast.LENGTH_SHORT).show();
                //bindGridView();
                //syncSDCard2();
                /*s = null;
                s = new Thread(new sync());
                s.start();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent intent = new AdobeImageIntent.Builder(getApplicationContext())
                    .setData(cameraUri)
                    .withOutput(Uri.parse("file://" + Environment.getExternalStorageDirectory()
                            + "/PhotoX/"+ cameraFileName))
                    .withToolList(mTools)
                    .withOutputFormat(Bitmap.CompressFormat.JPEG)
                    .withOutputSize(MegaPixels.Mp5)
                    .withOutputQuality(100)
                    .build();
            startActivityForResult(intent, 1);
        }*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent intent = new AdobeImageIntent.Builder(getApplicationContext())
                    .setData(cameraUri)
                    .withOutput(Uri.parse("file://" + Environment.getExternalStorageDirectory()
                            + "/PhotoX/" + cameraFileName))
                    .withToolList(mTools)
                    .withOutputFormat(Bitmap.CompressFormat.JPEG)
                    .withOutputSize(MegaPixels.Mp5)
                    .withOutputQuality(100)
                    .build();
            startActivityForResult(intent, 1);
        }
        else if(requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            Intent intent = new AdobeImageIntent.Builder(getApplicationContext())
                    .setData(Uri.parse(path))
                    .withOutput(Uri.parse("file://" + Environment.getExternalStorageDirectory()
                            + "/PhotoX/" + cameraFileName))
                    .withToolList(mTools)
                    .withOutputFormat(Bitmap.CompressFormat.JPEG)
                    .withOutputSize(MegaPixels.Mp5)
                    .withOutputQuality(100)
                    .build();
            startActivityForResult(intent, 1);
        }
        else if(requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(Environment.getExternalStorageDirectory() + "/PhotoX/" + cameraFileName);
            tempImage.setImageURI(imageUri);
        }
    }
}
