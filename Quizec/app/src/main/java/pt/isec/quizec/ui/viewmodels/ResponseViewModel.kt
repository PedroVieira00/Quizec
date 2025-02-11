package pt.isec.quizec.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.quizec.models.Response
import pt.isec.quizec.repository.ResponseRepository
import kotlin.random.Random

class ResponseViewModel(private val responseRepository: ResponseRepository): ViewModel() {
    val id = mutableStateOf("")
    val userId = mutableStateOf("")
    val questionId = mutableStateOf("")
    val questionnaireId = mutableStateOf("")
    var responsesInt = mutableStateListOf<Int>()
    var responsesString = mutableStateListOf<String>()

    private val _response = MutableStateFlow<List<Response>>(emptyList())
    val response: StateFlow<List<Response>> = _response

    private fun generateUniqueId(): String {
        val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..6)
            .map { Random.nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    fun getResponses(id: String = "", uid: String = "", qid: String = "", callback: (List<Response>) -> Unit) {
        responseRepository.getResponses(id, uid, qid, callback)
    }

    fun save() {
        Log.i("Response", " AQUI3 : ${id.value}")

        id.value = generateUniqueId()
        val response = Response(
            id.value,
            userId = userId.value,
            questionnaireId = questionnaireId.value,
            responsesInt = responsesInt
        )

        responseRepository.addResponse(
            response.id,
            response.userId,
            response.questionnaireId,
            response.responsesInt
        ) { documentId ->
            documentId?.let {
                id.value = it
                Log.i("Response", " AQUI 2: ${id.value}") // Log here, after the ID is updated
            } ?: run {
                Log.e("Response", "Failed to get document ID")
            }
        }
    }


    fun updateResponse(response: Response, callback: (Boolean) -> Unit){
        val updatedResponse = Response(
            id = response.id,
            userId = response.userId,
            questionnaireId = response.questionnaireId,
            questionId = questionId.value,
            responsesInt = response.responsesInt + responsesInt,
            responsesString = response.responsesString + responsesString
        )

        responseRepository.updateResponse(updatedResponse, callback)
    }

    fun addResponse(response: String) {
        responsesString.add(response)
    }
}