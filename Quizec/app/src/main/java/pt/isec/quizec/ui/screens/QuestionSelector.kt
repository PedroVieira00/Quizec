package pt.isec.quizec.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.R
import pt.isec.quizec.datasource.remote.QuestionDataSource
import pt.isec.quizec.datasource.remote.QuestionnaireDataSource
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.repository.QuestionRepository
import pt.isec.quizec.repository.QuestionnaireRepository
import pt.isec.quizec.repository.UserRepository
import pt.isec.quizec.ui.screens.questionnaires.QuestionsSelectorScreen
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel
import pt.isec.quizec.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsSelector(navController: NavHostController){
    val context = LocalContext.current.applicationContext as QuizecApp
    val firebaseHelper = context.firebaseHelper

    val userViewModel : UserViewModel
            = viewModel { UserViewModel(UserRepository(UserDataSource(firebaseHelper))) }

    val questionViewModel : QuestionViewModel
            = viewModel { QuestionViewModel(QuestionRepository(QuestionDataSource(firebaseHelper))) }

    val questionnaireViewModel : QuestionnaireViewModel
            = viewModel { QuestionnaireViewModel(
        QuestionnaireRepository(QuestionnaireDataSource(firebaseHelper)),
        QuestionRepository(QuestionDataSource(firebaseHelper))
    ) }

    val selectedIds = remember { mutableStateListOf<String>() }
    //val questions = remember { mutableStateListOf<Question>() }

    val questions by questionViewModel.questions.collectAsState()

    LaunchedEffect(Unit) {
        questionViewModel.getQuestions( uid = userViewModel.currentUser.value!!.uid )
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
                title = { Text("Quizec") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("CreateQuestion")
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.save_button_description),
                        )

                    }
                }
            )
        }

    ) {
        QuestionsSelectorScreen(
            modifier = Modifier.padding(it),
            viewModel = questionnaireViewModel,
            onSelect = { selectedQuestionsIds ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("SelectedQuestionsIds", selectedQuestionsIds)
                Log.i("QuestionsSelector", "Selected Questions: ${selectedQuestionsIds.size}")

                navController.popBackStack()
            },
            questions = questions
        )
    }
}