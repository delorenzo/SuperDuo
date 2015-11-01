package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import it.jaschke.alexandria.data.BookContract;
import it.jaschke.alexandria.services.BookService;


public class AddBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    View rootView;
    @Bind(R.id.ean) TextView eanView;
    @Bind(R.id.bookTitle) TextView bookTitleView;
    @Bind(R.id.bookSubTitle) TextView bookSubTitleView;
    @Bind(R.id.authors) TextView authorsView;
    @Bind(R.id.categories) TextView categoriesView;
    @Bind(R.id.bookCover) ImageView bookCoverImage;

    private final int LOADER_ID = 1;
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private final String EAN_CONTENT = "eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";
    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";
    private final String LOG_TAG = getClass().getSimpleName();

    public AddBookFragment(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(eanView !=null) {
            outState.putString(EAN_CONTENT, eanView.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);
        if(savedInstanceState!=null){
            eanView.setText(savedInstanceState.getString(EAN_CONTENT));
            eanView.setHint("");
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnTextChanged(value = R.id.ean, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        String ean = s.toString();
        //catch isbn10 numbers
        if (ean.length() == 10 && !ean.startsWith("978")) {
            ean = "978" + ean;
        }
        if (ean.length() < 13) {
            clearFields();
            return;
        }
        //Once we have an ISBN, start a book intent
        startBookIntent(ean);
    }

    private void startBookIntent(String ean) {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        getActivity().startService(bookIntent);
        AddBookFragment.this.restartLoader();
    }

    @OnClick(R.id.scan_button)
    public void scan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult barcodeScanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (barcodeScanResult.getContents() != null) {
            String barcode = barcodeScanResult.getContents();
            Log.v(LOG_TAG, "Barcode scanned:  " + barcode);
            eanView.setText(barcode);
            startBookIntent(barcode);
        }
        else {
            eanView.setText("");
        }
    }

    @OnClick(R.id.delete_button)
    public void delete() {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, eanView.getText().toString());
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        eanView.setText("");
    }

    @OnClick(R.id.save_button)
    public void save() {
        eanView.setText("");
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(eanView.getText().length()==0){
            return null;
        }
        String eanStr= eanView.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                BookContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.TITLE));
        bookTitleView.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.SUBTITLE));
        bookSubTitleView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(BookContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        authorsView.setLines(authorsArr.length);
        authorsView.setText(authors.replace(",", "\n"));

        String imgUrl = data.getString(data.getColumnIndex(BookContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            Picasso.with(getContext())
                    .load(imgUrl)
                    .into(bookCoverImage);
            bookCoverImage.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(BookContract.CategoryEntry.CATEGORY));
        categoriesView.setText(categories);

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        bookTitleView.setText("");
        bookSubTitleView.setText("");
        authorsView.setText("");
        categoriesView.setText("");
        bookCoverImage.setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }
}
