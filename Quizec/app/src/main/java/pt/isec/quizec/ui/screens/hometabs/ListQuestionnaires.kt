package pt.isec.quizec.ui.screens.hometabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.isec.quizec.R
import pt.isec.quizec.models.Question
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel

@Composable
fun ListQuestionnaires(
    tab: Int? = null,
    userId: String? = null,
    questionViewModel: QuestionViewModel? = null,
    questionnaireViewModel: QuestionnaireViewModel,
    onCopyClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onGetQuestionnaire: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val questionnaires by questionnaireViewModel.questionnaires.collectAsState()
    val questions = remember { mutableStateOf<List<Question>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onGetQuestionnaire()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (questions.value.isEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(questionnaires) { questionnaire ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (tab != 1) Modifier.clickable {
                                    questionViewModel?.getQuestionnaireQuestions(questionnaire.questions) {
                                        questions.value = it
                                    }
                                } else Modifier
                            )
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
                                    .padding(end = 8.dp)
                            ) {
                                if(tab==1){
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = questionnaire.title,
                                            maxLines = 2,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "ID: ${questionnaire.id}",
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = questionnaire.title,
                                        maxLines = 2,
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = questionnaire.description,
                                    maxLines = 3,
                                )
                            }
                            if (tab == 1) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = stringResource(R.string.edit_label),
                                        modifier = Modifier
                                            .clickable {
                                                if(questionnaire.responses.isEmpty()){
                                                    onEditClick(questionnaire.id)
                                                } else {
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        snackbarHostState.showSnackbar(
                                                            message = context.getString(R.string.cant_edit_questionnaire),
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }
                                                }
                                            }
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.copy_label),
                                        modifier = Modifier
                                            .clickable {
                                                questionnaireViewModel.uid.value = userId!!
                                                questionnaireViewModel.title.value = questionnaire.title
                                                questionnaireViewModel.description.value = questionnaire.description
                                                questionnaireViewModel.image.value = questionnaire.image.toString()
                                                questionnaireViewModel.selectedQuestionsIds.value = questionnaire.questions
                                                questionnaireViewModel.save()

                                                onCopyClick()
                                            }
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.delete_label),
                                        modifier = Modifier
                                            .clickable {
                                                if (questionnaire.responses.isEmpty()) {
                                                    questionnaireViewModel.deleteQuestionnaire(questionnaire.id) { }
                                                } else {
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        snackbarHostState.showSnackbar(

                                                            message = context.getString(R.string.cant_delete_questionnaire),
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(questions.value) { question ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = question.title,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = question.question,
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = context.getString(R.string.copy_label),
                                modifier = Modifier
                                    .clickable {
                                        questionViewModel?.uid?.value = userId!!
                                        questionViewModel?.restoreFromQuestion(question)
                                        questionViewModel?.saveQuestion()
                                            ?.let { questionViewModel.save(it) }
                                        onCopyClick()
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
