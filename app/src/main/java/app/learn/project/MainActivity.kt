package app.learn.project

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.learn.project.ui.theme.LearningProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearningProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        onOpenSecond = ::openSecondActivity,
                        onCall = ::callFriend,
                        onShare = ::shareText,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Явный Intent: точно указываем, какую Activity открыть
    private fun openSecondActivity(text: String) {
        val intent = Intent(this, SecondActivity::class.java)
            .putExtra(SecondActivity.EXTRA_TEXT, text)
        startActivity(intent)
    }

    // Неявный Intent: система сама найдёт приложение, умеющее ACTION_DIAL
    private fun callFriend(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_no_dialer, Toast.LENGTH_SHORT).show()
        }
    }

    // Системный Intent: ACTION_SEND + chooser «Поделиться через…»
    private fun shareText(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_chooser_title)))
    }
}

private val PHONE_REGEX = Regex("""^\+?[0-9\s\-()]{3,20}$""")

/** Номер валиден, если состоит из цифр/пробелов/скобок/дефисов, с необязательным «+», и в нём 3–15 цифр. */
fun isValidPhone(phone: String): Boolean =
    PHONE_REGEX.matches(phone) && phone.count(Char::isDigit) in 3..15

@Composable
fun MainScreen(
    onOpenSecond: (String) -> Unit,
    onCall: (String) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable { mutableStateOf("") }
    var errorRes by rememberSaveable { mutableStateOf<Int?>(null) }

    // Общая проверка: пустую строку никуда не отправляем
    fun withText(action: (String) -> Unit) {
        val value = text.trim()
        if (value.isEmpty()) errorRes = R.string.error_empty_text else action(value)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                errorRes = null
            },
            label = { Text(stringResource(R.string.input_label)) },
            isError = errorRes != null,
            supportingText = errorRes?.let { { Text(stringResource(it)) } },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { withText(onOpenSecond) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.open_second_activity))
        }

        Button(
            onClick = {
                withText { phone ->
                    if (isValidPhone(phone)) onCall(phone) else errorRes = R.string.error_invalid_phone
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.call_friend))
        }

        Button(onClick = { withText(onShare) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.share_text))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LearningProjectTheme {
        MainScreen(onOpenSecond = {}, onCall = {}, onShare = {})
    }
}
