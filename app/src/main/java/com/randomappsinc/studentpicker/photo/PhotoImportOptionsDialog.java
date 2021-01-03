package com.randomappsinc.studentpicker.photo;

import android.content.Context;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;

public class PhotoImportOptionsDialog {

    public interface Delegate {
        void addWithCamera();

        void addWithGallery();
    }

    private final MaterialDialog optionsDialog;

    public PhotoImportOptionsDialog(Context context, Delegate delegate) {
        this.optionsDialog = new MaterialDialog.Builder(context)
                .title(R.string.photo_options_dialog_title)
                .items(R.array.photo_import_options_without_upload)
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

    public void showPhotoOptions(NameDO nameDO) {
        boolean imageAlreadyUploaded = !TextUtils.isEmpty(nameDO.getPhotoUri());
        Context context = optionsDialog.getContext();
        String[] options = imageAlreadyUploaded
                ? context.getResources().getStringArray(R.array.photo_import_options_with_upload)
                : context.getResources().getStringArray(R.array.photo_import_options_without_upload);
        optionsDialog.setItems(options);
        optionsDialog.show();
    }
}
