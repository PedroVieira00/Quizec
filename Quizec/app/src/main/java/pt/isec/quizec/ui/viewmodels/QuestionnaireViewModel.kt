package pt.isec.quizec.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.isec.quizec.models.Question
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.repository.QuestionRepository
import pt.isec.quizec.repository.QuestionnaireRepository
import kotlin.random.Random

class QuestionnaireViewModel(private val questionnaireRepository: QuestionnaireRepository,
                             private val questionRepository: QuestionRepository): ViewModel() {

    val uid = mutableStateOf<String>("")
    val id = mutableStateOf("")
    val title = mutableStateOf("")
    val description = mutableStateOf("")
    val image = mutableStateOf<String>("")
    val selectedQuestionsIds = mutableStateOf<List<String>>(emptyList())
    val questions = mutableStateListOf<Question>()
    val maxTime = mutableIntStateOf(0)
    val geoRestricted = mutableStateOf(false)

    var responses = mutableStateListOf<String>()

    private val _questionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val questionnaires: StateFlow<List<Questionnaire>> = _questionnaires



    /*private val _questionCorrectAnswers = MutableStateFlow<List<String>>(emptyList())
    val questionCorrectAnswers: StateFlow<List<String>> = _questionCorrectAnswers

    private val _questionOptions = MutableStateFlow<List<String>>(emptyList())
    val questionOptions: StateFlow<List<String>> = _questionOptions*/


    fun addQuestion(newQuestion: Question) {
        Log.i("Test", "$newQuestion")
        questions.add(newQuestion)
    }
    
    fun updateSelectedQuestionsIds(selectedQuestions: List<String>) {
        selectedQuestionsIds.value = selectedQuestions
    }

    fun updateAvailableQuestions(questions: List<Question>) {
        this.questions.addAll(questions)
    }

    fun getQuestionIds(): List<String> {
        return questions.map { it.id }
    }

    private fun generateUniqueId(): String {
        val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..6)
            .map { Random.nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    fun saveQuestionnaire() : Questionnaire{
        return questionnaireRepository.createQuestionnaire(
            id = id.value,
            uid = uid.value,
            title = title.value,
            description = description.value,
            image = image.value,
            questions = selectedQuestionsIds.value,
            maxTime = maxTime.intValue,
            geoRestricted = geoRestricted.value
        )
    }

    //It has to have this remainingQuestions cuz otherwise it does not guarantee that all
    //the questions are added before proceeding
    //We can use async...
    fun save() {
        val questionIds = mutableListOf<String>()

        if (questions.isEmpty()) {
            questionnaireRepository.addQuestionnaire(
                generateUniqueId(),
                uid.value,
                title.value,
                description.value,
                image.value,
                selectedQuestionsIds.value,
                maxTime.value,
                geoRestricted.value,
                callback = {
                }
            )
            return
        }

        var remainingQuestions = questions.size

        questions.forEach { question ->

            questionRepository.addQuestion(
                question = question,
                callback = { documentId ->
                    documentId?.let {
                        questionIds.add(it)
                    }

                    remainingQuestions--

                    if (remainingQuestions == 0) {
                        updateSelectedQuestionsIds(selectedQuestionsIds.value + questionIds)

                        questionnaireRepository.addQuestionnaire(
                            generateUniqueId(),
                            uid.value,
                            title.value,
                            description.value,
                            image.value,
                            selectedQuestionsIds.value,
                            maxTime.value,
                            geoRestricted.value,
                            callback = {
                            }
                        )
                    }
                }
            )
        }
    }

    /*fun addQuestions(selectedQuestions: List<Question>) {
        selectedQuestions.forEach { question ->
            Log.i("Test", "$question")
            Log.i("Test", "${this.questions.any { it.id == question.id }}")
            Log.i("Test", "${this.questions.isEmpty()}")
            if (!this.questions.any { it.id == question.id } || this.questions.isEmpty()) {
                Log.i("Test", "Dentro :${question}")
                this.addQuestion(question)
            }
        }
    }*/

    fun getQuestionnaires(uid: String = "", id: String = "", qid: String = "", creatorId: String = "", callback: (List<Questionnaire>) -> Unit) {
        questionnaireRepository.getQuestionnaires(uid = uid, id = id, qid = qid, creatorId =  creatorId, callback = callback)
    }

    fun getQuestionnaires(uid: String = "", id: String = "", qid: String = "", creatorId: String = "") {
        viewModelScope.launch{
            questionnaireRepository.getQuestionnaires(uid, id, qid, creatorId) { fetchedQuestionnaires ->
                _questionnaires.value = fetchedQuestionnaires
            }
        }
    }

    fun updateQuestionnaire(questionnaire: Questionnaire, uid: String = "", responseId: String = "", qid: String = "", callback: (Boolean) -> Unit) {
        val updatedUsersInIds = if (uid.isNotEmpty() && uid !in questionnaire.usersInIds) {
            questionnaire.usersInIds + uid
        } else {
            questionnaire.usersInIds
        }
        val updatedResponses = if (responseId.isNotEmpty()) {
            questionnaire.responses + responseId
        } else {
            questionnaire.responses
        }
        val updatedQuestions = if (qid.isNotEmpty()) {
            questionnaire.questions - qid
        } else {
            questionnaire.questions
        }

        val updatedQuestionnaire = Questionnaire(
            questionnaire.id,
            questionnaire.uid,
            questionnaire.title,
            questionnaire.description,
            questionnaire.image,
            updatedQuestions,
            questionnaire.maxTime,
            questionnaire.geoRestricted,
            updatedUsersInIds,
            updatedResponses
        )

        questionnaireRepository.updateQuestionnaire(updatedQuestionnaire, callback)
    }

    fun deleteQuestionnaire(questionnaireId: String, callback: (Boolean) -> Unit) {
        questionnaireRepository.deleteQuestionnaire(questionnaireId, callback)
    }
}