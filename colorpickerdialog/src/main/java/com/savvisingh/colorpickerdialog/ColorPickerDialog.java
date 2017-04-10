/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.savvisingh.colorpickerdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import com.savvisingh.colorpickerdialog.ColorPickerSwatch.OnColorSelectedListener;

/**
 * A dialog which takes in as input an array of colors and creates a palette allowing the user to
 * select a specific color swatch, which invokes a listener.
 */
public class ColorPickerDialog extends DialogFragment implements OnColorSelectedListener {

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    public static final int SELECTION_SINGLE = 1;
    public static final int SELECTION_MULTI = 2;

    protected AlertDialog mAlertDialog;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_SELECTED_COLOR = "selected_color";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";
    protected static final String KEY_SELECTION_TYPE = "selection_type";



    protected int mTitleResId = R.string.color_picker_default_title;
    protected ArrayList<Integer> mColors = null;
    protected ArrayList<Integer> mSelectedColor;
    protected int mColumns;
    protected int mSize;

    private ColorPickerPalette mPalette;
    private ProgressBar mProgress;

    protected OnDialogButtonListener mListener;

    private int mSelectionType;

    public ColorPickerDialog() {
        // Empty constructor required for dialog fragments.
    }

    public static ColorPickerDialog newInstance(int mSelectionType, ArrayList<Integer> colors,
            int columns, int size) {

        ColorPickerDialog ret = new ColorPickerDialog();
        ret.initialize(mSelectionType, colors, columns, size);
        return ret;
    }

    public void initialize(int selectionType, ArrayList<Integer> colors, int columns, int size) {

        mSelectedColor = new ArrayList<>();
        setArguments(selectionType, columns, size);
        setColors(colors);

    }

    public void setArguments(int selectionType, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_SELECTION_TYPE, selectionType);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        setArguments(bundle);
    }

    public void setOnDialodButtonListener(OnDialogButtonListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSelectionType = getArguments().getInt(KEY_SELECTION_TYPE);
            mColumns = getArguments().getInt(KEY_COLUMNS);
            mSize = getArguments().getInt(KEY_SIZE);
        }

        if (savedInstanceState != null) {
            mColors = savedInstanceState.getIntegerArrayList(KEY_COLORS);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mPalette = (ColorPickerPalette) view.findViewById(R.id.color_picker);
        mPalette.init(mSize, mColumns, this);

        if (mColors != null) {
            showPaletteView();
        }

        if(mSelectionType == SELECTION_MULTI)
            mTitleResId = R.string.color_picker_default_title_multiselect;


        mAlertDialog = new AlertDialog.Builder(activity)
            .setTitle(mTitleResId)
            .setView(view)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener!=null)
                            mListener.onDonePressed(mSelectedColor);
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener!=null)
                            mListener.onDismiss();
                        dismiss();
                    }
                })
            .create();

        return mAlertDialog;
    }

    @Override
    public void onColorSelected(int color) {

        if(mSelectionType == SELECTION_MULTI){
            if(!mSelectedColor.contains(color))
                mSelectedColor.add(color);
            else mSelectedColor.remove((Object) color);
        }
        else if(mSelectionType == SELECTION_SINGLE){
            if(!mSelectedColor.contains(color)){
                mSelectedColor.clear();
                mSelectedColor.add(color);
            } else mSelectedColor.remove((Object) color);
        }


        refreshPalette();
    }

    public void showPaletteView() {
        if (mProgress != null && mPalette != null) {
            mProgress.setVisibility(View.GONE);
            refreshPalette();
            mPalette.setVisibility(View.VISIBLE);
        }
    }



    public void setColors(ArrayList<Integer> colors) {
        if (mColors != colors) {
            mColors = colors;
            refreshPalette();
        }
    }




    private void refreshPalette() {
        if (mPalette != null && mColors != null) {
            mPalette.drawPalette(mColors, mSelectedColor);
        }
    }

    public List<Integer> getColors() {
        return mColors;
    }

    public ArrayList<Integer> getSelectedColors() {
        return mSelectedColor;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(KEY_COLORS, mColors);
        outState.putSerializable(KEY_SELECTED_COLOR, mSelectedColor);
    }

    /**
     * Interface for a callback when a color square is selected.
     */
    public interface OnDialogButtonListener {

        /**
         * Called when a specific color square has been selected.
         */
        public void onDonePressed(ArrayList<Integer> mSelectedColors);

        public void onDismiss();

    }

}
