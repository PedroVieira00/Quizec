package pt.isec.quizec.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.models.Question
import pt.isec.quizec.repository.UserRepository
import pt.isec.quizec.ui.components.PieChart
import pt.isec.quizec.ui.screens.CreateQuestionnaire
import pt.isec.quizec.ui.screens.HomePage
import pt.isec.quizec.ui.screens.Login
import pt.isec.quizec.ui.screens.Options
import pt.isec.quizec.ui.screens.Question
import pt.isec.quizec.ui.screens.QuestionsSelector
import pt.isec.quizec.ui.screens.Register
import pt.isec.quizec.ui.viewmodels.UserViewModel
import pt.isec.quizec.ui.screens.RespondQuestionnaire

@Composable
fun QuizecNavGraph(navController: NavHostController = rememberNavController()){
    val context = QuizecApp.getInstance()
    val firebaseHelper = context.firebaseHelper
    val userViewModel : UserViewModel
            = viewModel { UserViewModel(UserRepository(UserDataSource(firebaseHelper))) }

    val isLoggedIn = remember { mutableStateOf(userViewModel.currentUser.value != null) }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn.value) "HomePage" else "Login" 
    ){
        composable("CreateQuestion"){
            val previousRoute = navController.previousBackStackEntry?.destination?.route

            if(previousRoute == "HomePage"){
                /*CreateQuestion(
                    navController = navController,
                    onSaveQuestion = {
                        navController.popBackStack()
                    },
                    before = previousRoute
                )*/
                Question(navController = navController,
                    onSaveQuestion = { viewModel ->
                        viewModel.uid.value = userViewModel.currentUser.value!!.uid
                        viewModel.save()
                        navController.popBackStack()
                    },
                    isEditable = true
                )
            }
            else{
                /*CreateQuestion(
                    navController = navController,
                    onSaveQuestion = { question ->

                        val gson = Gson()
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set("question", gson.toJson(question))

                        navController.popBackStack()
                    }
                )*/
                Question(navController = navController,
                    onSaveQuestion = { viewModel ->
                        viewModel.setId()
                        viewModel.saveQuestion()?.let { question ->
                            val gson = Gson()
                            navController.previousBackStackEntry
                                ?.savedStateHandle?.set("question", gson.toJson(question))
                        }
                        navController.popBackStack()
                    },
                    isEditable = true
                )
            }
            /*when(backStackEntry.destination.route)
            {
                "CreateQuestionnaire" -> {
                    CreateQuestion(
                        navController = navController,
                        onSaveQuestion = { question ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set<Question>("newQuestion", question)
                        }
                    )
                }
            }*/

        }
        composable("CreateQuestion/{questionId}"){
            val questionId = navController.currentBackStackEntry?.arguments?.getString("questionId")
            var question : Question? = null
            Question(navController = navController,
                onSaveQuestion = { viewModel ->
                    Log.i("CreateQuestion", "questionId: $questionId")
                    /*questionId?.let {
                        viewModel.getQuestions(id = it){ questions ->
                            question = questions.firstOrNull()
                            Log.i("CreateQuestion", "question: ${question?.id}")
                            question?.let {
                                //viewModel.restoreFromQuestion(it)
                                viewModel.updateQuestion(it){}
                            }
                        }
                    }

                    //viewModel.restoreFromQuestion(question)
                    */
                    viewModel.saveQuestion()?.let { it1 -> viewModel.updateQuestion(it1) { } }
                    navController.popBackStack()
                },
                isEditable = true
            )
            /*CreateQuestion(
                navController = navController,
                onSaveQuestion = {
                    navController.popBackStack()
                },
            )*/
        }
        composable("CreateQuestionnaire"){
            CreateQuestionnaire(navController = navController)
        }
        composable("CreateQuestionnaire/{questionnaireId}"){
            CreateQuestionnaire(navController = navController)
        }
        composable("QuestionSelector"){
            QuestionsSelector(navController = navController)
        }
        composable("HomePage") {
            HomePage(navController = navController)
        }
        composable("Login") {
            Login(navController = navController)
        }
        composable("Register") {
            Register(navController = navController)
        }
        composable("Options") {
            Options(navController = navController)
        }
        composable("RespondQuestionnaire") {
            RespondQuestionnaire(navController = navController)
        }
        composable("PieChart/{questionId}") {
            PieChart(navController = navController)
        }
        composable("Question") {
            /*val backStackEntry = navController.previousBackStackEntry
            val savedStateHandle = backStackEntry?.savedStateHandle
            val savedQuestion = savedStateHandle?.get<Question>("selectedQuestion")
            Log.i("ShowQuestion", "Selected Question: $savedQuestion")*/
            Question(navController = navController,
                onSaveQuestion = {},
                isEditable = false
            )
        }
    }
}
