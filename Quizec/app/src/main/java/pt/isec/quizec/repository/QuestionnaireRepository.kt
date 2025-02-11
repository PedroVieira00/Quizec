package pt.isec.quizec.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import pt.isec.quizec.datasource.remote.QuestionnaireDataSource
import pt.isec.quizec.models.Questionnaire

class QuestionnaireRepository(private val questionnaireDataSource: QuestionnaireDataSource) {

    fun getQuestionnaires(uid: String, id: String, qid: String, creatorId: String, callback: (List<Questionnaire>) -> Unit) {
        questionnaireDataSource.getQuestionnaires(uid = uid, id = id, qid = qid, creatorId =  creatorId, callback = callback)
    }

    fun addQuestionnaire(
        id: String,
        uid: String,
        title: String,
        description: String,
        image: String,
        questions: List<String>,
        maxTime: Int,
        geoRestricted: Boolean,
        callback: (String?) -> Unit) {

        val questionnaire: Questionnaire =
            Questionnaire(
                id = id,
                uid = uid,
                title = title,
                description = description,
                image = image,
                questions = questions,
                maxTime = maxTime,
                geoRestricted = geoRestricted
            )

        questionnaireDataSource.addQuestionnaire(questionnaire, callback)
    }

    fun updateQuestionnaire(questionnaire: Questionnaire, calback: (Boolean) -> Unit) {
        questionnaireDataSource.updateQuestionnaire(
            questionnaire = questionnaire,
            callback = calback
        )
    }

    fun createQuestionnaire(
        id: String,
        uid: String,
        title: String,
        description: String,
        image: String,
        questions: List<String>,
        maxTime: Int,
        geoRestricted: Boolean,
    ): Questionnaire {

        return Questionnaire(
            id = id,
            uid = uid,
            title = title,
            description = description,
            image = image,
            questions = questions,
            maxTime = maxTime,
            geoRestricted = geoRestricted,
        )
    }

    fun deleteQuestionnaire(questionnaireId: String, callback: (Boolean) -> Unit) {
        questionnaireDataSource.deleteQuestionnaire(questionnaireId, callback)
    }
}