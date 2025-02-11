package pt.isec.quizec.datasource.remote

import android.util.Log
import pt.isec.quizec.models.Response
import pt.isec.quizec.util.firebase.FirebaseHelper

class ResponseDataSource(private val firebaseHelper: FirebaseHelper) {
    companion object {
        private const val RESPONSE = "responses"
    }

    fun addResponse(response: Response, callback: (String?) -> Unit) {
        firebaseHelper.addDocument(
            documentId = response.id,
            collectionName = RESPONSE,
            data = response,
            onSuccess = { documentId ->
                callback(documentId)
            },
            onFailure = { exception ->
                callback(null)
            }
        )
    }

    fun updateResponse(response: Response, callback: (Boolean) -> Unit) {
        firebaseHelper.updateDocumentWithSet(
            documentId = response.id,
            collectionName = RESPONSE,
            data = response,
            onSuccess = { callback(true) },
            onFailure = { exception ->
                callback(false)
            }
        )
    }

    fun getResponses(id: String, uid: String, qid: String, callback: (List<Response>) -> Unit) {
        val conditions = when {
            uid.isNotEmpty() && id.isNotEmpty() -> {
                mapOf("id" to id, "uid" to uid)
            }
            uid.isNotEmpty() && id.isEmpty() -> {
                mapOf("uid" to uid)
            }
            id.isNotEmpty() && uid.isEmpty() -> {
                mapOf("id" to id)
            }
            id.isEmpty() && uid.isEmpty() && qid.isNotEmpty() -> {
                mapOf("questionId" to qid)
            }
            else -> null
        }

        firebaseHelper.getDocuments(
            collectionName = RESPONSE,
            conditions = conditions,
            onSuccess = { documents ->
                val responses = documents.mapNotNull { document ->
                    document.toObject(Response::class.java)
                }
                callback(responses)
            },
            onFailure = { exception ->
                callback(emptyList())
                Log.i("FirestoreQuery", "Error fetching documents", exception)
            }
        )
    }

}