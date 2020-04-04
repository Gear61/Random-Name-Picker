package com.randomappsinc.studentpicker.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.SpeechToTextManager;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.listpage.ListActivity;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class HomepageFragment extends Fragment implements
        NameListsAdapter.Delegate, RenameListDialog.Listener,
        DeleteListDialog.Listener, SpeechToTextManager.Listener {

    static HomepageFragment getInstance() {
        return new HomepageFragment();
    }

    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;

    @BindView(R.id.focal_point) View focusSink;
    @BindView(R.id.search_bar) View searchBar;
    @BindView(R.id.search_input) EditText searchInput;
    @BindView(R.id.no_lists_match) View noListsMatch;
    @BindView(R.id.voice_search) View voiceSearch;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.user_lists) RecyclerView lists;
    @BindView(R.id.no_content) View noListsAtAll;

    private PreferencesManager preferencesManager;
    private DataSource dataSource;
    private NameListsAdapter nameListsAdapter;
    private RenameListDialog renameListDialog;
    private DeleteListDialog deleteListDialog;
    private SpeechToTextManager speechToTextManager;
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
        preferencesManager = new PreferencesManager(getContext());
        renameListDialog = new RenameListDialog(this, getContext());
        deleteListDialog = new DeleteListDialog(this, getContext());
        dataSource = new DataSource(getContext());

        nameListsAdapter = new NameListsAdapter(this, dataSource.getNameLists(""));
        lists.setAdapter(nameListsAdapter);
        lists.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        setNoContent();

        // When the user is scrolling to browse flashcards, close the soft keyboard
        lists.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    UIUtils.hideKeyboard(getActivity());
                    focusSink.requestFocus();
                }
            }
        });

        speechToTextManager = new SpeechToTextManager(getContext(), this);
        speechToTextManager.setListeningPrompt(R.string.search_with_speech_message);
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
        ((HomeActivity) getActivity()).createNameList();
    }

    @OnClick(R.id.import_from_txt_button)
    void importFromTextFile() {
        ((HomeActivity) getActivity()).importFromTextFile();
    }

    @Override
    public void onResume() {
        super.onResume();
        focusSink.requestFocus();
        nameListsAdapter.refresh(dataSource.getNameLists(searchInput.getText().toString()));
        setNoContent();
    }

    @Override
    public void onItemClick(ListDO listDO) {
        Intent intent = new Intent(getActivity(), ListActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, listDO.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemEditClick(int position, ListDO listDO) {
        renameListDialog.show(position, listDO);
    }

    @Override
    public void onRenameListConfirmed(int position, ListDO updatedList) {
        dataSource.renameList(updatedList);
        preferencesManager.renameList(nameListsAdapter.getItem(position).getName(), updatedList.getName());
        nameListsAdapter.renameItem(position, updatedList.getName());
    }

    @Override
    public void onItemDeleteClick(int position, ListDO listDO) {
        deleteListDialog.presentForList(position, listDO);
    }

    @Override
    public void onDeleteListConfirmed(int position, ListDO listDO) {
        dataSource.deleteList(listDO.getId());
        preferencesManager.removeNameList(listDO.getName());
        nameListsAdapter.deleteItem(position);
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
