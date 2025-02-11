package pt.isec.quizec.ui.screens.questionnaires

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isec.quizec.R
import pt.isec.quizec.models.Question
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel

@Composable
fun QuestionsSelectorScreen(
    modifier: Modifier = Modifier,
    viewModel: QuestionnaireViewModel,
    questions: List<Question>,
    onSelect: (List<String>) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(id = R.string.select_questions),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        questions.forEach { question ->
            val isSelected = viewModel.selectedQuestionsIds.value.contains(question.id)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            viewModel.updateSelectedQuestionsIds(viewModel.selectedQuestionsIds.value + question.id)
                        } else {
                            viewModel.updateSelectedQuestionsIds(viewModel.selectedQuestionsIds.value - question.id)
                        }
                    }
                )
                Text(
                    text = question.title,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Button(
            onClick = {
                onSelect(viewModel.selectedQuestionsIds.value)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.confirm_label))
        }
    }
}
