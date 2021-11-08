package com.vinilaureto.seriesmanager.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.vinilaureto.seriesmanager.entities.Episode.Episode
import com.vinilaureto.seriesmanager.entities.Episode.EpisodeDAO
import com.vinilaureto.seriesmanager.entities.Season.Season
import com.vinilaureto.seriesmanager.entities.Season.SeasonDAO
import com.vinilaureto.seriesmanager.entities.Series.Series
import com.vinilaureto.seriesmanager.entities.Series.SeriesDAO
import java.sql.SQLException

class Database (context: Context): SeriesDAO, SeasonDAO, EpisodeDAO {
    companion object {
        private val DB_APP = "seriesManager"

        private val TABLE_SERIES = "series"
        private val COL_SERIES_TITLE = "title"
        private val COL_SERIES_YEAR = "year"
        private val COL_SERIES_CHANNEL = "channel"
        private val COL_SERIES_GENRE = "genre"
        private val COL_SERIES_SEASONS = "seasons"
        private val COL_SERIES_ID = "id"

        private val CREATE_SERIES_TABLE_STM = "CREATE TABLE IF NOT EXISTS $TABLE_SERIES (" +
                "$COL_SERIES_TITLE TEXT NOT NULL," +
                "$COL_SERIES_YEAR INTEGER NOT NULL," +
                "$COL_SERIES_CHANNEL TEXT NOT NULL, " +
                "$COL_SERIES_GENRE TEXT NOT NULL," +
                "$COL_SERIES_SEASONS INTEGER NOT NULL," +
                "$COL_SERIES_ID TEXT NOT NULL PRIMARY KEY" +
                ");"

        private val TABLE_SEASON = "seasons"
        private val COL_SEASON_NUMBER = "number"
        private val COL_SEASON_YEAR = "year"
        private val COL_SEASON_EPISODES = "episodes"
        private val COL_SEASON_SERIES_ID = "series_id"
        private val COL_SEASON_ID = "id"

        private val CREATE_SEASON_TABLE_STM = "CREATE TABLE IF NOT EXISTS $TABLE_SEASON (" +
                "$COL_SEASON_NUMBER INTEGER NOT NULL," +
                "$COL_SEASON_YEAR INTEGER NOT NULL," +
                "$COL_SEASON_EPISODES INTEGER NOT NULL, " +
                "$COL_SEASON_SERIES_ID TEXT NOT NULL," +
                "$COL_SEASON_ID TEXT NOT NULL PRIMARY KEY," +
                "FOREIGN KEY ($COL_SEASON_SERIES_ID) REFERENCES $TABLE_SERIES($COL_SERIES_ID)" +
                ");"

        private val TABLE_EPISODES = "episodes"
        private val COL_EPISODE_NUMBER = "number"
        private val COL_EPISODE_TITLE = "title"
        private val COL_EPISODE_DURATION = "duration"
        private val COL_EPISODE_WATCHED = "watched"
        private val COL_EPISODE_SEASON_ID = "season_id"
        private val COL_EPISODE_ID = "id"

        private val CREATE_EPISODE_TABLE_STM = "CREATE TABLE IF NOT EXISTS $TABLE_EPISODES (" +
                "$COL_EPISODE_NUMBER INTEGER NOT NULL," +
                "$COL_EPISODE_TITLE TEXT NOT NULL," +
                "$COL_EPISODE_DURATION TEXT NOT NULL, " +
                "$COL_EPISODE_WATCHED BOOLEAN NOT NULL," +
                "$COL_EPISODE_ID TEXT NOT NULL PRIMARY KEY," +
                "$COL_EPISODE_SEASON_ID TEXT NOT NULL," +
                "FOREIGN KEY ($COL_EPISODE_SEASON_ID) REFERENCES $TABLE_SEASON($COL_SEASON_ID)" +
                ");"
    }

    private val database: SQLiteDatabase

    init {
        database = context.openOrCreateDatabase(DB_APP, Context.MODE_PRIVATE, null)
        try {
            database.execSQL(CREATE_SERIES_TABLE_STM)
            database.execSQL(CREATE_SEASON_TABLE_STM)
            database.execSQL(CREATE_EPISODE_TABLE_STM)
        } catch (error: SQLException) {
            Log.e("DB", error.toString())
        }
    }

    /* Series implementation */
    private fun databaseSeriesAdapter(series: Series): ContentValues? {
        return ContentValues().also {
            with(it) {
                put(COL_SERIES_TITLE, series.title)
                put(COL_SERIES_YEAR, series.year)
                put(COL_SERIES_CHANNEL, series.channel)
                put(COL_SERIES_GENRE, series.genre)
                put(COL_SERIES_SEASONS, series.seasons)
                put(COL_SERIES_ID, series.id)
            }
        }
    }

    override fun createSeries(series: Series): Long {
        return database.insert(TABLE_SERIES, null, databaseSeriesAdapter(series))
    }

    override fun findAllSeries(): MutableList<Series> {
        val seriesList: MutableList<Series> = mutableListOf()
        val seriesQuery = database.query(true, TABLE_SERIES, null, null, null, null, null, null, null)

        while (seriesQuery.moveToNext()) {
            with(seriesQuery) {
                seriesList.add(
                    Series(
                    getString(getColumnIndexOrThrow(COL_SERIES_TITLE)),
                    getInt(getColumnIndexOrThrow(COL_SERIES_YEAR)),
                    getString(getColumnIndexOrThrow(COL_SERIES_CHANNEL)),
                    getString(getColumnIndexOrThrow(COL_SERIES_GENRE)),
                    findSeasonsBySeriesId(getString(getColumnIndexOrThrow(COL_SEASON_ID))).count(),
                    getString(getColumnIndexOrThrow(COL_SERIES_ID)))
                )
            }
        }

        return seriesList
    }

    override fun updateSeries(series: Series): Int {
        val convertedSeries = databaseSeriesAdapter(series)
        return database.update(TABLE_SERIES, convertedSeries, "${COL_SERIES_ID} = ?", arrayOf(series.id))
    }

    override fun removeSeries(series: Series): Int {
        return database.delete(TABLE_SERIES, "$COL_SERIES_ID = ?", arrayOf(series.id))
    }

    /* Season implementation */
    private fun databaseSeasonAdapter(season: Season): ContentValues? {
        return ContentValues().also {
            with(it) {
                put(COL_SEASON_NUMBER, season.number)
                put(COL_SEASON_YEAR, season.year)
                put(COL_SEASON_EPISODES, season.episodes)
                put(COL_SEASON_SERIES_ID, season.seriesId)
                put(COL_SEASON_ID, season.id)
            }
        }
    }

    override fun createSeason(season: Season): Long {
        return database.insert(TABLE_SEASON, null, databaseSeasonAdapter(season))
    }

    override fun findSeasonsBySeriesId(seriesId: String): MutableList<Season> {
        val seasonsList: MutableList<Season> = mutableListOf()
        val seasonsQuery = database.query(true, TABLE_SEASON, null, "$COL_SEASON_SERIES_ID = ?", arrayOf(seriesId), null, null, null, null)

        while (seasonsQuery.moveToNext()) {
            with(seasonsQuery) {
                seasonsList.add(
                    Season(
                        getInt(getColumnIndexOrThrow(COL_SEASON_NUMBER)),
                        getInt(getColumnIndexOrThrow(COL_SEASON_YEAR)),
                        findAllEpisodesBySeason(getString(getColumnIndexOrThrow(COL_SEASON_ID))).count(),
                        getString(getColumnIndexOrThrow(COL_SEASON_SERIES_ID)),
                        getString(getColumnIndexOrThrow(COL_SEASON_ID))
                    )
                )
            }
        }

        return seasonsList
    }

    override fun updateSeason(season: Season): Int {
        val convertedSeason = databaseSeasonAdapter(season)
        return database.update(TABLE_SEASON, convertedSeason, "${COL_SEASON_ID} = ?", arrayOf(season.id))
    }

    override fun removeSeason(season: Season): Int {
        return database.delete(TABLE_SEASON, "$COL_SEASON_ID = ?", arrayOf(season.id))
    }


    /* Episode implementation */
    private fun databaseEpisodeAdapter(episode: Episode): ContentValues? {
        return ContentValues().also {
            with(it) {
                put(COL_EPISODE_NUMBER, episode.number)
                put(COL_EPISODE_TITLE, episode.title)
                put(COL_EPISODE_DURATION, episode.duration)
                put(COL_EPISODE_WATCHED, episode.watched)
                put(COL_EPISODE_SEASON_ID, episode.seasonId)
                put(COL_EPISODE_ID, episode.id)
            }
        }
    }

    override fun createEpisode(episode: Episode): Long {
        return database.insert(TABLE_EPISODES, null, databaseEpisodeAdapter(episode))
    }

    override fun findAllEpisodesBySeason(seasonId: String): MutableList<Episode> {
        val episodeList: MutableList<Episode> = mutableListOf()
        val episodeQuery = database.query(true, TABLE_EPISODES, null, "${COL_EPISODE_SEASON_ID} = ?", arrayOf(seasonId), null, null, null, null)

        while (episodeQuery.moveToNext()) {
            with(episodeQuery) {
                episodeList.add(
                    Episode(
                        getInt(getColumnIndexOrThrow(COL_EPISODE_NUMBER)),
                        getString(getColumnIndexOrThrow(COL_EPISODE_TITLE)),
                        getString(getColumnIndexOrThrow(COL_EPISODE_DURATION)),
                        getString(getColumnIndexOrThrow(COL_EPISODE_WATCHED)).toBoolean(),
                        getString(getColumnIndexOrThrow(COL_EPISODE_SEASON_ID)),
                        getString(getColumnIndexOrThrow(COL_EPISODE_ID))
                    )
                )
            }
        }

        return episodeList
    }

    override fun updateEpisode(episode: Episode): Int {
        val convertedEpisode = databaseEpisodeAdapter(episode)
        return database.update(TABLE_EPISODES, convertedEpisode, "${COL_EPISODE_ID} = ?", arrayOf(episode.id))
    }

    override fun removeEpisode(episode: Episode): Int {
        return database.delete(TABLE_EPISODES, "$COL_EPISODE_ID = ?", arrayOf(episode.id))
    }
}