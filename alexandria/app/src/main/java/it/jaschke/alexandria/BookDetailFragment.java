package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.jaschke.alexandria.data.BookContract;
import it.jaschke.alexandria.services.BookService;


public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    @Bind(R.id.fullBookTitle) TextView titleView;
    @Bind(R.id.fullBookSubTitle) TextView subtitleView;
    @Bind(R.id.fullBookDesc) TextView descriptionView;
    @Bind(R.id.authors) TextView authorsView;
    @Bind(R.id.fullBookCover) ImageView bookCoverView;
    @Bind(R.id.categories) TextView categoriesView;
    @Bind(R.id.backButton) AppCompatImageButton backButton;

    public BookDetailFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            ean = arguments.getString(BookDetailFragment.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.delete_button)
    public void deleteBook() {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                BookContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
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

        bookTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.TITLE));
        titleView.setText(bookTitle);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+bookTitle);
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }

        String bookSubTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.SUBTITLE));
        subtitleView.setText(bookSubTitle);

        String desc = data.getString(data.getColumnIndex(BookContract.BookEntry.DESC));
        descriptionView.setText(desc);

        String authors = data.getString(data.getColumnIndex(BookContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        authorsView.setLines(authorsArr.length);
        authorsView.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(BookContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            Picasso.with(getContext())
                    .load(imgUrl)
                    .into(bookCoverView);
            bookCoverView.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(BookContract.CategoryEntry.CATEGORY));
        categoriesView.setText(categories);

        if(rootView.findViewById(R.id.right_container)!=null){
            backButton.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}