package pt.isec.quizec.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    type: String? = null,
    correctAnswers: SnapshotStateList<Int>? = null,
    placeholder: String? = null,
    editableOptions: SnapshotStateList<String>? = null,
    readOnlyOptions: List<String>? = null,
    currentOption: String? = null,
    isOnFillTheBlanks: Boolean? = false,
    isResponding: Boolean? = false,
    onOptionSelectedString: ((String) -> Unit)? = null,
    onOptionSelected: (Int) -> Unit = {},
){
    val isEditable by remember {mutableStateOf(editableOptions != null)}
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(currentOption ?: "") }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val isReadOnly by remember {mutableStateOf(currentOption != null)}

    var onAddOption: () -> Unit = {}
    var onUpdateOption: (String) -> Unit = {}
    var onFocusChange: (FocusState, String) -> Unit = {_, _ ->}
    var onRemoveOption: (String) -> Unit = {}
    var textDefaultValue: String = ""
    var trailingIcon:  @Composable ((String) -> Unit)? = null
    var options: List<String> by remember {
        mutableStateOf(readOnlyOptions ?: editableOptions ?: emptyList())
    }
    Log.i("Dropdown1", "isEditable: $isEditable and editableOptions: $editableOptions")
    if(isEditable && editableOptions != null) {
            options = editableOptions
            onAddOption = {
                if (selectedIndex == -1 &&
                    selectedOption.isNotEmpty() &&
                    !options.contains(selectedOption)) {

                    editableOptions.add("$placeholder:$selectedOption")
                    selectedIndex = options.indexOf(selectedOption)
                }
            }

            onUpdateOption = { newValue ->

                selectedOption = newValue

                Log.i("Dropdown", "placeholder: $placeholder")
                Log.i("Dropdown", "selectedOption: $selectedOption")
                if(placeholder == selectedOption){
                    correctAnswers?.add(editableOptions.size)
                }
            }

            onFocusChange = { focusState, newValue ->
                if (!focusState.isFocused) {
                    onUpdateOption(newValue)
                    expanded = false
                }
            }

            onRemoveOption = { value ->
                editableOptions.remove(value)
                if(placeholder == value){
                    correctAnswers?.remove(editableOptions.indexOf(value))
                }
                selectedIndex = -1
                selectedOption = ""
                expanded = false
            }

            if(!isResponding!!) {
                trailingIcon = { value ->

                    IconButton(
                        onClick = {
                            onRemoveOption(value)
                        },
                    ) { Icon(Icons.Default.Close, contentDescription = "Remove Option") }
                }
            }




    }else if (readOnlyOptions != null){
        options = readOnlyOptions
        if(currentOption != null){
            Log.i("Question13", "kakakkakakkakak13")
            selectedOption = currentOption
        }
        textDefaultValue = if(isOnFillTheBlanks == true){
            "Select option"
        } else {
            "Select question type"
        }
    }else if (currentOption != null){
        Log.i("Question13", "kakakkakakkakak")
        //options = listOf<String>(currentOption)
        selectedOption = currentOption
    }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if(!isReadOnly){
                expanded = !expanded
                onAddOption()
            }
        }
    ){
        TextField(
            value = if (selectedOption.isEmpty()) textDefaultValue else selectedOption,
            onValueChange = { newValue -> onUpdateOption(newValue)},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            readOnly = !isEditable || isReadOnly,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .onFocusChanged { focusState -> onFocusChange(focusState, selectedOption) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .exposedDropdownSize(true)
        ){
            if(isEditable) {
                DropdownMenuItem(
                    text = { Text("") },
                    onClick = {
                        selectedIndex = -1
                        selectedOption = ""
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
            options.forEachIndexed { index, option ->
                val displayOption = option.split(":").getOrNull(1) ?: option
                DropdownMenuItem(
                    text = { Text(text = displayOption) },
                    onClick = {
                        selectedOption = option.split(":").getOrNull(1) ?: option
                        selectedIndex = index
                        expanded = false

                        onOptionSelected(index)
                        onOptionSelectedString?.let { it(selectedOption) }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    trailingIcon = { trailingIcon?.invoke(option) }
                )
            }
        }
    }
}
