package pt.isec.quizec.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pt.isec.quizec.ui.screens.hometabs.HomeTab

@Composable
fun BottomBar(
    onTabSelected: (HomeTab) -> Unit
)
{
    val tabs = remember { HomeTab.entries }

    BottomAppBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                Log.d("BottomBar", "Rendering tab: ${tab.label}, Icon: ${tab.icon}")
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { onTabSelected(tab) }),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label)
                    Text(
                        text = tab.label)
                }
            }
        }
    }
}