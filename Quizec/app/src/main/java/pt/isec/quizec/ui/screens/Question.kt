package pt.isec.quizec.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.R
import pt.isec.quizec.datasource.remote.QuestionDataSource
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.models.Question
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.repository.QuestionRepository
import pt.isec.quizec.repository.UserRepository
import pt.isec.quizec.ui.screens.questions.MainQuestionScreen
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Question(
    navController: NavHostController,
    onSaveQuestion: (QuestionViewModel) -> Unit,
    isEditable: Boolean = true
){

    val context = LocalContext.current.applicationContext as QuizecApp
    val firebaseHelper = context.firebaseHelper

    val questionViewModel: QuestionViewModel
            = viewModel {
        QuestionViewModel(QuestionRepository(QuestionDataSource(firebaseHelper)))
    }

    val userViewModel : UserViewModel
            = viewModel { UserViewModel(UserRepository(UserDataSource(firebaseHelper))) }

    val question = remember { mutableStateOf<Question?>(null) }
    val questionId = navController.currentBackStackEntry?.arguments?.getString("questionId")
    val backStackEntry = navController.previousBackStackEntry
    val savedStateHandle = backStackEntry?.savedStateHandle
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if(questionId != null){
            questionViewModel.getQuestions(id = questionId){ fetchedQuestion ->
                fetchedQuestion.firstOrNull()?.let { question ->
                    questionViewModel.restoreFromQuestion(question)
                    questionViewModel.id.value = question.id
                    questionViewModel.uid.value = question.uid
                }
            }
        }else{
            val savedQuestion = savedStateHandle?.get<Question>("selectedQuestion")
            savedQuestion?.let {
                questionViewModel.restoreFromQuestion(it)
            }
        }
    }

    Log.i("Question12", "type: ${questionViewModel.type.value}")
    Log.i("Question12", "options size: ${questionViewModel.questionOptions.size}")

    val formIsValid = questionViewModel.title.value.isNotEmpty()
            && questionViewModel.question.value.isNotEmpty()
            && questionViewModel.type.value.isNotEmpty()

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
                title = {
                    if(questionId != null){
                        Text(stringResource(R.string.edit_question_label))

                    } else{
                        Text(stringResource(R.string.create_question_label))
                    }
                        },
                actions = {
                    IconButton(
                        onClick = {
                            if(questionViewModel.type.value == QuestionType.MATCHING.name || questionViewModel.type.value == QuestionType.CONCEPT_ASSOCIATION.name || questionViewModel.type.value == QuestionType.ORDERING.name){
                                if(questionViewModel.questionOptions.size <= 2){
                                    CoroutineScope(Dispatchers.Main).launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.cant_create_question_too_few_options),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } else {
                                    if(questionViewModel.type.value == QuestionType.MATCHING.name || questionViewModel.type.value == QuestionType.CONCEPT_ASSOCIATION.name){
                                        if(questionViewModel.questionOptions.size % 2 == 0){
                                            setCorrectAnswers(questionViewModel)
                                            onSaveQuestion(questionViewModel)
                                        }
                                        else {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.cant_create_question_even_options),
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    } else {
                                        setCorrectAnswers(questionViewModel)
                                        onSaveQuestion(questionViewModel)
                                    }
                                }
                            } else {
                                onSaveQuestion(questionViewModel)
                            }
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
        }
    ) {
        MainQuestionScreen(
            modifier = Modifier.padding(it),
            questionViewModel = questionViewModel,
            isEditable = isEditable
        )

        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

    }
}

fun setCorrectAnswers(questionViewModel: QuestionViewModel){
    val size = questionViewModel.questionOptions.size / 2

    questionViewModel.correctAnswers.clear()

    for (i in 0 until size) {
        questionViewModel.correctAnswers.add(size + i)
    }
}