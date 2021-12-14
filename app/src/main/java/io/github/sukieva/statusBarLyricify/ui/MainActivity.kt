package io.github.sukieva.statusBarLyricify.ui

import StatusBarLyric.API.StatusBarLyric
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.sukieva.statusBarLyricify.MyApp
import io.github.sukieva.statusBarLyricify.service.MusicListenerService
import io.github.sukieva.statusBarLyricify.ui.theme.StatusBarLyricifyTheme
import io.github.sukieva.statusBarLyricify.utils.toast

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
        val intent = Intent(this, MusicListenerService::class.java)
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, MusicListenerService::class.java)
        startService(intent)
    }

    override fun onRestart() {
        super.onRestart()
        val intent = Intent(this, MusicListenerService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, MusicListenerService::class.java)
        stopService(intent)
    }
}

@ExperimentalMaterial3Api
@Composable
fun MainView(model: MainViewModel = viewModel()) {
    val isLyricEnabled = remember { model.isLyricEnabled }
    model.checkLyricEnabled()
    Scaffold(
        topBar = { MyTopAppBar() },
        content = {
            Column(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(start = 30.dp)) {
                    Text(text = "Setting")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Enabled")
                        StatusBarLyric(MyApp.context, null, "", false).hasEnable().toString().toast()
                    }
                    Button(onClick = { model.statusBarLyric.updateLyric("歌词") }) {
                        Text(text = "发送歌词")
                    }
                    Button(onClick = { model.statusBarLyric.stopLyric() }) {
                        Text(text = "停止歌词")
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
        //modifier = Modifier.shadow(5.dp)
    )
}


