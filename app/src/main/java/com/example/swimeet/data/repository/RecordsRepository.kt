package com.example.swimeet.data.repository

import com.example.swimeet.data.model.Mark
import com.example.swimeet.data.model.Record
import com.example.swimeet.util.FirebaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecordsRepository {

    suspend fun getRecords(event: String): List<Record> {
        var recordList = mutableListOf<Record>()


        val result = FirebaseUtil.getRecordsRef()
            .whereEqualTo("swimEvent", event)
            .get().await()

        for (document in result.documents) {
            recordList.add(document.toObject(Record::class.java)!!)
        }


        return recordList
    }

    fun getRecord(category: String, swimEvent: String, genre: String, onComplete: (Long) -> Unit) {
        var time: Long = 0

        FirebaseUtil.getRecordsRef()
            .whereEqualTo("category", category)
            .whereEqualTo("swimEvent", swimEvent)
            .whereEqualTo("genre", genre)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (doc in it.result.documents) {
                        time = doc.get("time") as Long
                    }
                }

                onComplete(time)
            }
    }

    fun addMark(mark: Mark, genre: String, category: String, username: String, userId: String) {
        val record = Record(
            userId,
            username,
            mark.mark,
            mark.swimEvent,
            category,
            genre
        )
        FirebaseUtil.getRecordsRef().add(record)
    }

    suspend fun updateRecord(
        mark: Mark,
        genre: String,
        category: String,
        username: String,
        userId: String
    ) {
        val record = Record(
            userId,
            username,
            mark.mark,
            mark.swimEvent,
            category,
            genre
        )

        val result = FirebaseUtil.getRecordsRef()
            .whereEqualTo("category", category)
            .whereEqualTo("swimEvent", mark.swimEvent)
            .whereEqualTo("genre", genre)
            .get().await()

        for (doc in result) {
            val docRef = doc.reference
            docRef.set(record)
        }
    }

}
