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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tasks.consts.BottomNavItem
import com.example.tasks.consts.Routes
import com.example.tasks.screens.TodoAddScreen
import com.example.tasks.screens.DoneListScreen
import com.example.tasks.screens.SettingsScreen
import com.example.tasks.screens.TodoDetailScreen
import com.example.tasks.screens.TodoEditScreen
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
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navItems = listOf(
        BottomNavItem.TodoList,
        BottomNavItem.DoneList,
        BottomNavItem.Settings
    )
    val badgeCount = todoViewModel.sortedTodoList.collectAsState().value.size
    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEach{ navItem ->
                    NavigationBarItem(
                        selected = currentRoute == navItem.screenRoute,
                        onClick = {
                            if (currentRoute != navItem.screenRoute) {
                                navController.navigate(navItem.screenRoute) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if(navItem is BottomNavItem.TodoList && badgeCount > 0) {
                                    Badge {
                                        Text(badgeCount.toString())
                                    }
                                }
                            }) {
                                Icon(imageVector = navItem.icon, contentDescription = navItem.title)
                            }
                       },
                        label = {Text(text = navItem.title)}
                    )
                }
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.TodoList,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.TodoList) {
                TodoListScreen(todoViewModel, navController, settingsViewModel)
            }
            composable(Routes.DoneList) {
                DoneListScreen(todoViewModel, navController, settingsViewModel)
            }
            composable(Routes.Settings) {
                SettingsScreen(settingsViewModel)
            }
            composable(Routes.AddTodo) {
                TodoAddScreen(todoViewModel,settingsViewModel, navController)
            }
            composable(
                route = Routes.TodoDetail,
                arguments = listOf(navArgument("todoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val todoId = backStackEntry.arguments?.getInt("todoId") ?: return@composable
                TodoDetailScreen(todoId, todoViewModel, navController)
            }
            composable(
                route = Routes.TodoEdit,
                arguments = listOf(navArgument("todoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val todoId = backStackEntry.arguments?.getInt("todoId") ?: return@composable
                TodoEditScreen(todoId, todoViewModel, settingsViewModel, navController)
            }
        }
    }
}