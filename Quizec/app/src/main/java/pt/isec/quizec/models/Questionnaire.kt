package pt.isec.quizec.models

import java.io.Serializable

data class Questionnaire(
    var id: String = "",
    var uid: String = "",
    var title: String = "",
    var description: String = "",
    var image: String? = null,
    var questions: List<String> = emptyList<String>(),
    var maxTime: Int? = null,
    var geoRestricted: Boolean = false,
    var usersInIds: List<String> = emptyList(),
    var responses: List<String> = emptyList()
) : Serializable
