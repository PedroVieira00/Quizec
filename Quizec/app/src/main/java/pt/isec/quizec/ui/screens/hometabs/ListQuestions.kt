package pt.isec.quizec.ui.screens.hometabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.isec.quizec.models.Question
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.models.Questionnaire
import pt.isec.quizec.models.Response
import pt.isec.quizec.ui.components.PieChart
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel
import pt.isec.quizec.ui.viewmodels.ResponseViewModel
import pt.isec.quizec.ui.viewmodels.UserViewModel

@Composable
fun ListQuestions(
    navController: NavHostController,
    questionViewModel: QuestionViewModel,
    questionnaireViewModel: QuestionnaireViewModel,
    userViewModel: UserViewModel,
    onItemClick: (Question) -> Unit,
    onEditClick: (String) -> Unit,
) {
    val questionnaires = remember { mutableStateOf<List<Questionnaire>>(emptyList()) }
    val questions by questionViewModel.questions.collectAsState()
    val canDeleteEditQuestion = remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        questionViewModel.getQuestions(uid = userViewModel.currentUser.value!!.uid)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(questions) { question ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(question) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = question.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.question,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.clickable {
                                    questionnaireViewModel.getQuestionnaires(qid = question.id) { fetchedQuestionnaires ->
                                        questionnaires.value = fetchedQuestionnaires
                                        canDeleteEditQuestion.value = questionnaires.value.all { it.responses.isEmpty() }

                                        if (canDeleteEditQuestion.value) {
                                            onEditClick(question.id)
                                        } else {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Cannot edit question because it is linked to a questionnaire that has already been responded.",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Copy",
                                modifier = Modifier.clickable {
                                    questionViewModel.uid.value = userViewModel.currentUser.value!!.uid
                                    questionViewModel.restoreFromQuestion(question)
                                    questionViewModel.save()
                                }
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.clickable {
                                    questionnaireViewModel.getQuestionnaires(qid = question.id) { fetchedQuestionnaires ->
                                        questionnaires.value = fetchedQuestionnaires
                                        canDeleteEditQuestion.value = questionnaires.value.all { it.responses.isEmpty() }

                                        if (canDeleteEditQuestion.value) {
                                            questionnaires.value.forEach { questionnaire ->
                                                questionnaireViewModel.updateQuestionnaire(questionnaire, qid = question.id) { }
                                            }
                                            questionViewModel.deleteQuestion(question.id) { }
                                        } else {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Cannot delete question because it is linked to a questionnaire that has already been responded.",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

