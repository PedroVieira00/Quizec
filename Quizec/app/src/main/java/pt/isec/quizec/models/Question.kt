package pt.isec.quizec.models

import java.io.Serializable

data class Question(
    var id: String = "",
    var uid: String = "",
    val title: String = "",
    val question: String = "",
    val image: String? = null,
    val questionType: String? = null,
    val options: List<String> = emptyList<String>(),
    val correctAnswers: List<Int> = emptyList<Int>(),
    var responses: List<Int> = emptyList<Int>(),
    val fillInTheBlanksOptions: List<String> = emptyList<String>(),
) : Serializable

