package io.github.sukieva.statusBarLyricify.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.sukieva.statusBarLyricify.R
import io.github.sukieva.statusBarLyricify.ui.theme.StatusBarLyricifyTheme
import io.github.sukieva.statusBarLyricify.utils.browse
import io.github.sukieva.statusBarLyricify.utils.startServe
import io.github.sukieva.statusBarLyricify.utils.stopServe

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StatusBarLyricifyTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainView()
                }
            }
        }
        startServe()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServe()
    }

}

@ExperimentalMaterial3Api
@Composable
fun MainView(model: MainViewModel = viewModel()) {
    val isLyricEnabled = remember { model.isLyricEnabled }
    val openDialog = remember { mutableStateOf(false) }
    model.checkLyricEnabled()
    Scaffold(
        topBar = { MyTopAppBar() },
        content = {
            Column(Modifier.fillMaxWidth()) {
                MyCard(
                    title = if (isLyricEnabled.value) stringResource(id = R.string.active_yes) else stringResource(id = R.string.active_not),
                    isActive = isLyricEnabled.value,
                    onClick = {
                        model.dialogId = 1
                        openDialog.value = true
                    }
                )
                Column(Modifier.padding(all = 30.dp)) {
                    MyTitle(stringResource(id = R.string.description))
                    MyBody(stringResource(id = R.string.keep_alive), onClick = {
                        model.dialogId = 2
                        openDialog.value = true
                    })
                    MyBody(stringResource(id = R.string.notice), onClick = {
                        model.dialogId = 3
                        openDialog.value = true
                    })
                    Spacer(modifier = Modifier.height(20.dp))
                    MyTitle(stringResource(id = R.string.about))
                    MyBody(title = "StatusBarLyricify", onClick = { browse("https://github.com/SukiEva/StatusBarLyricify") })
                    MyBody(title = "MIUIStatusBarLyric", onClick = { browse("https://github.com/577fkj/MIUIStatusBarLyric") })
                    MyBody(title = "StatusBarLyricExt", onClick = { browse("https://github.com/577fkj/StatusBarLyricExt") })
                    MyBody(title = "LSPosed", onClick = { browse("https://github.com/LSPosed/LSPosed") })
                }
            }
        }
    )
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(id = R.string.dialog_title))
            },
            text = {
                val msg = when (model.dialogId) {
                    1 -> stringResource(id = R.string.dialog_body)
                    2 -> stringResource(id = R.string.dialog_keep_alive)
                    3 -> stringResource(id = R.string.dialog_error)
                    else -> ""
                }
                Text(text = msg)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_button))
                }
            }
        )
    }
}


@Composable
fun MyTitle(
    title: String = "Title",
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
fun MyBody(
    title: String = "",
    onClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 30.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }

}

@ExperimentalMaterial3Api
@Composable
fun MyTopAppBar() {
    SmallTopAppBar(
        title = { Text(text = "StatusBarLyricify", style = MaterialTheme.typography.headlineLarge) },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        //modifier = Modifier.shadow(5.dp)
    )
}

@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    title: String = "Title",
    onClick: () -> Unit = {},
    isActive: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(start = 30.dp, end = 30.dp, top = 10.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        elevation = 2.dp,
        backgroundColor = when {
            isActive -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondary
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 5.dp, bottom = 5.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}


