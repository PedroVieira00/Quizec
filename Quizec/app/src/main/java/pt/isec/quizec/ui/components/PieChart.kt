package pt.isec.quizec.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.datasource.remote.QuestionDataSource
import pt.isec.quizec.datasource.remote.ResponseDataSource
import pt.isec.quizec.models.Question
import pt.isec.quizec.models.QuestionType
import pt.isec.quizec.models.Response
import pt.isec.quizec.repository.QuestionRepository
import pt.isec.quizec.repository.ResponseRepository
import pt.isec.quizec.ui.viewmodels.QuestionViewModel
import pt.isec.quizec.ui.viewmodels.ResponseViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun PieChart(
    navController: NavHostController,
) {

    val context = LocalContext.current.applicationContext as QuizecApp
    val firebaseHelper = context.firebaseHelper

    val questionViewModel: QuestionViewModel
            = viewModel {
        QuestionViewModel(QuestionRepository(QuestionDataSource(firebaseHelper)))
    }

    val responseViewModel: ResponseViewModel
            = viewModel {
        ResponseViewModel(ResponseRepository(ResponseDataSource(firebaseHelper)))
    }

    val responses = remember { mutableStateOf<List<Response>>(emptyList()) }
    val question = remember { mutableStateOf<Question?>(null) }
    val questionId = navController.currentBackStackEntry?.arguments?.getString("questionId")
    val responseInt = remember { mutableStateOf<MutableList<Int>>(mutableListOf()) }
    val responseString = remember { mutableStateOf<MutableList<String>>(mutableListOf()) }
    val amountResponse = remember { mutableStateOf<MutableList<Int>>(mutableListOf()) }
    val colors = remember { mutableStateOf<MutableList<Color>>(mutableListOf()) }
    val array = remember { mutableStateOf<MutableList<List<Int>>>(mutableListOf()) }
    val arrayStrings = remember { mutableStateOf<MutableList<List<String>>>(mutableListOf()) }
    val responseOrdering = remember { mutableStateOf<MutableList<List<Int>>>(mutableListOf()) }
    val responseOrderingString = remember { mutableStateOf<MutableList<List<String>>>(mutableListOf()) }

    LaunchedEffect (Unit) {
        questionId?.let {
            questionViewModel.getQuestions(id = it){ fetchedQuestion ->
                question.value = fetchedQuestion.firstOrNull()
            }

            responseViewModel.getResponses(qid = questionId) { fetchedResponses ->
                responses.value = fetchedResponses

                responses.value.forEach { it ->
                    if (question.value?.questionType == QuestionType.MATCHING.name || question.value?.questionType == QuestionType.CONCEPT_ASSOCIATION.name || question.value?.questionType == QuestionType.ORDERING.name) {
                        if (!array.value.contains(it.responsesInt)) {
                            array.value.add(it.responsesInt)
                            amountResponse.value.add(1)
                        } else {
                            val index = array.value.indexOf(it.responsesInt)
                            amountResponse.value[index] += 1
                        }
                    }
                    if(question.value?.questionType == QuestionType.YES_NO.name || question.value?.questionType == QuestionType.MULTIPLE_CHOICE_ONE_CORRECT.name || question.value?.questionType == QuestionType.MULTIPLE_CHOICE_MULTIPLE_CORRECT.name){
                        it.responsesInt.forEach { ot ->
                            if (!responseInt.value.contains(ot)) {
                                responseInt.value.add(ot)
                                amountResponse.value.add(1)
                                colors.value.add(Color(
                                    red = Random.nextInt(0, 256),
                                    green = Random.nextInt(0, 256),
                                    blue = Random.nextInt(0, 256),
                                    alpha = 255
                                ))
                            } else {
                                val index = responseInt.value.indexOf(ot)
                                amountResponse.value[index] += 1
                            }
                        }
                    }
                    if(question.value?.questionType == QuestionType.FILL_IN_THE_BLANK.name || question.value?.questionType == QuestionType.WORD_RESPONSE.name){
                        if (!arrayStrings.value.contains(it.responsesString)) {
                            arrayStrings.value.add(it.responsesString)
                            amountResponse.value.add(1)
                        } else {
                            val index = arrayStrings.value.indexOf(it.responsesString)
                            amountResponse.value[index] += 1
                        }
                    }
                }
            }
        }
    }

    val total = responses.value.size
    if (total == 0) return

    val proportions = amountResponse.value.map {
        it / total.toFloat()
    }

    val angles = proportions.map { it * 360f }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text("Results")
                },
            )
        },
        content = { paddingValues ->
            if(question.value?.questionType == QuestionType.YES_NO.name || question.value?.questionType == QuestionType.MULTIPLE_CHOICE_ONE_CORRECT.name || question.value?.questionType == QuestionType.MULTIPLE_CHOICE_MULTIPLE_CORRECT.name){
                val size = 300.dp

                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                ) {
                    Canvas(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(size)
                    ) {
                        var startAngle = 0f

                        angles.forEachIndexed { index, sweepAngle ->
                            val sliceCenter = startAngle + sweepAngle / 2
                            val sliceCenterRadians = Math.toRadians(sliceCenter.toDouble())

                            drawArc(
                                color = colors.value[index],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size = Size(size.toPx(), size.toPx()),
                                topLeft = Offset.Zero,
                            )

                            val labelX =
                                (size.toPx() / 2) + (size.toPx() / 2) * Math.cos(sliceCenterRadians)
                            val labelY =
                                (size.toPx() / 2) + (size.toPx() / 2) * Math.sin(sliceCenterRadians)

                            val label = "${responseInt.value[index]}"
                            drawContext.canvas.nativeCanvas.apply {
                                drawText(
                                    label,
                                    labelX.toFloat(),
                                    labelY.toFloat(),
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 50f
                                        isAntiAlias = true
                                    }
                                )
                            }
                            startAngle += sweepAngle
                        }
                    }
                }
            }
            if(question.value?.questionType == QuestionType.MATCHING.name || question.value?.questionType == QuestionType.CONCEPT_ASSOCIATION.name || question.value?.questionType == QuestionType.ORDERING.name){
                val responseWithAmounts = array.value.zip(amountResponse.value)
                val sortedResponses = responseWithAmounts.sortedByDescending { it.second }

                responseOrdering.value.clear()
                sortedResponses.forEach { (response, _) ->
                    responseOrdering.value.add(response)
                }

                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(sortedResponses) { (response, count) ->
                            Text(
                                text = "Response: $response - Count: $count",
                                modifier = Modifier.padding(8.dp),
                                color = Color.Black,
                            )
                        }
                    }
                }
            }
            if(question.value?.questionType == QuestionType.FILL_IN_THE_BLANK.name || question.value?.questionType == QuestionType.WORD_RESPONSE.name){
                val responseWithAmounts = arrayStrings.value.zip(amountResponse.value)
                val sortedResponses = responseWithAmounts.sortedByDescending { it.second }

                responseOrderingString.value.clear()
                sortedResponses.forEach { (response, _) ->
                    responseOrderingString.value.add(response)
                }

                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(sortedResponses) { (response, count) ->
                            Text(
                                text = "Response: $response - Count: $count",
                                modifier = Modifier.padding(8.dp),
                                color = Color.Black,
                            )
                        }
                    }
                }
            }
        }
    )
}