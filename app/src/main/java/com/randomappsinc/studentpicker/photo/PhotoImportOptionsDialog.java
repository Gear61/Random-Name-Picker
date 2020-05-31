package com.randomappsinc.studentpicker.photo;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class PhotoImportOptionsDialog {

    public interface Delegate {
        void addWithCamera();

        void addWithGallery();
    }

    private MaterialDialog optionsDialog;

    public PhotoImportOptionsDialog(Context context, Delegate delegate) {
        this.optionsDialog = new MaterialDialog.Builder(context)
                .title(R.string.photo_import_dialog_title)
                .items(R.array.photo_import_options)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            delegate.addWithGallery();
                            break;
                        case 1:
                            delegate.addWithCamera();
                            break;
                    }
                })
                .positiveText(R.string.cancel)
                .build();
    }

    public void showPhotoOptions() {
        optionsDialog.show();
    }
}
