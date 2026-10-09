package app.learn.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.learn.project.ui.theme.LearningProjectTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Валидация входных данных: extra может отсутствовать или быть пустым
        val text = intent.getStringExtra(EXTRA_TEXT)?.takeIf { it.isNotBlank() }
        setContent {
            LearningProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SecondScreen(
                        text = text,
                        onBack = ::finish,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_TEXT = "app.learn.project.EXTRA_TEXT"
    }
}

@Composable
fun SecondScreen(text: String?, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.received_text_label), style = MaterialTheme.typography.titleMedium)
        Text(
            text = text ?: stringResource(R.string.no_text_received),
            style = MaterialTheme.typography.headlineSmall,
            color = if (text == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Button(onClick = onBack) {
            Text(stringResource(R.string.back))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    LearningProjectTheme {
        SecondScreen(text = "Привет!", onBack = {})
    }
}
