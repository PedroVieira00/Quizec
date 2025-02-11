package pt.isec.quizec.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isFreeText: Boolean = false,
    isMandatory: Boolean = true,
    isEditable: Boolean = true
) {
    OutlinedTextField(
        value = value,
        isError = { isMandatory && value.isEmpty() }(),
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = !isFreeText,
        modifier = modifier
            .fillMaxWidth()
            .then(if (isFreeText) Modifier.height(100.dp) else Modifier),
        readOnly = !isEditable
    )
}