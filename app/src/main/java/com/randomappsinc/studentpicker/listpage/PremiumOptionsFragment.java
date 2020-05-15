package com.randomappsinc.studentpicker.listpage;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.choosing.ChoosingMessageDialog;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.PremiumFeature;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.export.CsvExporter;
import com.randomappsinc.studentpicker.export.TxtExporter;
import com.randomappsinc.studentpicker.premium.BuyPremiumActivity;
import com.randomappsinc.studentpicker.premium.PremiumFeatureOpener;
import com.randomappsinc.studentpicker.speech.SetLanguageDialog;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PremiumOptionsFragment extends Fragment
        implements ListOptionsAdapter.ItemSelectionListener, CsvExporter.Listener,
        TxtExporter.Listener, ChoosingMessageDialog.Listener, SetLanguageDialog.Listener,
        PremiumFeatureOpener.Delegate {

    static PremiumOptionsFragment getInstance(int listId) {
        PremiumOptionsFragment fragment = new PremiumOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.recycler_view) RecyclerView premiumOptions;
    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private int listId;
    private DataSource dataSource;
    private CsvExporter csvExporter;
    private TxtExporter txtExporter;
    private ChoosingMessageDialog choosingMessageDialog;
    private SetLanguageDialog setLanguageDialog;
    private PremiumFeatureOpener premiumFeatureOpener;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.simple_vertical_recyclerview,
                container,
                false);
        listId = getArguments().getInt(Constants.LIST_ID_KEY);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(lineDivider);
        premiumOptions.addItemDecoration(itemDecorator);
        premiumOptions.setAdapter(new ListOptionsAdapter(
                getActivity(),
                this,
                R.array.premium_options,
                R.array.premium_options_icons));
        dataSource = new DataSource(getContext());
        csvExporter = new CsvExporter(this);
        txtExporter = new TxtExporter(this);
        choosingMessageDialog = new ChoosingMessageDialog(getContext(), this, listId);

        int currentLanguage = dataSource.getChoosingSettings(listId).getSpeechLanguage();
        setLanguageDialog = new SetLanguageDialog(getContext(), this, currentLanguage);
        premiumFeatureOpener = new PremiumFeatureOpener(getContext(), this);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                premiumFeatureOpener.openPremiumFeature(
                        PremiumFeature.SHARE_AS_TXT, () -> txtExporter.turnListIntoTxt(listId, getContext()));
                break;
            case 1:
                premiumFeatureOpener.openPremiumFeature(
                        PremiumFeature.SHARE_AS_CSV, () -> csvExporter.turnListIntoCsv(listId, getContext()));
                break;
            case 2:
                premiumFeatureOpener.openPremiumFeature(
                        PremiumFeature.CUSTOMIZE_CHOOSING_MESSAGE, () -> choosingMessageDialog.show());
                break;
            case 3:
                premiumFeatureOpener.openPremiumFeature(
                        PremiumFeature.SET_SPEECH_LANGUAGE, () -> setLanguageDialog.show());
                break;
        }
    }

    @Override
    public void launchBuyPremiumPage() {
        Intent intent = new Intent(getActivity(), BuyPremiumActivity.class);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void onCsvFileCreated(Uri fileUri) {
        getActivity().runOnUiThread(() -> {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.setType("application/csv");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri);

            String listName = dataSource.getListName(listId);
            intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.export_file_title, listName));
            startActivity(Intent.createChooser(intentShareFile, getString(R.string.export_file_with)));
        });
    }

    @Override
    public void onCsvExportFailed() {
        getActivity().runOnUiThread(() -> UIUtils.showLongToast(
                R.string.export_csv_failed, getContext()));
    }

    @Override
    public void onTxtFileCreated(Uri fileUri) {
        getActivity().runOnUiThread(() -> {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.setType("text/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri);

            String listName = dataSource.getListName(listId);
            intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.export_file_title, listName));
            startActivity(Intent.createChooser(intentShareFile, getString(R.string.export_file_with)));
        });
    }

    @Override
    public void onTxtExportFailed() {
        getActivity().runOnUiThread(() -> UIUtils.showLongToast(
                R.string.export_txt_failed, getContext()));
    }

    @Override
    public void onNewChoosingMessageConfirmed(String newMessage) {
        dataSource.updateChoosingMessage(listId, newMessage);
        UIUtils.showShortToast(R.string.choosing_message_updated, getContext());
    }

    @Override
    public void onLanguageSelected(int speechLanguage) {
        dataSource.updateSpeechLanguage(listId, speechLanguage);
        UIUtils.showShortToast(R.string.speech_language_set, getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
