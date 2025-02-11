package pt.isec.quizec.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.quizec.ui.screens.hometabs.FloatingButtonConfig
import pt.isec.quizec.R

@Composable
fun FloatingButton(
    floatingButtons : List<FloatingButtonConfig>
) {
    if(floatingButtons.isNotEmpty()) {
        if (floatingButtons.size == 1) {
            Log.i("Floating", "1")
            FloatingActionButton(
                onClick = { floatingButtons.first().onClick()
                    Log.i("Floating", "onClick 1")}
            ) {
                Icon(
                    imageVector = floatingButtons.first().icon,
                    contentDescription = floatingButtons.first().label
                )
            }
        } else {
            Log.i("Floating", ">1")
            Column(horizontalAlignment = Alignment.End) {
                var isMenuOpen by remember { mutableStateOf(false) }

                if (isMenuOpen) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize(Alignment.BottomEnd)
                    ) {
                        floatingButtons.forEach { floatingButton ->
                            ExtendedFloatingActionButton(
                                text = { Text(floatingButton.label) },
                                icon = {
                                    Icon(
                                        imageVector = floatingButton.icon,
                                        contentDescription = floatingButton.label
                                    )
                                },
                                onClick = {
                                    isMenuOpen = false
                                    floatingButton.onClick()
                                    Log.i("Floating", "onClick 2")
                                },
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = {
                        isMenuOpen = !isMenuOpen
                        Log.i("Floating", "onClick open")
                    }
                ) {
                    Icon(
                        imageVector = if (isMenuOpen) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = stringResource(R.string.open_menu_description)
                    )
                }
            }
        }
    }
}