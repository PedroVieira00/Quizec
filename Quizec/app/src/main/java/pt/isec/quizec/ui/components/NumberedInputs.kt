package pt.isec.quizec.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.isec.quizec.models.QuestionType

@Composable
fun NumberedInputs(
    type: String? = null,
    size: Int,
    responses: SnapshotStateList<Int>? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..size) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(type != QuestionType.ORDERING.name){
                        Text(text = "$i- ", modifier = Modifier.padding(end = 4.dp))
                    }
                    var inputValue by remember { mutableStateOf("") }
                    TextField(
                        value = inputValue,
                        onValueChange = { newText ->
                            if (newText.isEmpty() || newText.all { it.isDigit() }) {
                                inputValue = newText
                                newText.toIntOrNull()?.let { responses?.set(i - 1, it) }
                            }
                        },
                        modifier = Modifier.width(60.dp)
                    )
                }
            }
        }
    }
}
