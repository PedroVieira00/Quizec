package pt.isec.quizec.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.R
import pt.isec.quizec.datasource.remote.QuestionDataSource
import pt.isec.quizec.datasource.remote.QuestionnaireDataSource
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.repository.QuestionRepository
import pt.isec.quizec.repository.QuestionnaireRepository
import pt.isec.quizec.repository.UserRepository
import pt.isec.quizec.ui.components.BottomBar
import pt.isec.quizec.ui.components.FloatingButton
import pt.isec.quizec.ui.screens.hometabs.HomeTab
import pt.isec.quizec.ui.screens.hometabs.ListQuestionnaires
import pt.isec.quizec.ui.screens.hometabs.ListQuestions
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel
import pt.isec.quizec.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController) {
    val context = QuizecApp.getInstance()
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

    val currentUser = userViewModel.getCurrentUser()
    val isUserLoggedIn = currentUser != null

    val isLoggedInState = remember { mutableStateOf(isUserLoggedIn) }
    val selectedTab = remember { mutableStateOf<HomeTab>(HomeTab.CreatedQuestionnaires) }

    LaunchedEffect(isLoggedInState.value) {  }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedTab.value.title) },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("Login")
                            userViewModel.logOut()
                        },
                        enabled = isUserLoggedIn
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.logout_description)
                        )

                    }
                }
            )
        },
        bottomBar = {
            BottomBar(onTabSelected = {selected -> selectedTab.value = selected})
        },
        floatingActionButton = {
                FloatingButton(selectedTab.value.floatingActions(navController))
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val userId = userViewModel.currentUser.value?.uid

            when (selectedTab.value) {
                HomeTab.CreatedQuestionnaires -> ListQuestionnaires(
                    tab = 1,
                    userId = userId ?: "",
                    questionnaireViewModel = questionnaireViewModel,
                    onCopyClick = { navController.navigate("HomePage") },
                    onEditClick = { questionnaireId ->
                        navController.navigate("CreateQuestionnaire/$questionnaireId")
                    },
                    onGetQuestionnaire = {questionnaireViewModel.getQuestionnaires(creatorId = userViewModel.currentUser.value!!.uid)}
                )
                HomeTab.ParticipatedQuestionnaires -> ListQuestionnaires(
                    userId = userId ?: "",
                    questionViewModel = questionViewModel,
                    questionnaireViewModel = questionnaireViewModel,
                    onCopyClick = {},
                    onEditClick = {},
                    onGetQuestionnaire = {
                        questionnaireViewModel.getQuestionnaires(
                            uid = userViewModel.currentUser.value!!.uid
                        )
                    }
                )
                HomeTab.UserQuestions -> ListQuestions(
                    navController = navController,
                    questionViewModel = questionViewModel,
                    questionnaireViewModel = questionnaireViewModel,
                    userViewModel = userViewModel,
                    onItemClick = { /*question ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle?.set("selectedQuestion", question)
                        navController.navigate("Question")
                        Log.i("ShowQuestion", "HomePage $question")*/
                        navController.navigate("PieChart/${it.id}")
                    },
                    onEditClick = { questionId ->
                        navController.navigate("CreateQuestion/$questionId")
                    }
                )
                HomeTab.Settings -> Options(navController)
            }

        }
    }
}
