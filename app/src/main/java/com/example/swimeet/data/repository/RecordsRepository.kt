package com.example.swimeet.data.repository

import com.example.swimeet.data.model.Record
import com.example.swimeet.util.FirebaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecordsRepository {

    suspend fun getRecords(event: String): List<Record> {
        var recordList = mutableListOf<Record>()

        withContext(Dispatchers.IO) {
            val result = FirebaseUtil.getRecordsRef()
                .whereEqualTo("isRecord", true)
                .whereEqualTo("swimEvent", event)
                .get().await()

            for (document in result.documents) {
                recordList.add(document.toObject(Record::class.java)!!)
            }
        }

        return recordList
    }

}
