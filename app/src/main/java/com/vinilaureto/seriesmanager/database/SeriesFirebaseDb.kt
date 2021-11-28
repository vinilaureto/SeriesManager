package com.vinilaureto.seriesmanager.database

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vinilaureto.seriesmanager.auth.AuthFirebase
import com.vinilaureto.seriesmanager.entities.Series.Series
import com.vinilaureto.seriesmanager.entities.Series.SeriesDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SeriesFirebaseDb: SeriesDAO  {
    companion object {
        private val DB_APP = "series"
    }

    // Referência para o Realtime Datanase - vai criar caso não exista
    private val appRtDb = Firebase.database.getReference(DB_APP)

    // Datasource - Firebase vai atualizar essa lista
    private val seriesList = mutableListOf<Series>()


    init {
        appRtDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newSeries = snapshot.getValue(Series::class.java)
                newSeries?.apply {
                    if (seriesList.find { it.id == this.id } == null) {
                        seriesList.add(this)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val editedSeries: Series? = snapshot.value as? Series
                editedSeries?.apply {
                    seriesList[seriesList.indexOfFirst { it.id == this.id }] = this
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedSeries: Series? = snapshot.value as? Series
                removedSeries?.apply {
                    seriesList.remove(this)
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
                seriesList.clear()
                snapshot.children.forEach {
                    val series: Series = it.getValue<Series>()?: Series()
                    seriesList.add(series)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Não se aplica
            }

        })
    }

    override fun createSeries(series: Series): Long {
        createOrUpdateSeries(series)
        return 0L
    }

    override fun findAllSeries(): MutableList<Series> {
        val seriesOfUser = mutableListOf<Series>()
        val userCode = AuthFirebase.firebaseAuth.currentUser?.uid.toString()
        seriesList.forEach {
            if (it.userCode == userCode) {
                seriesOfUser.add(it)
            }
        }
        return seriesOfUser
    }

    override fun updateSeries(series: Series): Int {
        createOrUpdateSeries(series)
        return 1
    }

    override fun removeSeries(series: Series): Int {
        appRtDb.child(series.id).removeValue()
        return 1
    }

    override fun findSeriesByTitle(title: String): MutableList<Series> {
        var returnList = mutableListOf<Series>()
        seriesList.forEach {
            if (it.title == title) {
                returnList.add(it)
            }
        }
        return seriesList
    }

    private fun createOrUpdateSeries(series: Series) {
        appRtDb.child(series.id).setValue(series)
    }
}