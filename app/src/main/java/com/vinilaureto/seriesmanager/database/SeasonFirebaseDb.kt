package com.vinilaureto.seriesmanager.database

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vinilaureto.seriesmanager.entities.Season.Season
import com.vinilaureto.seriesmanager.entities.Season.SeasonDAO
import com.vinilaureto.seriesmanager.entities.Series.Series

class SeasonFirebaseDb: SeasonDAO  {
    companion object {
        private val DB_APP = "seasons"
    }

    // Referência para o Realtime Database - vai criar caso não exista
    private val appRtDb = Firebase.database.getReference(DB_APP)

    // Datasource - Firebase vai atualizar essa lista
    private val seasonsList = mutableListOf<Season>()


    init {
        appRtDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                                val newSeason = snapshot.getValue(Season::class.java)
                newSeason?.apply {
                    if (seasonsList.find { it.id == this.id } == null) {
                        seasonsList.add(this)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val editedSeason: Season? = snapshot.value as? Season
                editedSeason?.apply {
                    seasonsList[seasonsList.indexOfFirst { it.id == this.id }] = this
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedSeries: Season? = snapshot.value as? Season
                removedSeries?.apply {
                    seasonsList.remove(this)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Não se aplica devido a estrutura da arvore atual
            }

            override fun onCancelled(error: DatabaseError) {
                // Não se aplica devido a estrutura da arvore atual
            }
        })

        appRtDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                seasonsList.clear()
                snapshot.children.forEach {
                    val season: Season = it.getValue<Season>()?: Season()
                    seasonsList.add(season)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Não se aplica
            }

        })
    }

    private fun createOrUpdateSeason(season: Season) {
        appRtDb.child(season.id).setValue(season)
    }

    override fun createSeason(season: Season): Long {
        createOrUpdateSeason(season)
        return  0L
    }

    override fun findSeasonsBySeriesId(seriesId: String): MutableList<Season> {
        var seasonsOfSeries = mutableListOf<Season>()
        seasonsList.forEach {
            if (it.seriesId == seriesId) {
                seasonsOfSeries.add(it)
            }
        }
        return seasonsOfSeries
    }

    override fun updateSeason(season: Season): Int {
        createOrUpdateSeason(season)
        return  1
    }

    override fun removeSeason(season: Season): Int {
        appRtDb.child(season.id).removeValue()
        return 1
    }

    override fun findOneSeasonBySeriesId(seriesId: String, seasonNumber: Int): MutableList<Season> {
        var seasonsOfSeries = mutableListOf<Season>()
        seasonsList.forEach {
            if (it.seriesId == seriesId && it.number == seasonNumber) {
                seasonsOfSeries.add(it)
            }
        }
        return seasonsOfSeries
    }
}