package com.vinilaureto.seriesmanager.database

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vinilaureto.seriesmanager.entities.Episode.Episode
import com.vinilaureto.seriesmanager.entities.Episode.EpisodeDAO

class EpisodeFirebaseDb: EpisodeDAO  {
    companion object {
        private val DB_APP = "episodes"
    }

    private val appRtDb = Firebase.database.getReference(DB_APP)
    private val episodesList = mutableListOf<Episode>()

    init {
        appRtDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newEpisode = snapshot.getValue(Episode::class.java)
                newEpisode?.apply {
                    if (episodesList.find { it.id == this.id } == null) {
                        episodesList.add(this)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val editedEpisode: Episode? = snapshot.value as? Episode
                editedEpisode?.apply {
                    episodesList[episodesList.indexOfFirst { it.id == this.id }] = this
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedEpisode: Episode? = snapshot.value as? Episode
                removedEpisode?.apply {
                    episodesList.remove(this)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        appRtDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                episodesList.clear()
                snapshot.children.forEach {
                    val episode: Episode = it.getValue<Episode>()?: Episode()
                    episodesList.add(episode)
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun createOrUpdateEpisode(episode: Episode) {
        appRtDb.child(episode.id).setValue(episode)
    }

    override fun createEpisode(episode: Episode): Long {
        createOrUpdateEpisode(episode)
        return 0L
    }

    override fun findAllEpisodesBySeason(seasonId: String): MutableList<Episode> {
        var episodesOfSeason = mutableListOf<Episode>()
        episodesList.forEach {
            if (it.seasonId == seasonId) {
                episodesOfSeason.add(it)
            }
        }
        return episodesOfSeason
    }

    override fun updateEpisode(episode: Episode): Int {
        createOrUpdateEpisode(episode)
        return 1
    }

    override fun removeEpisode(episode: Episode): Int {
        appRtDb.child(episode.id).removeValue()
        return 1
    }

    override fun findOneEpisodeBySeasonId(
        seasonId: String,
        episodeNumber: Int
    ): MutableList<Episode> {
        var episodesOfSeason = mutableListOf<Episode>()
        episodesList.forEach {
            if (it.seasonId == seasonId && it.number == episodeNumber) {
                episodesOfSeason.add(it)
            }
        }
        return episodesOfSeason
    }


}