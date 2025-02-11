package pt.isec.quizec.util.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.models.User

class FirebaseHelper(private val firestore: FirebaseFirestore, private val fireauth: FirebaseAuth) {

    fun addDocument(
        documentId: String,
        collectionName: String,
        data: Any,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.i("FirebaseHelper", "addDocument")
        firestore.collection(collectionName)
            .document(documentId)
            .set(data)
            .addOnSuccessListener {
                Log.i("FirebaseHelper", "success")
                onSuccess(documentId)
            }
            .addOnFailureListener { exception ->
                Log.i("FirebaseHelper", "failure")
                onFailure(exception)
            }
        Log.i("FirebaseHelper", "addDocument End")
    }

    /*fun signIn(
        email: String,
        password: String,
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("FirebaseHelper", "Starting sign-in for email: $email")

        fireauth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser == null) {
                    val exception = Exception("Firebase user is null after sign-in")
                    Log.e("FirebaseHelper", exception.message ?: "Unknown error")
                    onFailure(exception)
                    return@addOnSuccessListener
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseHelper", "Error signing in: ${exception.message}")
                onFailure(exception)
            }
    }*/

    fun signIn(
        email: String,
        password: String,
        onSuccess: (String?) -> Unit,
        onFailure:(Exception) -> Unit
    ) {
        fireauth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                onSuccess(authResult.user?.uid)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun createUser(
        data: User,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
        password: String
    ) {
        Log.d("FirebaseHelper", "Starting to create user with email: ${data.email}")
        fireauth.createUserWithEmailAndPassword(data.email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser == null) {
                    val exception = Exception("Firebase user is null after creation")
                    Log.e("FirebaseHelper", exception.message ?: "Unknown error")
                    onFailure(exception)
                    return@addOnSuccessListener
                }

                val updatedUser = data.copy(uid = firebaseUser.uid)

                val userMap = mapOf(
                    "uid" to updatedUser.uid,
                    "username" to updatedUser.username,
                    "email" to updatedUser.email
                )

                Log.d("FirebaseHelper", "Adding user data to Firestore...")
                addDocument(
                    documentId = data.id,
                    collectionName = "users",
                    data = userMap,
                    onSuccess = { documentId ->
                        Log.d("FirebaseHelper", "User data successfully saved with ID: $documentId")
                        onSuccess(documentId)
                    },
                    onFailure = { exception ->
                        Log.e("FirebaseHelper", "Error saving user data: ${exception.message}")
                        onFailure(exception)
                    }
                )
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseHelper", "Error creating Firebase Auth user: ${exception.message}")
                onFailure(exception)
            }
    }

    fun updateDocument(
        collectionName: String,
        documentId: String,
        updates: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection(collectionName)
            .document(documentId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun deleteDocument(
        collectionName: String,
        documentId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection(collectionName)
            .document(documentId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getDocuments(
        collectionName: String,
        conditions: Map<String, Any>? = null,
        onSuccess: (List<DocumentSnapshot>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val collectionRef = firestore.collection(collectionName)

        val query = conditions?.entries?.fold(collectionRef as Query) { acc, condition ->
            when {
                (condition.key == "usersInIds" && condition.value is String) || (condition.key == "questions" && condition.value is String)-> {
                    acc.whereArrayContains(condition.key, condition.value)
                }
                else -> {
                    acc.whereEqualTo(condition.key, condition.value)
                }
            }
        } ?: collectionRef

        query.get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FirestoreQuery", "Query succeeded. Found ${querySnapshot.size()} documents.")
                onSuccess(querySnapshot.documents)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreQuery", "Query failed: ${exception.message}", exception)
                onFailure(exception)
            }
    }

    fun listenToCollection(
        collectionName: String,
        onEvent: (List<DocumentSnapshot>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection(collectionName)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    onError(exception)
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    onEvent(querySnapshot.documents)
                }
            }
    }
    
    fun registerUser(
        email: String,
        password: String,
        onSuccess: (String?) -> Unit,
        onFailure:(Exception) -> Unit
    )
    {
        fireauth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                onSuccess(authResult.user?.uid)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun getCurrentUser(): FirebaseUser? {
        return fireauth.currentUser
    }

    fun logOut() {
        fireauth.signOut()
    }

    fun updateDocumentWithSet(
        documentId: String,
        collectionName: String,
        data: Any,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection(collectionName)
            .document(documentId)
            .set(data)
            .addOnSuccessListener {
                Log.e("firestoreUpdate", "Success")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("firestoreUpdate", "Error retrieving document: ${exception.message}")
                onFailure(exception)
            }
    }

    fun addAuthStateListener(listener: AuthStateListener){
        fireauth.addAuthStateListener(listener)
    }
}
