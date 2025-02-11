package pt.isec.quizec.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.R
import pt.isec.quizec.datasource.remote.QuestionDataSource
import pt.isec.quizec.datasource.remote.QuestionnaireDataSource
import pt.isec.quizec.datasource.remote.ResponseDataSource
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.models.Question
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.models.Response
import pt.isec.quizec.models.User
import pt.isec.quizec.repository.QuestionRepository
import pt.isec.quizec.repository.QuestionnaireRepository
import pt.isec.quizec.repository.ResponseRepository
import pt.isec.quizec.repository.UserRepository
import pt.isec.quizec.ui.components.NumberedInputs
import pt.isec.quizec.ui.screens.questions.BasicOptionsScreen
import pt.isec.quizec.ui.screens.questions.FillTheBlanks
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel
import pt.isec.quizec.ui.viewmodels.ResponseViewModel
import pt.isec.quizec.ui.viewmodels.UserViewModel

@Composable
fun Options(navController: NavHostController) {
    val context = LocalContext.current.applicationContext as QuizecApp
    val firebaseHelper = context.firebaseHelper

    val userViewModel: UserViewModel = viewModel { UserViewModel(UserRepository(UserDataSource(firebaseHelper))) }

    val responseViewModel: ResponseViewModel = viewModel { ResponseViewModel(ResponseRepository(ResponseDataSource(firebaseHelper))) }

    val questionnaireViewModel: QuestionnaireViewModel = viewModel {
        QuestionnaireViewModel(
            QuestionnaireRepository(QuestionnaireDataSource(firebaseHelper)),
            QuestionRepository(QuestionDataSource(firebaseHelper))
        )
    }

    val questionViewModel: QuestionViewModel = viewModel { QuestionViewModel(QuestionRepository(QuestionDataSource(firebaseHelper))) }

    val questionnaire = remember { mutableStateOf<Questionnaire?>(null) }
    val user = remember { mutableStateOf<User?>(null) }
    var text by remember { mutableStateOf("") }
    var questionnaireChosenId by remember { mutableStateOf("") }
    var questionnaireChosenIdTemp by remember { mutableStateOf("") }
    val questions = remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val response = remember { mutableStateOf<Response?>(null) }
    val cannotRespondMessage = remember { mutableIntStateOf(0) }

    LaunchedEffect(questionnaireChosenId) {
        if (questionnaireChosenId.isNotEmpty()) {
            questionnaireViewModel.getQuestionnaires(id = questionnaireChosenId) { fetchedQuestionnaire ->
                questionnaire.value = fetchedQuestionnaire.firstOrNull()

                if (questionnaire.value != null) {
                    if(questionnaire.value!!.usersInIds.contains(userViewModel.currentUser.value!!.uid)){
                        cannotRespondMessage.intValue = 1
                    }
                    if(questionnaire.value!!.uid == userViewModel.currentUser.value!!.uid){
                        cannotRespondMessage.intValue = 2
                    }
                    if(cannotRespondMessage.intValue == 0){
                        questionnaire.value?.questions?.let { questionIds ->
                            questionViewModel.getQuestionnaireQuestions(questionIds) { fetchedQuestions ->
                                questions.value = fetchedQuestions
                            }
                        }

                        userViewModel.getUserByUid(userViewModel.currentUser.value!!.uid) { fetchedUser ->
                            user.value = fetchedUser
                            if (user.value != null) {
                                responseViewModel.userId.value = user.value!!.uid
                                responseViewModel.questionnaireId.value = questionnaire.value!!.id
                                responseViewModel.save()

                                responseViewModel.getResponses(id = responseViewModel.id.value) { fetchedResponse ->
                                    response.value = fetchedResponse.firstOrNull()
                                    if (response.value != null && questionnaire.value != null) {
                                        questionnaireViewModel.responses.add(responseViewModel.id.value)
                                        questionnaireViewModel.updateQuestionnaire(
                                            questionnaire = questionnaire.value!!,
                                            responseId = responseViewModel.id.value,
                                            uid = user.value!!.uid
                                        ) {
                                            Log.i("Questionarioo", "Questionnaire updated successfully")
                                        }
                                    } else {
                                        Log.w("Questionarioo", "Response or Questionnaire is null")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (questionnaireChosenId.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("ID") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Button(
                    onClick = {
                        questionnaireChosenId = text
                        questionnaireChosenIdTemp = text
                    },
                    enabled = text.isNotEmpty()
                ) {
                    Text("Done")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (cannotRespondMessage.intValue == 1) {
                Text(
                    text = stringResource(R.string.cant_respond_again_message),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            } else if (cannotRespondMessage.intValue == 2) {
                Text(
                    text = stringResource(R.string.cant_respond_owner_message),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                if (currentQuestionIndex < questions.value.size) {
                    val currentQuestion = questions.value[currentQuestionIndex]

                    Text(
                        text = "Question ${currentQuestionIndex + 1}",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    if(currentQuestion.questionType != QuestionType.FILL_IN_THE_BLANK.name){
                        Text(
                            text = currentQuestion.question,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    questionViewModel.question.value = currentQuestion.question
                    questionViewModel.questionOptions.clear()
                    questionViewModel.questionOptions.addAll(currentQuestion.options)
                    questionViewModel.correctAnswers.clear()
                    questionViewModel.correctAnswers.addAll(currentQuestion.correctAnswers)
                    questionViewModel.fillInTheBlanksOptions.clear()
                    questionViewModel.fillInTheBlanksOptions.addAll(currentQuestion.fillInTheBlanksOptions)

                    val safeResponsesMatchingOrAssociation = remember { mutableStateListOf(*Array(questionViewModel.questionOptions.size / 2) { 0 }) }
                    val safeResponsesOrdering = remember { mutableStateListOf(*Array(questionViewModel.questionOptions.size) { 0 }) }

                    when(currentQuestion.questionType){
                        QuestionType.YES_NO.name ->
                            BasicOptionsScreen(
                                isInResponse = true,
                                responsesInt = responseViewModel.responsesInt,
                                options = questionViewModel.questionOptions,
                                correctOptions = questionViewModel.correctAnswers,
                                maxOptions = 2,
                                maxCorrectOptions = 1,
                                questionIndex = currentQuestionIndex
                            )
                        QuestionType.MULTIPLE_CHOICE_ONE_CORRECT.name ->
                            BasicOptionsScreen(
                                isInResponse = true,
                                responsesInt = responseViewModel.responsesInt,
                                options = questionViewModel.questionOptions,
                                correctOptions = questionViewModel.correctAnswers,
                                maxOptions = null,
                                maxCorrectOptions = 1,
                                questionIndex = currentQuestionIndex
                            )
                        QuestionType.MULTIPLE_CHOICE_MULTIPLE_CORRECT.name ->
                            BasicOptionsScreen(
                                isInResponse = true,
                                responsesInt = responseViewModel.responsesInt,
                                options = questionViewModel.questionOptions,
                                correctOptions = questionViewModel.correctAnswers,
                                questionIndex = currentQuestionIndex
                            )
                        QuestionType.MATCHING.name -> {
                            BasicOptionsScreen(
                                type = QuestionType.MATCHING.name,
                                options = questionViewModel.questionOptions,
                                questionIndex = currentQuestionIndex
                            )
                            NumberedInputs(
                                size = questionViewModel.questionOptions.size/2,
                                responses = safeResponsesMatchingOrAssociation
                            )
                        }
                        QuestionType.ORDERING.name -> {
                            BasicOptionsScreen(
                                type = QuestionType.ORDERING.name,
                                options = questionViewModel.questionOptions,
                                questionIndex = currentQuestionIndex
                            )
                            NumberedInputs(
                                type = QuestionType.ORDERING.name,
                                size = questionViewModel.questionOptions.size,
                                responses = safeResponsesOrdering
                            )
                        }
                        QuestionType.FILL_IN_THE_BLANK.name ->
                            FillTheBlanks(
                                type = QuestionType.FILL_IN_THE_BLANK.name,
                                correctAnswers = questionViewModel.correctAnswers,
                                isInResponse = true,
                                isEditable = false,
                                question = questionViewModel.question,
                                options = questionViewModel.fillInTheBlanksOptions,
                                onOptionSelectedString = { response ->
                                    responseViewModel.addResponse(response)
                                }
                            )
                        QuestionType.CONCEPT_ASSOCIATION.name -> {
                            BasicOptionsScreen(
                                type = QuestionType.CONCEPT_ASSOCIATION.name,
                                options = questionViewModel.questionOptions,
                                questionIndex = currentQuestionIndex
                            )
                            NumberedInputs(
                                size = questionViewModel.questionOptions.size/2,
                                responses = safeResponsesMatchingOrAssociation
                            )
                        }
                        QuestionType.WORD_RESPONSE.name -> {
                            FillTheBlanks(
                                type = QuestionType.FILL_IN_THE_BLANK.name,
                                correctAnswers = questionViewModel.correctAnswers,
                                question = questionViewModel.question,
                                isFreeText = true,
                                isEditable = false,
                                isInResponse = true,
                                options = questionViewModel.fillInTheBlanksOptions,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (currentQuestionIndex <= questions.value.size - 1) {
                                responseViewModel.questionId.value = currentQuestion.id

                                responseViewModel.responsesInt.clear()
                                if(currentQuestion.questionType == QuestionType.MATCHING.name || currentQuestion.questionType == QuestionType.CONCEPT_ASSOCIATION.name){
                                    safeResponsesMatchingOrAssociation.forEach { response ->
                                        responseViewModel.responsesInt.add(response)
                                    }
                                } else if (currentQuestion.questionType == QuestionType.WORD_RESPONSE.name) {
                                    responseViewModel.responsesString.clear()
                                    responseViewModel.responsesString.addAll(questionViewModel.fillInTheBlanksOptions)
                                    Log.i("Questionarioo1", "responsesString: ${responseViewModel.responsesString}")
                                } else {
                                    safeResponsesOrdering.forEach { response ->
                                        responseViewModel.responsesInt.add(response)
                                    }
                                }
                                responseViewModel.updateResponse(response.value!!) {
                                    responseViewModel.getResponses(id = responseViewModel.id.value) { fetchedResponse ->
                                        response.value = fetchedResponse.firstOrNull()
                                    }
                                }
                                responseViewModel.responsesInt.clear()
                                responseViewModel.responsesString.clear()
                                if(currentQuestionIndex != questions.value.size - 1){
                                    responseViewModel.save()
                                }
                                responseViewModel.getResponses(id = responseViewModel.id.value) { fetchedResponse ->
                                    response.value = fetchedResponse.firstOrNull()
                                }
                                questionnaireViewModel.updateQuestionnaire(
                                    questionnaire = questionnaire.value!!,
                                    responseId = response.value!!.id,
                                    uid = user.value!!.uid
                                ){
                                    questionnaireViewModel.getQuestionnaires(id = questionnaireChosenId) { fetchedQuestionnaire ->
                                        questionnaire.value = fetchedQuestionnaire.firstOrNull()
                                    }
                                }
                                currentQuestionIndex += 1
                            }
                        },
                        enabled = currentQuestionIndex <= questions.value.size - 1
                    ) {
                        if(currentQuestionIndex == questions.value.size - 1){
                            Text(stringResource(R.string.finish_label))
                        } else {
                            Text(stringResource(R.string.next_label))
                        }
                    }
                } else {
                    if (currentQuestionIndex != 0) {
                        Text(
                            text = stringResource(R.string.quiz_end),
                            fontSize = 24.sp,
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
