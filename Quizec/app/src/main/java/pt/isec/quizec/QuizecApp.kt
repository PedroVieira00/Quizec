package pt.isec.quizec

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.quizec.util.firebase.FirebaseHelper

class QuizecApp : Application() {
    
    val firebaseHelper: FirebaseHelper by lazy {
        FirebaseHelper(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
    }

    companion object {
        private lateinit var instance: QuizecApp
        fun getInstance(): QuizecApp = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}