package pt.isec.quizec.repository

import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.models.User

class UserRepository(private val userDataSource: UserDataSource) {
    fun addUser(user: User, callback: (String?) -> Unit, password: String) {
        userDataSource.addUser(user, callback, password)
    }

    fun getUser(email: String, password: String, callback: (String?) -> Unit) {
        userDataSource.getUser(email, password, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return userDataSource.getCurrentUser()
    }

    fun logOut() {
        userDataSource.logOut()
    }

    fun getUserByUid(uid: String, callback: (User?) -> Unit){
        userDataSource.getUserByUid(uid, callback)
    }

    fun addAuthStateListener(listener: AuthStateListener){
        userDataSource.addAuthStateListener(listener)
    }
}