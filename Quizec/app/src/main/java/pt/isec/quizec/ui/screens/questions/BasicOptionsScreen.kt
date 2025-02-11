package pt.isec.quizec.ui.screens.questions

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.ui.components.TextFieldComponent
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.ResponseViewModel
import pt.isec.quizec.R
@Composable
fun BasicOptionsScreen(
    isInResponse: Boolean? = false,
    type: String? = null,
    responsesInt: SnapshotStateList<Int>? = null,
    questionViewModel: QuestionViewModel? = null,
    options: SnapshotStateList<String>,
    correctOptions: SnapshotStateList<Int>? = null,
    maxOptions: Int? = null,
    maxCorrectOptions: Int? = null,
    questionIndex: Int? = null,
    isEditable: Boolean = true
) {
    val selectedOptions = remember(questionIndex) {
        mutableStateListOf<Int>()
    }
    if(!isInResponse!!){
        if (correctOptions != null) {
            if(type == QuestionType.YES_NO.name || type == QuestionType.MULTIPLE_CHOICE_ONE_CORRECT.name){
                if(correctOptions.isNotEmpty()){
                    correctOptions.first { option ->
                        selectedOptions.add(option)
                    }
                }
            } else {
                if(correctOptions.isNotEmpty()){
                    correctOptions.forEach { option ->
                        selectedOptions.add(option)
                    }
                }
            }
        }
    }

    options.forEach { option ->
        Log.i("Question13", "sfjashlfgbdfsgfjasdfjuiasdfiusauigilasdfgnlidfsgnds: $option")
    }

    if (options.isEmpty()) {
        options.addAll(listOf("", ""))
    }

    Column {
        if (type == QuestionType.MATCHING.name && questionViewModel != null) {
            stringResource(R.string.matching_label)
            Text(stringResource(R.string.matching_label))
        }
        if(type == QuestionType.CONCEPT_ASSOCIATION.name && questionViewModel != null){
            Text(stringResource(R.string.concept_association_label))
        }

        if ((type == QuestionType.MATCHING.name || type == QuestionType.CONCEPT_ASSOCIATION.name) && questionViewModel == null) {
            val halfSize = options.size / 2
            val leftOptions = options.take(halfSize)
            val rightOptions = options.drop(halfSize)

            Row(modifier = Modifier.padding(8.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    leftOptions.forEachIndexed { index, option ->
                        Text( "${index + 1}- $option")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    rightOptions.forEachIndexed { index, option ->
                        Text("${index + 1}- $option")
                    }
                }
            }
        } else {
            options.forEachIndexed { index, option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (type != QuestionType.MATCHING.name && type != QuestionType.CONCEPT_ASSOCIATION.name && type != QuestionType.ORDERING.name && correctOptions != null) {
                        if (maxCorrectOptions == 1) {
                            RadioButton(
                                selected = selectedOptions.contains(index),
                                onClick = {
                                    if (isEditable) {
                                        selectedOptions.clear()
                                        selectedOptions.add(index)
                                        correctOptions.clear()
                                        correctOptions.add(index)
                                        responsesInt?.add(index)
                                    }
                                }
                            )
                        } else {
                            Checkbox(
                                checked = selectedOptions.contains(index),
                                onCheckedChange = { isChecked ->
                                    if (isEditable) {
                                        if (isChecked) {
                                            if (maxCorrectOptions == null || selectedOptions.size < maxCorrectOptions) {
                                                selectedOptions.add(index)
                                                correctOptions.add(index)
                                                responsesInt?.add(index)
                                            }
                                        } else {
                                            selectedOptions.remove(index)
                                            correctOptions.remove(index)
                                            responsesInt?.remove(index)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    if (questionViewModel == null) {
                        if(type == QuestionType.ORDERING.name){
                            Text(text = "${index + 1}. $option", modifier = Modifier.padding(start = 8.dp))
                        }
                        Text(text = option, modifier = Modifier.padding(start = 8.dp))
                    } else {
                        Log.i("Question13", "option: $option")
                        TextFieldComponent(
                            value = option,
                            onValueChange = { newText -> options[index] = newText },
                            label = "Option ${index + 1}",
                            isEditable = isEditable
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (isEditable && questionViewModel != null) {
        if (maxOptions == null || options.size < maxOptions) {
            Button(onClick = {
                options.add("")
            }) {
                Text(stringResource(R.string.add_option_label))
            }
        }

        if (options.size > 2) {
            Button(onClick = {
                val lastIndex = options.lastIndex
                options.removeAt(lastIndex)
                correctOptions?.removeAll { it == lastIndex }
            }) {
                Text(stringResource(R.string.remove_option_label))
            }
        }
    }
}

