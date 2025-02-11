package pt.isec.quizec.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.R
import pt.isec.quizec.datasource.remote.QuestionDataSource
import pt.isec.quizec.datasource.remote.QuestionnaireDataSource
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.models.Question
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.repository.QuestionRepository
import pt.isec.quizec.repository.QuestionnaireRepository
import pt.isec.quizec.repository.UserRepository
import pt.isec.quizec.ui.components.FloatingButton
import pt.isec.quizec.ui.screens.hometabs.FloatingButtonConfig
import pt.isec.quizec.ui.screens.questionnaires.MainQuestionnaireScreen
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel
import pt.isec.quizec.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionnaire(navController: NavHostController){
    val context = LocalContext.current.applicationContext as QuizecApp
    val firebaseHelper = context.firebaseHelper

    val questionnaireViewModel : QuestionnaireViewModel
            = viewModel { QuestionnaireViewModel(
        QuestionnaireRepository(QuestionnaireDataSource(firebaseHelper)),
        QuestionRepository(QuestionDataSource(firebaseHelper))
    ) }

    val userViewModel : UserViewModel
            = viewModel { UserViewModel(UserRepository(UserDataSource(firebaseHelper))) }

    val formIsValid = questionnaireViewModel.title.value.isNotEmpty() && questionnaireViewModel.description.value.isNotEmpty()
            && (questionnaireViewModel.questions.isNotEmpty() || questionnaireViewModel.selectedQuestionsIds.value.isNotEmpty())

    val questionnaireId = navController.currentBackStackEntry?.arguments?.getString("questionnaireId")
    val questionnaire = remember { mutableStateOf<Questionnaire?>(null) }
    val gson = Gson()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val selectedQuestionsIds = savedStateHandle?.get<List<String>>("SelectedQuestionsIds")
    val questionJson = savedStateHandle?.get<String>("question")
    val question: Question? = questionJson?.let {
        gson.fromJson(it, Question::class.java)
    }

    LaunchedEffect(Unit) {
        selectedQuestionsIds?.let {
            questionnaireViewModel.updateSelectedQuestionsIds(it)
            savedStateHandle["SelectedQuestionsIds"] = null
        }
        question?.let {
            questionnaireViewModel.addQuestion(it)
            Log.i("Question", "Retrieved Question: ${it.title}")
            Log.i("Question", "Retrieved Question: ${it.options.size}")
            savedStateHandle["question"] = null
        }
        if(questionnaireId != null){
            questionnaireViewModel.getQuestionnaires(id = questionnaireId) { fetchedQuestionnaire ->
                questionnaire.value = fetchedQuestionnaire.firstOrNull()

                questionnaireViewModel.id.value = questionnaire.value?.id!!
                questionnaireViewModel.title.value = questionnaire.value?.title!!
                questionnaireViewModel.description.value = questionnaire.value?.description!!
                questionnaireViewModel.image.value = questionnaire.value!!.image.toString()
                questionnaireViewModel.maxTime.intValue = questionnaire.value!!.maxTime!!
                questionnaireViewModel.geoRestricted.value = questionnaire.value!!.geoRestricted
                questionnaireViewModel.selectedQuestionsIds.value = questionnaire.value!!.questions
            }
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                },
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(
                        onClick = {
                            questionnaireViewModel.uid.value = userViewModel.currentUser.value!!.uid
                            if(questionnaireId != null){
                                questionnaireViewModel.updateQuestionnaire(questionnaireViewModel.saveQuestionnaire()){  }
                            } else{
                                questionnaireViewModel.save()
                            }
                            navController.navigate("HomePage")
                        },
                        enabled = formIsValid
                    ) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = stringResource(R.string.save_button_description),
                        )

                    }
                }
            )
        },
        floatingActionButton = {
            FloatingButton(
                listOf(
                    FloatingButtonConfig(
                        label = stringResource(R.string.add_question_label),
                        icon = Icons.AutoMirrored.Default.List,
                        onClick = { navController.navigate("QuestionSelector") }
                    ),
                    FloatingButtonConfig(
                        label = stringResource(R.string.create_question_label),
                        icon = Icons.Default.Add,
                        onClick = { navController.navigate("CreateQuestion")}
                    )
                )
            )
        }
    ) {
        MainQuestionnaireScreen(
            questionnaireId = questionnaireId,
            modifier = Modifier.padding(it),
            viewModel = questionnaireViewModel,
        )
    }
}

@Preview
@Composable
fun CreateQuestionnairePreview(){
    val navController: NavHostController = rememberNavController()
    CreateQuestionnaire(navController)
}