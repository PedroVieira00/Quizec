package pt.isec.quizec.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.isec.quizec.R
import pt.isec.quizec.models.Question
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.repository.QuestionRepository
import kotlin.random.Random

class QuestionViewModel(private val questionRepository: QuestionRepository): ViewModel() {
    val id = mutableStateOf("")
    val uid = mutableStateOf<String>("")
    val title = mutableStateOf("")
    val question = mutableStateOf("")
    val image = mutableStateOf<String>("")
    val type = mutableStateOf<String>("")
    var responses = mutableStateListOf<Int>()

    //Basic questions
    var questionOptions = mutableStateListOf<String>()
    var correctAnswers = mutableStateListOf<Int>()

    //options for fill in the blanks
    val fillInTheBlanksOptions = mutableStateListOf<String>()


    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions


    /*private val _questionCorrectAnswers = MutableStateFlow<List<String>>(emptyList())
    val questionCorrectAnswers: StateFlow<List<String>> = _questionCorrectAnswers

    private val _questionOptions = MutableStateFlow<List<String>>(emptyList())
    val questionOptions: StateFlow<List<String>> = _questionOptions*/

    private fun generateUniqueId(): String {
        val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..6)
            .map { Random.nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    fun setId(){
        id.value = generateUniqueId()
    }

    fun saveQuestion(): Question? {

        return questionRepository.createQuestion(
            id = id.value,
            uid = uid.value,
            title = title.value,
            question = question.value,
            image = image.value,
            type = type.value,
            options = questionOptions,
            correctAnswers = correctAnswers,
            fillInTheBlanksOptions = fillInTheBlanksOptions
        )
    }

    fun save(question: Question){
        val questionIds = mutableListOf<String>()

        questionRepository.addQuestion(
            question = question,
            callback = {documentId ->
                documentId?.let {
                    questionIds.add(it)
                }
            }
        )
    }

    fun save(){
        questionRepository.addQuestion(
            generateUniqueId(),
            uid.value,
            title.value,
            question.value,
            image.value,
            type.value,
            questionOptions,
            correctAnswers,
            fillInTheBlanksOptions,
            callback = {
            }
        )
    }

    fun getQuestionTypeString(questionType: QuestionType?): Int? {
        return when (questionType) {
            QuestionType.YES_NO -> R.string.question_type_yes_no
            QuestionType.MULTIPLE_CHOICE_ONE_CORRECT -> R.string.question_type_multiple_choice_one_correct
            QuestionType.MULTIPLE_CHOICE_MULTIPLE_CORRECT -> R.string.question_type_multiple_choice_multiple_correct
            QuestionType.MATCHING -> R.string.question_type_matching
            QuestionType.ORDERING -> R.string.question_type_ordering
            QuestionType.FILL_IN_THE_BLANK -> R.string.question_type_fill_in_the_blank
            QuestionType.CONCEPT_ASSOCIATION -> R.string.question_type_concept_association
            QuestionType.WORD_RESPONSE -> R.string.question_type_word_response
            null -> null//R.string.question_type_unknown
        }
    }

    fun updateOptions(option: String) {
        questionOptions.add(option)
    }

    fun updateCorrectAnswers(option: Int) {
        correctAnswers.add(option)
    }

    fun updateOptions(options: List<String>) {
        questionOptions.addAll(options)
    }

    fun updateCorrectAnswers(correctAnswers: List<Int>) {
        this.correctAnswers.addAll(correctAnswers)
    }

    fun getQuestions(id: String = "", uid: String = "", callback: (List<Question>) -> Unit) {
        questionRepository.getQuestions(id, uid, callback)
    }

    fun getQuestions(id: String = "", uid: String) {
        viewModelScope.launch{
            questionRepository.getQuestions(id, uid) { fetchedQuestionnaires ->
                _questions.value = fetchedQuestionnaires
            }
        }
    }

    fun getQuestionnaireQuestions(questionIds: List<String>, callback: (List<Question>) -> Unit) {
        questionRepository.getQuestionnaireQuestions(questionIds, callback)
    }

    fun updateQuestion(question: Question, callback: (Boolean) -> Unit) {
        val updatedQuestion = Question(
            id = question.id,
            uid = question.uid,
            title = question.title,
            question = question.question,
            image = question.image,
            questionType = question.questionType,
            options = question.options,
            correctAnswers = question.correctAnswers,
            /*question.responses + responses*/
        )

        questionRepository.updateQuestion(updatedQuestion, callback)
    }

    fun updateFillInTheBlanksOptions(placeholder: String, options: List<String>) {
        // Flatten options and save as placeholder:option format
        options.forEach { option ->
            fillInTheBlanksOptions.add("$placeholder:$option")
        }
    }

    fun restoreFromQuestion(question: Question) {
        //ID
        //UID
        title.value = question.title
        this.question.value = question.question
        image.value = question.image ?: ""
        type.value = question.questionType ?: ""
        questionOptions.addAll(question.options)
        correctAnswers.addAll(question.correctAnswers)
        question.fillInTheBlanksOptions.forEach { option ->
            val parts = option.split(":")
            if (parts.size == 2) {
                val placeholder = parts[0]
                val optionValue = parts[1]
                updateFillInTheBlanksOptions(placeholder, listOf(optionValue))
            }
        }
    }

    fun deleteQuestion(questionId: String, callback: (Boolean) -> Unit) {
        questionRepository.deleteQuestion(questionId, callback)
    }

}