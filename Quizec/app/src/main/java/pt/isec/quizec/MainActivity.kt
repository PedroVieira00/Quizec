package pt.isec.quizec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pt.isec.quizec.ui.QuizecNavGraph
import pt.isec.quizec.ui.theme.QuizecTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuizecTheme(dynamicColor = false) {
                QuizecNavGraph()
            }
        }

    }
}