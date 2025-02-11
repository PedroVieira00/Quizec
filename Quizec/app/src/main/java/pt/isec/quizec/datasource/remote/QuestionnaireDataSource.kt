package pt.isec.quizec.datasource.remote

import android.util.Log
import com.google.firebase.firestore.FieldValue
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.util.firebase.FirebaseHelper

class QuestionnaireDataSource(private val firebaseHelper: FirebaseHelper) {
    companion object {
        private const val QUESTIONNAIRES = "questionnaires"
    }

    fun getQuestionnaires(uid: String, id: String, qid: String, creatorId: String, callback: (List<Questionnaire>) -> Unit) {
        val conditions = when {
            uid.isNotEmpty() && id.isNotEmpty() -> {
                mapOf("id" to id, "usersInIds" to uid)
            }
            uid.isNotEmpty() && id.isEmpty() -> {
                mapOf("usersInIds" to uid)
            }
            id.isNotEmpty() && uid.isEmpty() -> {
                mapOf("id" to id)
            }
            id.isEmpty() && uid.isEmpty() && qid.isNotEmpty() -> {
                mapOf("questions" to qid)
            }
            uid.isEmpty() && id.isEmpty() && qid.isEmpty() && creatorId.isNotEmpty() -> {
                mapOf("uid" to creatorId)
            }
            else -> null
        }

        firebaseHelper.getDocuments(
            collectionName = QUESTIONNAIRES,
            conditions = conditions,
            onSuccess = { documents ->
                val questionnaires = documents.mapNotNull { document ->
                    document.toObject(Questionnaire::class.java)
                }
                callback(questionnaires)
            },
            onFailure = { exception ->
                callback(emptyList())
                Log.i("FirestoreQuery", "Error fetching documents", exception)
            }
        )
    }


    fun addQuestionnaire(questionnaire: Questionnaire, callback: (String?) -> Unit) {
        firebaseHelper.addDocument(
            documentId = questionnaire.id,
            collectionName = QUESTIONNAIRES,
            data = questionnaire,
            onSuccess = { documentId ->
                callback(documentId)
            },
            onFailure = { exception ->
                callback(null)
            }
        )
    }

    fun updateQuestionnaire(
        id: String,
        newTitle: String? = null,
        newDescription: String? = null,
        newQuestionIds: List<String>? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>()
        newTitle?.let { updates["title"] = it }
        newDescription?.let { updates["description"] = it }
        newQuestionIds?.let { updates["questionIds"] = it }

        if (updates.isNotEmpty()) {
            firebaseHelper.updateDocument(
                collectionName = QUESTIONNAIRES,
                documentId = id,
                updates = updates,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun updateQuestionnaire(questionnaire: Questionnaire, callback: (Boolean) -> Unit) {
        firebaseHelper.updateDocumentWithSet(
            documentId = questionnaire.id,
            collectionName = QUESTIONNAIRES,
            data = questionnaire,
            onSuccess = { callback(true) },
            onFailure = { exception ->
                callback(false)
            }
        )
    }

    fun deleteQuestionnaire(questionnaireId: String, callback: (Boolean) -> Unit) {
        firebaseHelper.deleteDocument(
            collectionName = QUESTIONNAIRES,
            documentId = questionnaireId,
            onSuccess = { callback(true) },
            onFailure = { exception ->
                callback(false)
            }
        )
    }
}