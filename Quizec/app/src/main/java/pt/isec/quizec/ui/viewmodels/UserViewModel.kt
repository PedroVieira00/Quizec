package pt.isec.quizec.ui.viewmodels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.models.User
import pt.isec.quizec.repository.UserRepository
import kotlin.random.Random

class UserViewModel(private val userRepository: UserRepository): ViewModel() {
    val id = mutableStateOf("")
    val username = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")
    
    var currentUser = mutableStateOf<FirebaseUser?>(null)
        private set

    init {
        currentUser.value = userRepository.getCurrentUser()

        userRepository.addAuthStateListener { auth ->
            Log.i("currentUser", "UserViewModel before ${currentUser.value?.uid}")
            currentUser.value = auth.currentUser
            Log.i("currentUser", "UserViewModel after ${currentUser.value?.uid}")
        }


    }

    private fun generateUniqueId(): String {
        val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..6)
            .map { Random.nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    fun saveUser(): User {
        return User(
            id = generateUniqueId(),
            uid = "",
            username = username.value,
            email = email.value,
        )
    }

    fun save(user: User, password: String){
        val userIds = mutableListOf<String>()

        userRepository.addUser(
            user = user,
            callback = {documentId ->
                documentId?.let {
                    userIds.add(it)
                }
            },password
        )
    }

    fun getUser(callback: (String?) -> Unit){
        userRepository.getUser(
            email = email.value,
            password = password.value,
            callback = callback
        )
    }

    fun getCurrentUser(): FirebaseUser? {
        return userRepository.getCurrentUser()
    }

    fun logOut() {
        userRepository.logOut()
    }

    fun getUserByUid(uid: String, callback: (User?) -> Unit){
        userRepository.getUserByUid(uid, callback)
    }
}