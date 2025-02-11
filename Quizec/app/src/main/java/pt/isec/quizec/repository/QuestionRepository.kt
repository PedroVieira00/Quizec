package pt.isec.quizec.repository

import com.google.firebase.firestore.DocumentSnapshot
import pt.isec.quizec.datasource.remote.QuestionDataSource
import pt.isec.quizec.models.Question

class QuestionRepository(private val questionDataSource: QuestionDataSource) {

    fun getQuestions(id: String, uid:String, callback: (List<Question>) -> Unit) {
        questionDataSource.getQuestions(id, uid, callback)
    }

    fun addQuestion(question: Question, callback: (String?) -> Unit) {
        questionDataSource.addQuestion(question, callback)
    }

    fun addQuestion(
        id : String,
        uid : String,
        title : String,
        question : String,
        image : String,
        type : String,
        options : List<String>,
        correctAnswers: List<Int>,
        fillInTheBlanksOptions: List<String>,
        callback: (String?) -> Unit){

        val question: Question =
            Question(
                id = id,
                uid = uid,
                title = title,
                question = question,
                image = image,
                questionType = type,
                options = options,
                correctAnswers = correctAnswers,
                fillInTheBlanksOptions = fillInTheBlanksOptions
            )

        questionDataSource.addQuestion(question, callback)
    }

    fun updateQuestion(
        id: String,
        newTitle: String? = null,
        newDescription: String? = null,
        newOptions: List<String>? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        questionDataSource.updateQuestion(
            id,
            newTitle,
            newDescription,
            newOptions,
            onSuccess,
            onFailure
        )
    }

    fun createQuestion(
        id : String,
        uid : String,
        title : String,
        question : String,
        image : String,
        type : String,
        options : List<String>,
        correctAnswers: List<Int>,
        fillInTheBlanksOptions: List<String>
        ) : Question? {

        if (title.isEmpty() || question.isEmpty() || type.isEmpty())
            return null

        return Question(
            id = id,
            uid = uid,
            title = title,
            question = question,
            image = image,
            questionType = type,
            fillInTheBlanksOptions = fillInTheBlanksOptions,
            options = options,
            correctAnswers = correctAnswers
        )

    }

    fun observeQuestions(
        onEvent: (List<DocumentSnapshot>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        questionDataSource.observeQuestions(onEvent, onFailure)
    }

    fun getQuestionnaireQuestions(questionIds: List<String>, callback: (List<Question>) -> Unit) {
        questionDataSource.getQuestionnaireQuestions(questionIds, callback)
    }

    fun updateQuestion(question: Question, callback: (Boolean) -> Unit){
        questionDataSource.updateQuestion(
            question = question,
            callback = callback
        )
    }

    fun deleteQuestion(questionId: String, callback: (Boolean) -> Unit) {
        questionDataSource.deleteQuestion(questionId, callback)
    }
}