package com.randomappsinc.studentpicker.photo;

import android.content.Context;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;

public class PhotoImportOptionsDialog {

    public interface Delegate {
        void viewImage(NameDO nameDO);

        void addWithGallery();

        void addWithCamera();

        void removeImage(NameDO nameDO);
    }

    private Context context;
    private Delegate delegate;

    public PhotoImportOptionsDialog(Context context, Delegate delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    public void showPhotoOptions(NameDO nameDO) {
        boolean imageAlreadyUploaded = !TextUtils.isEmpty(nameDO.getPhotoUri());
        if (imageAlreadyUploaded) {
            new MaterialDialog.Builder(context)
                    .title(R.string.photo_options_dialog_title)
                    .items(R.array.photo_import_options_with_upload)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        switch (position) {
                            case 0:
                                delegate.viewImage(nameDO);
                                break;
                            case 1:
                                delegate.addWithGallery();
                                break;
                            case 2:
                                delegate.addWithCamera();
                                break;
                            case 3:
                                delegate.removeImage(nameDO);
                                break;
                        }
                    })
                    .positiveText(R.string.cancel)
                    .show();
        } else {
            new MaterialDialog.Builder(context)
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
                    .show();
        }
    }

    public void cleanUp() {
        context = null;
        delegate = null;
    }
}
