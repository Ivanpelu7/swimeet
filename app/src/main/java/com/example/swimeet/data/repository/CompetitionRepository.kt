package com.example.swimeet.data.repository


import com.example.swimeet.data.model.Competition
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.firestore.Query

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

        withContext(Dispatchers.IO) {
            val result = FirebaseUtil.getCompetitionsRef().orderBy("date", Query.Direction.ASCENDING).get().await()

            for (document in result.documents) {
                competitionList.add(document.toObject(Competition::class.java)!!)
            }

        }

        return competitionList
    }
}