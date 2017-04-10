Maven:
<dependency>
  <groupId>ColorPalletteSelection</groupId>
  <artifactId>colorpickerdialog</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>

Gradle:
compile 'ColorPalletteSelection:colorpickerdialog:1.0.1'




>>> Opening Single Selection ColorPicker

ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                ColorPickerDialog.SELECTION_SINGLE,
                closestColorsList,
                3, // Number of columns
                ColorPickerDialog.SIZE_SMALL);
                
dialog.show(getFragmentManager(), "some_tag");   
                
>>> Opening Multi Selection ColorPicker

ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                ColorPickerDialog.SELECTION_MULTI,
                closestColorsList,
                3, // Number of columns
                ColorPickerDialog.SIZE_SMALL);

dialog.show(getFragmentManager(), "some_tag");


>>> Dialog OnClick Listener

dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
            @Override
            public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                Log.d("selected colors", mSelectedColors.size() + " ");
            }
            @Override
            public void onDismiss() {
            }
        });
        
        
