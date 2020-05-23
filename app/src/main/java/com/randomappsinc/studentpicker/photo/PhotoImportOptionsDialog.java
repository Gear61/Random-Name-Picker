package com.randomappsinc.studentpicker.photo;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.PremiumFeature;
import com.randomappsinc.studentpicker.premium.PremiumFeatureOpener;

public class PhotoImportOptionsDialog {

    public interface Delegate extends PremiumFeatureOpener.Delegate {
        void takePictureWithCamera();

        void importImageFromGallery();
    }

    private Delegate delegate;
    private PremiumFeatureOpener premiumFeatureOpener;
    private MaterialDialog optionsDialog;

    public PhotoImportOptionsDialog(Context context, Delegate delegate) {
        this.delegate = delegate;
        this.premiumFeatureOpener = new PremiumFeatureOpener(context, delegate);
        this.optionsDialog = new MaterialDialog.Builder(context)
                .title(R.string.photo_import_dialog_title)
                .items(R.array.photo_import_options)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            maybeOpenCamera();
                            break;
                        case 1:
                            maybeOpenGallery();
                            break;
                    }
                })
                .positiveText(R.string.cancel)
                .build();
    }

    public void showPhotoOptions() {
        optionsDialog.show();
    }

    private void maybeOpenCamera() {
        premiumFeatureOpener.openPremiumFeature(
                PremiumFeature.ADD_PHOTO_FROM_GALLERY, () -> delegate.takePictureWithCamera());
    }

    private void maybeOpenGallery() {
        premiumFeatureOpener.openPremiumFeature(
                PremiumFeature.ADD_PHOTO_WITH_CAMERA, () -> delegate.importImageFromGallery());
    }
}
