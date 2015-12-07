package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.Locale;

import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.service.ScoresSyncService;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public FootballScoresAdapter mAdapter;
    @Bind(R.id.scores_list) ListView scoreList;
    @Bind(R.id.empty_matches_textview) TextView emptyView;
    public static final int SCORES_LOADER = 0;
    private LocalDate fragmentDate;
    private String[] fragmentDateString = new String[1];
    private int lastSelectedItem = -1;

    private static final String[] SCORE_COLUMNS = {
            FootballScoresContract.ScoresEntry._ID,
            FootballScoresContract.ScoresEntry.HOME_COL,
            FootballScoresContract.ScoresEntry.HOME_GOALS_COL,
            FootballScoresContract.ScoresEntry.AWAY_GOALS_COL,
            FootballScoresContract.ScoresEntry.AWAY_COL,
            FootballScoresContract.ScoresEntry.LEAGUE_COL,
            FootballScoresContract.ScoresEntry.MATCH_DAY,
            FootballScoresContract.ScoresEntry.TIME_COL,
            FootballScoresContract.ScoresEntry.MATCH_ID
    };
    static final int INDEX_SCORE_ID = 0;
    static final int INDEX_HOME_TEAM = 1;
    static final int INDEX_HOME_GOALS = 2;
    static final int INDEX_AWAY_GOALS = 3;
    static final int INDEX_AWAY_TEAM = 4;
    static final int INDEX_LEAGUE = 5;
    static final int INDEX_MATCH_DAY = 6;
    static final int INDEX_MATCH_TIME = 7;
    static final int INDEX_MATCH_ID = 8;

    public MainScreenFragment()
    {
    }

    public static MainScreenFragment newInstance(LocalDate date, Context context)
    {
        MainScreenFragment fragment = new MainScreenFragment();
        fragment.fragmentDate = date;
        fragment.fragmentDateString[0] = date.toString(
                context.getString(R.string.fragment_date_format),
                Locale.getDefault());
        return fragment;
    }

    private void updateScores()
    {
        Intent service_start = new Intent(getActivity(), ScoresSyncService.class);
        getActivity().startService(service_start);
    }

    public void setFragmentDate(LocalDate date, Context context)
    {
        fragmentDate = date;
        fragmentDateString[0] = date.toString(
                context.getString(R.string.fragment_date_format),
                Locale.getDefault());
    }

    public LocalDate getFragmentDate()
    {
        return fragmentDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        updateScores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new FootballScoresAdapter(getActivity(),null,0);
        scoreList.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        mAdapter.detail_match_id = MainActivity.selectedMatchId;
        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.matchId;
                MainActivity.selectedMatchId = (int) selected.matchId;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(
                getActivity(),
                FootballScoresContract.ScoresEntry.buildScoreWithDate(),
                SCORE_COLUMNS,
                null,
                fragmentDateString,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }
        mAdapter.swapCursor(cursor);
        mAdapter.notifyDataSetChanged();

        int emptyViewVisibility = mAdapter.isEmpty() ? TextView.VISIBLE : TextView.GONE;
        emptyView.setVisibility(emptyViewVisibility);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }

}
