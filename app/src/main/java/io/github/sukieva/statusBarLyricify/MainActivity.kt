package io.github.sukieva.statusBarLyricify

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import io.github.sukieva.statusBarLyricify.ui.theme.StatusBarLyricifyTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StatusBarLyricifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainView()
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun MainView() {
    Scaffold(
        topBar = { MyTopAppBar() },
        content = {
            Column(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(start = 30.dp)) {
                    Text(text = "Setting")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Enabled")
                    }
                }
            }
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun MyTopAppBar() {
    SmallTopAppBar(
        title = { Text(text = "StatusBarLyricify") },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        modifier = Modifier.shadow(5.dp)
    )
}

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showSystemUi = true)
@Composable
fun MainViewPreview(){
    StatusBarLyricifyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MainView()
        }
    }
}

