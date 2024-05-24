package com.example.swimeet.data.repository


import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class CompetitionRepository {

    suspend fun addCompetition(competition: Competition, onCompetitionAdded: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val id = FirebaseUtil.getCompetitionsRef().document().id
            competition.eventId = id

            FirebaseUtil.getCompetitionsRef().document(id).set(competition).addOnSuccessListener {
                onCompetitionAdded(true)
            }.addOnFailureListener {
                onCompetitionAdded(false)
            }
        }
    }

    suspend fun getCompetitions(): List<Competition> {
        val competitionList = mutableListOf<Competition>()

        deleteCompetitions()

        withContext(Dispatchers.IO) {
            val result =
                FirebaseUtil.getCompetitionsRef().orderBy("date", Query.Direction.ASCENDING).get()
                    .await()

            for (document in result.documents) {
                competitionList.add(document.toObject(Competition::class.java)!!)
            }

        }

        return competitionList
    }

    suspend fun deleteCompetitions() {
        val currentTime = Calendar.getInstance().timeInMillis / 1000 // Convertimos a segundos
        val currentTimestamp = Timestamp(currentTime, 0)

        withContext(Dispatchers.IO) {
            val result =
                FirebaseUtil.getCompetitionsRef().whereLessThan("date", currentTimestamp)
                    .get().await()

            for (document in result.documents) {
                document.reference.delete()
            }
        }
    }

    suspend fun deleteEvents() {
        val currentTime = Calendar.getInstance().timeInMillis / 1000 // Convertimos a segundos
        val currentTimestamp = Timestamp(currentTime, 0)

        withContext(Dispatchers.IO) {
            val result =
                FirebaseUtil.getEventsRef().whereLessThan("date", currentTimestamp)
                    .get().await()

            for (document in result.documents) {
                document.reference.delete()
            }
        }
    }

    suspend fun getEvents(): List<Event> {
        val eventList = mutableListOf<Event>()

        deleteEvents()

        withContext(Dispatchers.IO) {
            val result =
                FirebaseUtil.getEventsRef().orderBy("date", Query.Direction.ASCENDING).get().await()

            for (document in result.documents) {
                eventList.add(document.toObject(Event::class.java)!!)
            }

        }

        return eventList
    }

    suspend fun addEvent(event: Event, onEventAdded: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val id = FirebaseUtil.getEventsRef().document().id
            event.eventId = id

            FirebaseUtil.getEventsRef().document(id).set(event).addOnSuccessListener {
                onEventAdded(true)
            }.addOnFailureListener {
                onEventAdded(false)
            }
        }
    }

    suspend fun getCompetitionInfo(id: String): Competition? {
        var comp: Competition?

        withContext(Dispatchers.IO) {
            val compDocu = FirebaseUtil.getCompetitionsRef().document(id).get().await()
            comp = compDocu.toObject(Competition::class.java)
        }

        return comp
    }
}