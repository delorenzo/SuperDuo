package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.joda.time.LocalDate;

import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.FootballScoresContract;


public class CollectionWidgetService extends RemoteViewsService {

    private static final String[] SCORE_COLUMNS = {
            FootballScoresContract.ScoresEntry.HOME_COL,
            FootballScoresContract.ScoresEntry.HOME_GOALS_COL,
            FootballScoresContract.ScoresEntry.AWAY_GOALS_COL,
            FootballScoresContract.ScoresEntry.AWAY_COL,
            FootballScoresContract.ScoresEntry.MATCH_ID
    };
    private static final int INDEX_HOME_TEAM = 0;
    private static final int INDEX_HOME_GOALS = 1;
    private static final int INDEX_AWAY_GOALS = 2;
    private static final int INDEX_AWAY_TEAM = 3;
    private static final int INDEX_MATCH_ID = 4;

    public static final String LOG_TAG = CollectionWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                LocalDate currentDate = new LocalDate();
                String[] currentDateString = new String[1];
                currentDateString[0] = currentDate.toString(
                        getApplicationContext().getString(R.string.fragment_date_format),
                        Locale.getDefault());
                final long identityToken = Binder.clearCallingIdentity();
                Uri scoresUri = FootballScoresContract.ScoresEntry.buildScoreWithDate();
                Cursor data = getContentResolver().query(
                        FootballScoresContract.ScoresEntry.buildScoreWithDate(),
                        SCORE_COLUMNS,
                        null,
                        currentDateString,
                        FootballScoresContract.ScoresEntry.DATE_COL + " ASC"
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null ||
                        !data.moveToPosition(position)) {
                    return null;
                }

                Context context = getApplicationContext();
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.collection_widget_item);
                String homeTeam = data.getString(INDEX_HOME_TEAM);
                String awayTeam = data.getString(INDEX_AWAY_TEAM);
                int homeGoals = data.getInt(INDEX_HOME_GOALS);
                int awayGoals = data.getInt(INDEX_AWAY_GOALS);
                views.setTextViewText(R.id.home_team, homeTeam);
                views.setTextViewText(R.id.away_team, awayTeam);
                views.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(homeTeam));
                views.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(awayTeam));
                views.setTextViewText(R.id.score, Utilities.getScores(
                        context,
                        homeGoals,
                        awayGoals));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(
                            views,
                            String.format(context.getString(R.string.home_crest), homeTeam),
                            String.format(context.getString(R.string.away_crest), awayTeam)
                    );
                }

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.id.collection_widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(INDEX_MATCH_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String homeDescription, String awayDescription) {
                views.setContentDescription(R.id.home_crest, homeDescription);
                views.setContentDescription(R.id.away_crest, awayDescription);
            }
        };
    }
}

