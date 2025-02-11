package pt.isec.quizec.models

import java.io.Serializable

data class User(
    var id: String = "",
    var uid: String = "",
    val username: String = "",
    val email: String = "",
) : Serializable