package pt.isec.quizec.models

import java.io.Serializable

data class Response (
    var id: String = "",
    var userId: String = "",
    var questionId: String = "",
    var questionnaireId: String = "",
    var responsesInt: List<Int> = emptyList<Int>(),
    var responsesString: List<String> = emptyList<String>()
) : Serializable