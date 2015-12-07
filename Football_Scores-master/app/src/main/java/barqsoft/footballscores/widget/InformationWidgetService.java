package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import org.joda.time.LocalDate;

import java.util.Locale;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.FootballScoresContract;


public class InformationWidgetService extends IntentService {

    public InformationWidgetService() { super("InformationWidgetService"); }

    private static final String[] SCORE_COLUMNS = {
            FootballScoresContract.ScoresEntry.MATCH_ID,
            FootballScoresContract.ScoresEntry.HOME_COL,
            FootballScoresContract.ScoresEntry.HOME_GOALS_COL,
            FootballScoresContract.ScoresEntry.AWAY_GOALS_COL,
            FootballScoresContract.ScoresEntry.AWAY_COL
    };
    static final int INDEX_MATCH_ID = 0;
    static final int INDEX_HOME_TEAM = 1;
    static final int INDEX_HOME_GOALS = 2;
    static final int INDEX_AWAY_GOALS = 3;
    static final int INDEX_AWAY_TEAM = 4;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, InformationWidgetProvider.class));
        LocalDate currentDate = new LocalDate();
        String[] currentDateString = new String[1];
        currentDateString[0] = currentDate.toString(
                context.getString(R.string.fragment_date_format),
                Locale.getDefault());

        Uri scoresUri = FootballScoresContract.ScoresEntry.buildScoreWithDate();
        Cursor data = getContentResolver().query(
                FootballScoresContract.ScoresEntry.buildScoreWithDate(),
                SCORE_COLUMNS,
                null,
                currentDateString,
                //FootballScoresContract.ScoresEntry.DATE_COL + " ASC"
                null
        );

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String homeTeam = data.getString(INDEX_HOME_TEAM);
        String awayTeam = data.getString(INDEX_AWAY_TEAM);
        int homeGoals = data.getInt(INDEX_HOME_GOALS);
        int awayGoals = data.getInt(INDEX_AWAY_GOALS);
        data.close();

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

            int layoutId = R.layout.information_widget;
            RemoteViews views = new RemoteViews(this.getPackageName(), layoutId);
            views.setTextViewText(R.id.home_team, homeTeam);
            views.setTextViewText(R.id.away_team, awayTeam);
            views.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(homeTeam));
            views.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(awayTeam));
            views.setTextViewText(R.id.score, Utilities.getScores(context, homeGoals, awayGoals));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(
                        views,
                        String.format(context.getString(R.string.home_crest), homeTeam),
                        String.format(context.getString(R.string.away_crest), awayTeam)
                );
            }
            views.setOnClickPendingIntent(layoutId, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String homeDescription, String awayDescription) {
        views.setContentDescription(R.id.home_crest, homeDescription);
        views.setContentDescription(R.id.away_crest, awayDescription);
    }
}

