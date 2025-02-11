package pt.isec.quizec.ui.screens.hometabs

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

sealed class HomeTab(
    val label: String,
    val icon: ImageVector,
    val title: String
) {
    companion object {
        val entries: List<HomeTab> by lazy {
            listOf(
                CreatedQuestionnaires,
                ParticipatedQuestionnaires,
                UserQuestions,
                Settings
            )
        }
    }

    open fun floatingActions(navController: NavController): List<FloatingButtonConfig> = emptyList()

    object CreatedQuestionnaires: HomeTab(
        label = "Created",
        icon = Icons.Default.Home,
        title = "Created Questionnaires"
    ){

        override fun floatingActions(navController: NavController): List<FloatingButtonConfig> {
            return listOf(
                FloatingButtonConfig(
                    label = "Add",
                    icon = Icons.Default.Add,
                    onClick = { Log.i("Floating", "Created inside action")
                        navController.navigate("CreateQuestionnaire") }
                )
            )
        }

    }

    object ParticipatedQuestionnaires : HomeTab(
        label = "Participated",
        icon = Icons.Default.Create,
        title = "Participated Questionnaires"
    ){
        override fun floatingActions(navController: NavController): List<FloatingButtonConfig> {
            return listOf(
                FloatingButtonConfig(
                    label = "Add",
                    icon = Icons.Default.Add,
                    onClick = { Log.i("Floating", "Participated inside action")
                        navController.navigate("CreateQuestionnaire") }
                )
            )
        }
    }

    object UserQuestions : HomeTab(
        label = "Questions",
        icon = Icons.AutoMirrored.Default.List,
        title = "Questions Database"
    ){
        override fun floatingActions(navController: NavController): List<FloatingButtonConfig> {
            return listOf(
                FloatingButtonConfig(
                    label = "Add Question",
                    icon = Icons.Default.Add,
                    onClick = {
                        Log.i("Floating", "Questions inside action")
                        navController.navigate("CreateQuestion") }
                )
            )
        }
    }

    object Settings : HomeTab(
        label = "Settings",
        icon = Icons.Default.Settings,
        title = "Settings"
    )

}

data class FloatingButtonConfig(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)