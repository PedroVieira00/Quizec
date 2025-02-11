package pt.isec.quizec.repository

import pt.isec.quizec.datasource.remote.ResponseDataSource
import pt.isec.quizec.models.Response

class ResponseRepository(private val responseDataSource: ResponseDataSource) {
    fun addResponse(
        id: String,
        uid: String,
        questionnaireId: String,
        responses: List<Int>,
        callback: (String?) -> Unit
    ) {
        val response: Response =
            Response(
                id = id,
                userId = uid,
                questionnaireId = questionnaireId,
                responsesInt = responses
            )

        responseDataSource.addResponse(response, callback)
    }

    fun updateResponse(response: Response, callback: (Boolean) -> Unit) {
        responseDataSource.updateResponse(
            response = response,
            callback = callback
        )
    }

    fun getResponses(id: String, uid: String, qid: String, callback: (List<Response>) -> Unit) {
        responseDataSource.getResponses(id, uid, qid, callback)
    }

}