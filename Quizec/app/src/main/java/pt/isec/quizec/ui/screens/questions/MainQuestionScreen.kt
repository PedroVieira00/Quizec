package pt.isec.quizec.ui.screens.questions


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.quizec.R
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.ui.components.Dropdown
import pt.isec.quizec.ui.components.ImagePickerComponent
import pt.isec.quizec.ui.components.TextFieldComponent
import pt.isec.quizec.ui.viewmodels.QuestionViewModel

@Composable
fun MainQuestionScreen(
    modifier: Modifier = Modifier,
    questionViewModel: QuestionViewModel,
    isEditable: Boolean = true
){

    LaunchedEffect(questionViewModel.type.value) {
        if(questionViewModel.type.value == QuestionType.FILL_IN_THE_BLANK.name || questionViewModel.type.value == QuestionType.WORD_RESPONSE.name){
            questionViewModel.questionOptions.clear()
            questionViewModel.correctAnswers.clear()
        }
    }

    Log.i("Question13", "type: ${questionViewModel.type.value}")
    Log.i("Question13", "options size: ${questionViewModel.questionOptions.size}")

    Column(
        modifier = modifier
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        Arrangement.SpaceAround,
    ) {

        TextFieldComponent(
            value = questionViewModel.title.value,
            onValueChange = { newText ->
                questionViewModel.title.value = newText
            },
            label = stringResource(id = R.string.title_label),
            isEditable = isEditable
        )

        Spacer(Modifier.height(10.dp))

        ImagePickerComponent(
            imageUri = questionViewModel.image,
            isEditable = isEditable
        )

        Spacer(Modifier.height(10.dp))

        TextFieldComponent(
            value = questionViewModel.question.value,
            onValueChange = { newText ->
                questionViewModel.question.value = newText
            },
            label = stringResource(id = R.string.question_label),
            isFreeText = true,
            isMandatory = true,
            isEditable = isEditable
        )

        Spacer(Modifier.height(10.dp))


        val questionTypeOptions = QuestionType.entries.map { stringResource(id = it.getStringResId()) }
        Log.i("MainQuestionScreen", "$isEditable")
        if(!isEditable){
            Dropdown(
                currentOption = questionViewModel.type.value,
            )
        }else {
            Dropdown(
                readOnlyOptions = questionTypeOptions,
            ) { index ->
                questionViewModel.type.value = QuestionType.entries[index].name
            }
        }

        Spacer(Modifier.height(10.dp))

        when (questionViewModel.type.value) {
            QuestionType.YES_NO.name ->
                BasicOptionsScreen(
                    type = QuestionType.YES_NO.name,
                    options = questionViewModel.questionOptions,
                    questionViewModel = questionViewModel,
                    correctOptions = questionViewModel.correctAnswers,
                    maxOptions = 2,
                    maxCorrectOptions = 1,
                    isEditable = isEditable
                )

            QuestionType.MULTIPLE_CHOICE_ONE_CORRECT.name ->
                BasicOptionsScreen(
                    options = questionViewModel.questionOptions,
                    questionViewModel = questionViewModel,
                    correctOptions = questionViewModel.correctAnswers,
                    maxOptions = null,
                    maxCorrectOptions = 1,
                    isEditable = isEditable
                )

            QuestionType.MULTIPLE_CHOICE_MULTIPLE_CORRECT.name -> {
                BasicOptionsScreen(
                    options = questionViewModel.questionOptions,
                    questionViewModel = questionViewModel,
                    correctOptions = questionViewModel.correctAnswers
                )
            }

            QuestionType.MATCHING.name -> {
                BasicOptionsScreen(
                    type = QuestionType.MATCHING.name,
                    options = questionViewModel.questionOptions,
                    questionViewModel = questionViewModel,
                    correctOptions = questionViewModel.correctAnswers
                )
            }

            QuestionType.ORDERING.name ->
                BasicOptionsScreen(
                    type = QuestionType.ORDERING.name,
                    options = questionViewModel.questionOptions,
                    questionViewModel = questionViewModel,
                    correctOptions = questionViewModel.correctAnswers
                )

            QuestionType.FILL_IN_THE_BLANK.name ->
                FillTheBlanks(
                    correctAnswers = questionViewModel.correctAnswers,
                    question = questionViewModel.question,
                    options = questionViewModel.fillInTheBlanksOptions,
                )

            QuestionType.CONCEPT_ASSOCIATION.name -> {
                BasicOptionsScreen(
                    type = QuestionType.CONCEPT_ASSOCIATION.name,
                    options = questionViewModel.questionOptions,
                    questionViewModel = questionViewModel,
                    correctOptions = questionViewModel.correctAnswers
                )
            }

            QuestionType.WORD_RESPONSE.name ->
                FillTheBlanks(
                    question = questionViewModel.question,
                    options = questionViewModel.fillInTheBlanksOptions,
                    isFreeText = true
                )
        }
    }

}




