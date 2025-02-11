package pt.isec.quizec.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pt.isec.quizec.QuizecApp
import pt.isec.quizec.R
import pt.isec.quizec.datasource.remote.UserDataSource
import pt.isec.quizec.repository.UserRepository
import pt.isec.quizec.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController) {
    val context = LocalContext.current.applicationContext as QuizecApp
    val firebaseHelper = context.firebaseHelper

    val userViewModel : UserViewModel
            = viewModel { UserViewModel(UserRepository(UserDataSource(firebaseHelper))) }

    val isFormValid = userViewModel.email.value.isNotEmpty() && userViewModel.password.value.isNotEmpty()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Login") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = userViewModel.email.value,
                onValueChange = { userViewModel.email.value = it },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = userViewModel.password.value,
                onValueChange = { userViewModel.password.value = it },
                label = { Text(stringResource(R.string.password_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    userViewModel.getUser(
                        callback = { documentId ->
                            if(documentId!=null){
                                Toast.makeText(context, "${context.getString(R.string.welcome)} ${userViewModel.getCurrentUser()?.uid}", Toast.LENGTH_SHORT).show()
                                navController.navigate("HomePage")
                            }else{
                                Toast.makeText(context, context.getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )


                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid,
            ) {
                Text(stringResource(R.string.login_label))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {navController.navigate("Register")}
            ) {
                stringResource(R.string.register_description)
            }
        }
    }
}