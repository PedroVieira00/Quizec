package pt.isec.quizec.datasource.remote

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import pt.isec.quizec.models.Question
import pt.isec.quizec.util.firebase.FirebaseHelper

class QuestionDataSource(private val firebaseHelper: FirebaseHelper) {
    companion object {
        private const val QUESTION = "questions"
    }

    fun addQuestion(question: Question, callback: (String?) -> Unit) {
        firebaseHelper.addDocument(
            documentId = question.id,
            collectionName = QUESTION,
            data = question,
            onSuccess = { documentId ->
                callback(documentId)
            },
            onFailure = { exception ->
                callback(null)
            }
        )
    }

    fun getQuestions(id: String, uid: String, callback: (List<Question>) -> Unit) {
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
            else -> null
        }

        firebaseHelper.getDocuments(
            collectionName = QUESTION,
            conditions = conditions,
            onSuccess = { documents ->
                val questions = documents.mapNotNull { document ->
                    document.toObject(Question::class.java)
                }
                callback(questions)
            },
            onFailure = { exception ->
                callback(emptyList())
                Log.i("FirestoreQuery", "Error fetching documents", exception)
            }
        )
    }

    fun updateQuestion(
        id: String,
        newTitle: String? = null,
        newDescription: String? = null,
        newOptions: List<String>? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>()
        newTitle?.let { updates["title"] = it }
        newDescription?.let { updates["description"] = it }
        newOptions?.let { updates["options"] = it }

        if (updates.isNotEmpty()) {
            firebaseHelper.updateDocument(
                collectionName = QUESTION,
                documentId = id,
                updates = updates,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun observeQuestions(onEvent: (List<DocumentSnapshot>) -> Unit,
                         onFailure: (Exception) -> Unit) {
        firebaseHelper.listenToCollection(QUESTION, onEvent, onFailure)
    }

    fun getQuestionnaireQuestions(questionIds: List<String>, callback: (List<Question>) -> Unit) {
        if (questionIds.isEmpty()) {
            callback(emptyList())
            return
        }

        val allQuestions = mutableListOf<Question>()
        var completedQueries = 0

        questionIds.forEach { questionId ->
            firebaseHelper.getDocuments(
                collectionName = QUESTION,
                conditions = mapOf("id" to questionId),
                onSuccess = { documents ->
                    val questions = documents.mapNotNull { document ->
                        document.toObject(Question::class.java)
                    }
                    allQuestions.addAll(questions)

                    completedQueries++
                    if (completedQueries == questionIds.size) {
                        callback(allQuestions)
                    }
                },
                onFailure = { exception ->
                    Log.e("QuestionFetch", "Error fetching question $questionId: ${exception.message}", exception)
                    completedQueries++
                    if (completedQueries == questionIds.size) {
                        callback(allQuestions)
                    }
                }
            )
        }
    }

    fun updateQuestion(question: Question, callback: (Boolean) -> Unit) {
        firebaseHelper.updateDocumentWithSet(
            documentId = question.id,
            collectionName = QUESTION,
            data = question,
            onSuccess = { callback(true) },
            onFailure = { exception ->
                callback(false)
            }
        )
    }

    fun deleteQuestion(questionId: String, callback: (Boolean) -> Unit) {
        firebaseHelper.deleteDocument(
            documentId = questionId,
            collectionName = QUESTION,
            onSuccess = { callback(true) },
            onFailure = { exception ->
                callback(false)
            }
        )
    }
}