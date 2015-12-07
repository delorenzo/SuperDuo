package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.data.FootballScoresContract.ScoresEntry;

public class FootballScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 2;
    public FootballScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CreateScoresTable = "CREATE TABLE " + FootballScoresContract.SCORES_TABLE + " ("
                + ScoresEntry._ID + " INTEGER PRIMARY KEY,"
                + FootballScoresContract.ScoresEntry.DATE_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresEntry.TIME_COL + " INTEGER NOT NULL,"
                + FootballScoresContract.ScoresEntry.HOME_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresEntry.AWAY_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresEntry.LEAGUE_COL + " INTEGER NOT NULL,"
                + FootballScoresContract.ScoresEntry.HOME_GOALS_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresEntry.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + ScoresEntry.MATCH_ID + " INTEGER NOT NULL,"
                + ScoresEntry.MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE ("+ FootballScoresContract.ScoresEntry.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + FootballScoresContract.SCORES_TABLE);
    }
}
