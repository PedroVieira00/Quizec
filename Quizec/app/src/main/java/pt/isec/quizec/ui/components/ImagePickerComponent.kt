package pt.isec.quizec.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import pt.isec.quizec.util.AMovServer
import pt.isec.quizec.R

@Composable
fun ImagePickerComponent(
    imageUri: MutableState<String>,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        contract = PickVisualMedia(),
        onResult = { uri ->
            //imageUri.value = uri.toString()
            if (uri != null) {
                AMovServer.asyncUploadImage(
                    inputStream = context.contentResolver.openInputStream(uri)!!,
                    extension = "jpg",
                    onResult = { result ->
                        if (result != null) {
                            imageUri.value = result
                            error = null
                        } else {
                            error = context.getString(R.string.upload_image_error_message)
                        }
                    }
                )
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        if (!imageUri.value.isEmpty() && error == null) {
            AsyncImage(
                model = imageUri.value,
                contentDescription = stringResource(R.string.image_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (error != null) {
                    Text(text = error!!)
                }

                Text(text = stringResource(R.string.no_image_selected))

            }
        }

        Spacer(Modifier.height(16.dp))

        if(isEditable) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        pickImage.launch(
                            PickVisualMediaRequest(
                                PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = if (!imageUri.value.isEmpty()) stringResource(R.string.add_image) else stringResource(R.string.replace_image))
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = {

                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(text = stringResource(R.string.clear_image))
                }
            }
        }

    }
}
