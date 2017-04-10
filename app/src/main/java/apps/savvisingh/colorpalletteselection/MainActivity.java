package apps.savvisingh.colorpalletteselection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Swatch;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.savvisingh.colorpickerdialog.ColorPickerDialog;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ImageUtils.ImageAttachmentListener, Palette.PaletteAsyncListener{



    ImageView iv_attachment;

    //For Image Attachment

    private Bitmap bitmap;
    private String file_name;

    ImageUtils imageutils;

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Swatch> swatches;

    private adapter mAdapter;
    private HashMap<String, Integer> materialColors;
    private HashMap<String, Integer> closestColors;
    private ArrayList<Integer> closestColorsList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        imageutils =new ImageUtils(this);

        iv_attachment=(ImageView)findViewById(R.id.imageView);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        swatches = new ArrayList<>();
        closestColors = new HashMap<>();
        closestColorsList = new ArrayList<>();
        mAdapter = new adapter(closestColorsList);

        recyclerView.setAdapter(mAdapter);

        iv_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        int[] colorcodes = getResources().getIntArray(R.array.materialColors);
        String[] colorNames = getResources().getStringArray(R.array.MaterialColorNames);

        materialColors = new HashMap<>();

        for (int i = 0; i< colorcodes.length; i++){
            materialColors.put(colorNames[i], colorcodes[i]);

        }

        Log.d("Color Code", colorcodes[0] + " ");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap=file;
        this.file_name=filename;
        iv_attachment.setImageBitmap(file);

        String path =  Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file,filename,path,false);

        if(bitmap!=null && !bitmap.isRecycled()){
            Palette.from(bitmap).generate(this);
        }



    }

    public ArrayList<Integer> mSelectedColors;

    @Override
    public void onGenerated(Palette palette) {

        swatches.clear();
        swatches.addAll(palette.getSwatches());

        closestColors.clear();
        closestColorsList.clear();

        for (Swatch swatch: swatches){
            String closestColor = getNearestColor(swatch.getRgb());
            if(!closestColors.containsKey(closestColor)){
                closestColors.put(closestColor, materialColors.get(closestColor));
                closestColorsList.add(materialColors.get(closestColor));
            }
        }

        mAdapter.notifyDataSetChanged();

        mSelectedColors = new ArrayList<>();

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                ColorPickerDialog.SELECTION_MULTI,
                closestColorsList,
                3, // Number of columns
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
            @Override
            public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                Log.d("selected colors", mSelectedColors.size() + " ");
            }

            @Override
            public void onDismiss() {

            }
        });

        dialog.show(getFragmentManager(), "some_tag");

    }


    String getNearestColor(int c){

        String nearestColor = null;
        double nearestDistance = Double.MAX_VALUE;
        for(String key : materialColors.keySet()){
            int color = materialColors.get(key);
            if(nearestDistance > colorDistance(c, color)){
                nearestDistance = colorDistance(c, color);
                nearestColor = key;
            }
        }

        return nearestColor;
    }

    double colorDistance(int c1, int c2)
    {
        int red1 = Color.red(c1);
        int red2 = Color.red(c2);
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = Color.green(c1) - Color.green(c2);
        int b = Color.blue(c1) - Color.blue(c2);
        return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }


}
