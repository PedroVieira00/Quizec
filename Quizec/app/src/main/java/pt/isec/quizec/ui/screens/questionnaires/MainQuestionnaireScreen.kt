package pt.isec.quizec.ui.screens.questionnaires


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isec.quizec.R
import pt.isec.quizec.ui.components.ImagePickerComponent
import pt.isec.quizec.ui.components.TextFieldComponent
import pt.isec.quizec.ui.viewmodels.QuestionnaireViewModel

@Composable
fun MainQuestionnaireScreen(
    questionnaireId: String?,
    modifier: Modifier = Modifier,
    viewModel: QuestionnaireViewModel,
    ) {

    Column(
        modifier = modifier
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text =
            if( questionnaireId != null){
                stringResource(id = R.string.edit_questionnaire_label)
            } else {
                stringResource(id = R.string.create_questionnaire_label)
            },
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextFieldComponent(
            value = viewModel.title.value,
            onValueChange = { newText -> viewModel.title.value = newText },
            label = stringResource(id = R.string.title_label)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldComponent(
            value = viewModel.description.value,
            onValueChange = { newText -> viewModel.description.value = newText },
            label = stringResource(id = R.string.description_label),
            isFreeText = true,
            isMandatory = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        ImagePickerComponent(
            imageUri = viewModel.image
        )
        if(viewModel.questions.isNotEmpty()){
            Text(
                text = stringResource(id = R.string.created_questions),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        viewModel.questions.forEach{ question ->
            Text(
                text = "- ${question.title}",
            )
        }
        if(viewModel.selectedQuestionsIds.value.isNotEmpty()){
            Text(
                text = stringResource(id = R.string.selected_questions),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        viewModel.selectedQuestionsIds.value.forEach{ question ->
            Text(
                text = "- $question",
            )
        }

    }
}



