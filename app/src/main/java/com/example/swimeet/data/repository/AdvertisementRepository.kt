package com.example.swimeet.data.repository

import com.example.swimeet.data.model.Advertisement
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class AdvertisementRepository {

    suspend fun addAdvertisement(adv: Advertisement, onAdvertisementAdded: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val id = FirebaseUtil.getAdvertisementsRef().document().id
            adv.advertisementId = id

            FirebaseUtil.getAdvertisementsRef().document(id).set(adv).addOnSuccessListener {
                onAdvertisementAdded(true)
            }.addOnFailureListener {
                onAdvertisementAdded(false)
            }
        }
    }

    suspend fun getAdvertisements(): List<Advertisement> {
        val advList = mutableListOf<Advertisement>()

        deleteAdvertisements()

        withContext(Dispatchers.IO) {
            val result =
                FirebaseUtil.getAdvertisementsRef().orderBy("date", Query.Direction.DESCENDING)
                    .get().await()

            for (document in result.documents) {
                advList.add(document.toObject(Advertisement::class.java)!!)
            }

        }

        return advList
    }

    suspend fun deleteAdvertisements() {
        val fechaActual = Calendar.getInstance()
        fechaActual.add(Calendar.DAY_OF_YEAR, -1)

        withContext(Dispatchers.IO) {
            val result =
                FirebaseUtil.getAdvertisementsRef().whereLessThanOrEqualTo("date", fechaActual.time)
                    .get().await()

            for (document in result.documents) {
                document.reference.delete()
            }
        }
    }
}