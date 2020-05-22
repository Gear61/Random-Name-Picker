package com.randomappsinc.studentpicker.home;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.choosing.NameChoosingActivity;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.PremiumFeature;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.listpage.ListLandingPageActivity;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.premium.BuyPremiumActivity;
import com.randomappsinc.studentpicker.speech.SpeechToTextManager;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class HomepageFragment extends Fragment implements
        NameListsAdapter.Delegate, SpeechToTextManager.Listener {

    static HomepageFragment getInstance() {
        return new HomepageFragment();
    }

    private static final int NUM_APP_OPENS_FOR_PREMIUM_TOOLTIP = 5;

    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;

    @BindView(R.id.homepage_root) View rootView;
    @BindView(R.id.buy_premium_tooltip) View buyPremiumTooltip;
    @BindView(R.id.search_bar) View searchBar;
    @BindView(R.id.search_input) EditText searchInput;
    @BindView(R.id.no_lists_match) View noListsMatch;
    @BindView(R.id.voice_search) View voiceSearch;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.user_lists) RecyclerView lists;
    @BindView(R.id.no_content) View noListsAtAll;
    @BindView(R.id.import_from_csv_empty_state_text) TextView importFromCsvEmptyStateText;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bottomAdBannerContainer;

    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private DataSource dataSource;
    private NameListsAdapter nameListsAdapter;
    private SpeechToTextManager speechToTextManager;
    private BannerAdManager bannerAdManager;
    private PreferencesManager preferencesManager;
    private Unbinder unbinder;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.homepage_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataSource = new DataSource(getContext());

        nameListsAdapter = new NameListsAdapter(this, dataSource.getNameLists(""));
        lists.setAdapter(nameListsAdapter);

        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(lineDivider);
        lists.addItemDecoration(itemDecorator);
        setNoContent();

        // When the user is scrolling to browse lists, close the soft keyboard
        lists.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    UIUtils.hideKeyboard(requireActivity());
                    rootView.requestFocus();
                }
            }
        });

        speechToTextManager = new SpeechToTextManager(getContext(), this);
        speechToTextManager.setListeningPrompt(R.string.search_with_speech_message);

        bannerAdManager = new BannerAdManager(bottomAdBannerContainer);

        preferencesManager = new PreferencesManager(getContext());
        if (preferencesManager.isOnFreeVersion() &&
                (!preferencesManager.hasSeenPremiumTooltip()
                        || preferencesManager.getNumAppOpens() % NUM_APP_OPENS_FOR_PREMIUM_TOOLTIP == 0)) {
            buyPremiumTooltip.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.buy_premium_tooltip)
    void buyPremium() {
        Intent intent = new Intent(getActivity(), BuyPremiumActivity.class);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
        buyPremiumTooltip.setVisibility(View.GONE);
    }

    @OnClick(R.id.dismiss_premium_tooltip)
    void dismissPremiumTooltip() {
        buyPremiumTooltip.setVisibility(View.GONE);
    }

    @OnClick(R.id.voice_search)
    void searchWithVoice() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.RECORD_AUDIO, getContext())) {
            speechToTextManager.startSpeechToTextFlow();
        } else {
            PermissionUtils.requestPermission(
                    this, Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onTextSpoken(String spokenText) {
        searchInput.setText(spokenText);
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable input) {
        nameListsAdapter.refresh(dataSource.getNameLists(input.toString()));
        setNoContent();
        voiceSearch.setVisibility(input.length() == 0 ? View.VISIBLE : View.GONE);
        clearSearch.setVisibility(input.length() == 0 ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.clear_search)
    void clearSearch() {
        searchInput.setText("");
    }

    @OnClick(R.id.create_name_list_button)
    void createNameList() {
        ((HomeActivity) requireActivity()).createNameList();
    }

    @OnClick(R.id.import_from_txt_button)
    void importFromTextFile() {
        ((HomeActivity) requireActivity()).importFromTextFile();
    }

    @OnClick(R.id.import_from_csv_button)
    void importFromCsvFile() {
        ((HomeActivity) requireActivity()).importFromCsvFile();
    }

    @Override
    public void onResume() {
        super.onResume();
        bannerAdManager.loadOrRemoveAd();
        importFromCsvEmptyStateText.setText(preferencesManager.hasUnlockedFeature(PremiumFeature.IMPORT_FROM_CSV)
                ? R.string.import_from_csv_file
                : R.string.import_from_csv_file_premium);
        if (!preferencesManager.isOnFreeVersion() && buyPremiumTooltip.getVisibility() == View.VISIBLE) {
            buyPremiumTooltip.setVisibility(View.GONE);
        }
        rootView.requestFocus();
        nameListsAdapter.refresh(dataSource.getNameLists(searchInput.getText().toString()));
        setNoContent();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    @Override
    public void onItemClick(ListDO listDO) {
        Intent intent = new Intent(getActivity(), ListLandingPageActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, listDO.getId());
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void onChooseButtonClicked(ListDO listDO) {
        Intent intent = new Intent(getActivity(), NameChoosingActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, listDO.getId());
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void setNoContent() {
        if (dataSource.getNumLists() == 0L) {
            searchBar.setVisibility(View.GONE);
            lists.setVisibility(View.GONE);
            noListsMatch.setVisibility(View.GONE);
            noListsAtAll.setVisibility(View.VISIBLE);
        } else {
            noListsAtAll.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);
            if (nameListsAdapter.getItemCount() == 0) {
                lists.setVisibility(View.GONE);
                noListsMatch.setVisibility(View.VISIBLE);
            } else {
                noListsMatch.setVisibility(View.GONE);
                lists.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
