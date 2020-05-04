package com.randomappsinc.studentpicker.home;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Language;

public class SetLanguageDialog {

    public interface Listener {
        void onLanguagesSelected(@Language int speechLanguage);
    }

    protected Listener listener;
    private MaterialDialog dialog;
    protected Context context;

    public SetLanguageDialog(Context context, Listener listener) {
        this.listener = listener;
        this.context = context;

        this.dialog = new MaterialDialog.Builder(context)
                .title(R.string.set_speech_language_dialog_title)
                .content(R.string.set_speech_language_dialog_body)
                .items(context.getResources().getStringArray(R.array.language_options))
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        listener.onLanguagesSelected(which);
                        return true;
                    }
                })
                .negativeText(R.string.cancel)
                .positiveText(R.string.save)
                .build();
    }

    private int getIndexFromLanguage(@Language int language) {
        switch (language) {
            case Language.DEFAULT:
                return -1;
            case Language.ENGLISH:
                return 0;
            case Language.SPANISH:
                return 1;
            case Language.FRENCH:
                return 2;
            case Language.JAPANESE:
                return 3;
            case Language.PORTUGUESE:
                return 4;
            case Language.CHINESE:
                return 5;
            case Language.GERMAN:
                return 6;
            case Language.ITALIAN:
                return 7;
            case Language.KOREAN:
                return 8;
            case Language.HINDI:
                return 9;
            case Language.BENGALI:
                return 10;
            case Language.RUSSIAN:
                return 11;
            case Language.NORWEGIAN:
                return 12;
            default:
                throw new IllegalArgumentException("Unsupported language!");
        }
    }

    @Language
    private int getLanguageFromIndex(int index) {
        switch (index) {
            case -1:
                return Language.DEFAULT;
            case 0:
                return Language.ENGLISH;
            case 1:
                return Language.SPANISH;
            case 2:
                return Language.FRENCH;
            case 3:
                return Language.JAPANESE;
            case 4:
                return Language.PORTUGUESE;
            case 5:
                return Language.CHINESE;
            case 6:
                return Language.GERMAN;
            case 7:
                return Language.ITALIAN;
            case 8:
                return Language.KOREAN;
            case 9:
                return Language.HINDI;
            case 10:
                return Language.BENGALI;
            case 11:
                return Language.RUSSIAN;
            case 12:
                return Language.NORWEGIAN;
            default:
                throw new IllegalArgumentException("Unsupported index!");
        }
    }

    public void show() {
        dialog.show();
    }
}
