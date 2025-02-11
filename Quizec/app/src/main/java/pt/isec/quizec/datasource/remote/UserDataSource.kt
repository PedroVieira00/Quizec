package pt.isec.quizec.datasource.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import pt.isec.quizec.models.User
import pt.isec.quizec.util.firebase.FirebaseHelper

class UserDataSource(private val firebaseHelper: FirebaseHelper) {
    companion object {
        private const val USERS = "users"
    }

    fun addUser(
        user: User,
        callback: (String?) -> Unit,
        password: String
    ) {
        /*firebaseHelper.createUser(
            data = user,
            onSuccess = { documentId ->
                callback(documentId)
            },
            onFailure = {
                callback(null)
            },
            password
        )*/
        firebaseHelper.registerUser(
            email = user.email,
            password = password,
            onSuccess = { uid ->
                uid?.let { Log.i("uid", it) }
                if(uid != null){
                    user.uid = uid
                    firebaseHelper.addDocument(
                        documentId = user.id,
                        collectionName = USERS,
                        data = user,
                        onSuccess = {documentId -> callback(documentId)},
                        onFailure = {exception -> callback(null)}
                    )
                }
            },
            onFailure = {
                callback(null)
            }
        )
    }

    fun getUser(email: String, password: String, callback: (String?) -> Unit) {
        firebaseHelper.signIn(
            email= email,
            password = password,
            onSuccess = {documentId -> callback(documentId)},
            onFailure = { exception -> callback(null) }
        )
    }

    /*fun signIn(
        email: String,
        password: String,
        callback: (User?) -> Unit
    ) {
        firebaseHelper.signIn(
            email = email,
            password = password,
            onSuccess = { user ->
                callback(user)
            },
            onFailure = { exception ->
                callback(null)
            }
        )
    }*/

    fun getCurrentUser(): FirebaseUser? {
        return firebaseHelper.getCurrentUser()
    }

    fun logOut() {
        firebaseHelper.logOut()
    }

    fun getUserByUid(uid: String, callback: (User?) -> Unit){
        firebaseHelper.getDocuments(
            collectionName = USERS,
            conditions = mapOf("uid" to uid),
            onSuccess = { documents ->
                val firstQuestionnaire = documents.firstOrNull()?.toObject(User::class.java)
                callback(firstQuestionnaire)
            },
            onFailure = { exception ->
                callback(null)
            }
        )
    }

    fun addAuthStateListener(listener: AuthStateListener){
        firebaseHelper.addAuthStateListener(listener)
    }
}
