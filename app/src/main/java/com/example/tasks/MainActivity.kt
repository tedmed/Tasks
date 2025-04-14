package com.example.tasks

import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.room.Index
import com.example.tasks.consts.BottomNavItem
import com.example.tasks.screens.DoneListScreen
import com.example.tasks.screens.SettingsScreen
import com.example.tasks.screens.TodoListScreen
import com.example.tasks.ui.theme.TasksTheme
import com.example.tasks.viewmodel.SettingsViewModel
import com.example.tasks.viewmodel.TodoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    private val todoViewModel: TodoViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin{
            androidContext(this@MainActivity)
            modules(
                roomDBModule,
                settingsModule,
                viewModelModule
            )
        }
        enableEdgeToEdge()
        setContent {
            val theme by settingsViewModel.theme.collectAsState(initial = "system")
            val isDarkTheme = when (theme) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            TasksTheme(darkTheme = isDarkTheme) {
                MainScreen(todoViewModel, settingsViewModel)
            }
        }
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
fun MainScreen(todoViewModel: TodoViewModel, settingsViewModel: SettingsViewModel) {
    val navItems = listOf(
        BottomNavItem.TodoList,
        BottomNavItem.DoneList,
        BottomNavItem.Settings
    )
    var selectedIndex by remember {
        mutableStateOf(0)
    }
    val badgeCount = todoViewModel.todoList.observeAsState().value?.size
    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed{ index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {selectedIndex = index},
                        icon = {
                            BadgedBox(badge = {
                                if (badgeCount != null) {
                                    if(index ==0 && badgeCount > 0) {
                                        Badge() {
                                            Text(badgeCount.toString())
                                        }
                                    }
                                }
                            }) {
                                Icon(imageVector = navItem.icon, contentDescription = "Icon")
                            }
                       },
                        label = {Text(text = navItem.title)}
                    )
                }
            }
        }) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex, todoViewModel, settingsViewModel)
    }
}

@Composable
fun ContentScreen(modifier: Modifier, selectedIndex: Int, todoViewModel: TodoViewModel, settingsViewModel: SettingsViewModel){
    when(selectedIndex){
        0-> TodoListScreen(todoViewModel, settingsViewModel)
        1-> DoneListScreen(todoViewModel, settingsViewModel)
        2-> SettingsScreen(settingsViewModel)
    }
}