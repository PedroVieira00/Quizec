package pt.isec.quizec.ui.screens.questions

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.ui.components.Dropdown
import pt.isec.quizec.ui.components.TextFieldComponent

@Composable
fun FillTheBlanks(
    type: String? = null,
    correctAnswers: SnapshotStateList<Int>? = null,
    isInResponse: Boolean? = false,
    question: MutableState<String>,
    options: SnapshotStateList<String>,
    isFreeText: Boolean = false,
    isEditable: Boolean = true,
    onOptionSelectedString: ((String) -> Unit)? = null
) {
    LaunchedEffect(Unit) {
        if(!isInResponse!!){
            val currentPlaceholders = parsePlaceholders(question.value)

            options.retainAll { option ->
                val placeholderIndex = options.indexOf(option)
                placeholderIndex >= 0 && placeholderIndex < currentPlaceholders.size && currentPlaceholders[placeholderIndex] in question.value
            }

            currentPlaceholders.forEachIndexed { index, _ ->
                if (index >= options.size) {
                    options.add("")
                }
            }
        }
    }

    Column(modifier = Modifier) {
        if (isInResponse == true) {
            val parts = splitSentenceWithPlaceholders(question.value)
            Text(
                text = buildAnnotatedString {
                    parts.forEach { part ->
                        if (part.isPlaceholder) {
                            append("_____ ")
                        } else {
                            append("${part.text} ")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            parsePlaceholders(question.value).forEachIndexed() { index,placeholder ->

                Text("Options for: ${index + 1}")

                if (!isFreeText) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val processedOptions = options
                        .filter { it.startsWith("$placeholder:") }
                        .map { it.substringAfter(":") }

                    val processedOptionsState = mutableStateListOf<String>().apply {
                        addAll(processedOptions)
                    }

                    Dropdown(
                        isOnFillTheBlanks = true,
                        editableOptions = processedOptionsState,
                        readOnlyOptions = processedOptionsState,
                        currentOption = null,
                        isResponding = isInResponse,
                        onOptionSelected = { selectedIndex ->
                            Log.i("FillTheBlanks", "Selected option: $selectedIndex for '$placeholder'")
                        },
                        onOptionSelectedString = { response ->
                            onOptionSelectedString?.let { it(response) }
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))

                    TextFieldComponent(
                        value = options[index],
                        onValueChange = { newValue -> options[index] = newValue },
                        label = "Space ${index + 1}",
                    )
                }
            }
        } else {
            Text("Write a sentence using placeholders like {{word}} or {{phrase}}")

            Spacer(modifier = Modifier.height(16.dp))

            parsePlaceholders(question.value).forEach { placeholder ->
                if(type != QuestionType.WORD_RESPONSE.name){
                    Text("Options for: '$placeholder'")
                }
                if (!isFreeText) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Log.i("Dropdown12", "options: ${options == null}")
                    if (isEditable) {
                        Dropdown(
                            correctAnswers = correctAnswers,
                            placeholder = placeholder,
                            editableOptions = options,
                            onOptionSelected = { selectedIndex ->
                                Log.i("FillTheBlanks", "Selected option: $selectedIndex for '$placeholder'")
                            }
                        )
                    } else {

                        Log.i("Dropdown12", "afsdfsfsafsdafasdf")
                        // If not editable, just display options without interaction
                        /*options.forEach { option ->
                            Log.i("FillTheBlanks", "Option: $option")
                        }*/
                        Dropdown(
                            readOnlyOptions = options,
                            onOptionSelected = { selectedIndex ->
                                Log.i("FillTheBlanks", "Selected option: $selectedIndex for '$placeholder'")
                            }
                        )
                    }
                }
            }
        }
    }
}

fun parsePlaceholders(sentence: String): List<String> {
    val regex = "\\{\\{(.*?)\\}\\}".toRegex()
    return regex.findAll(sentence).map { it.groupValues[1] }.toList()
}

data class SentencePart(val text: String, val isPlaceholder: Boolean)

fun splitSentenceWithPlaceholders(sentence: String): List<SentencePart> {
    val regex = "\\{\\{(.*?)\\}\\}".toRegex()
    val parts = mutableListOf<SentencePart>()
    var currentIndex = 0

    // Split the sentence into parts, distinguishing placeholders
    regex.findAll(sentence).forEach { match ->
        if (match.range.first > currentIndex) {
            parts.add(SentencePart(sentence.substring(currentIndex, match.range.first), isPlaceholder = false))
        }
        parts.add(SentencePart(match.groupValues[0], isPlaceholder = true))
        currentIndex = match.range.last + 1
    }

    if (currentIndex < sentence.length) {
        parts.add(SentencePart(sentence.substring(currentIndex), isPlaceholder = false))
    }

    return parts
}
