package com.example.obracheck_frontend.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.obracheck_frontend.ui.login.LoginScreen
import com.example.obracheck_frontend.ui.site.SiteFormScreen
import com.example.obracheck_frontend.ui.site.SiteListScreen
import com.example.obracheck_frontend.ui.login.WelcomeScreen
import com.example.obracheck_frontend.ui.worker.WorkerFormScreen
import com.example.obracheck_frontend.ui.worker.WorkerListScreen
import com.example.obracheck_frontend.viewmodel.SiteViewModel
import com.example.obracheck_frontend.viewmodel.UserViewModel
import com.example.obracheck_frontend.viewmodel.WorkerViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val siteViewModel = remember { SiteViewModel() }
    val userViewModel = remember { UserViewModel() }
    val workerViewModel = remember { WorkerViewModel() }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                navController = navController,
                viewModel = userViewModel
            )
        }


        composable(
            route = NavRoutes.WELCOME,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType },
                navArgument("name")   { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: -1L
            val encodedName = backStackEntry.arguments?.getString("name") ?: ""
            val userName = Uri.decode(encodedName)

            WelcomeScreen(
                navController = navController,
                userId = userId,
                userName = userName
            )
        }

        composable(
            route = NavRoutes.SITE_LIST,
            arguments = listOf(navArgument("userId") { defaultValue = -1L })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: -1L
            SiteListScreen(
                userId = userId,
                siteViewModel = siteViewModel,
                onNavigateToForm = { id ->
                    navController.navigate("siteform?id=${id ?: -1}&userId=$userId")
                },
                onNavigateToWorkers = { siteId ->
                    navController.navigate("workerlist/$siteId")
                }
            )
        }

        composable(
            route = NavRoutes.SITE_FORM,
            arguments = listOf(
                navArgument("id") { defaultValue = -1L },
                navArgument("userId") { defaultValue = -1L }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")?.takeIf { it != -1L }
            val userId = backStackEntry.arguments?.getLong("userId") ?: -1L

            SiteFormScreen(
                siteViewModel = siteViewModel,
                siteId = id,
                userId = userId,
                onSubmitComplete = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NavRoutes.WORKER_FORM,
            arguments = listOf(
                navArgument("siteId") { defaultValue = -1L },
                navArgument("editId") { defaultValue = -1L }
            )
        ) { backStackEntry ->
            val siteId = backStackEntry.arguments?.getLong("siteId") ?: -1L
            val editId = backStackEntry.arguments?.getLong("editId")?.takeIf { it != -1L }

            WorkerFormScreen(
                siteId = siteId,
                editId = editId,
                viewModel = workerViewModel,
                onSubmitComplete = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NavRoutes.WORKER_LIST,
            arguments = listOf(navArgument("siteId") { defaultValue = -1L })
        ) { backStackEntry ->
            val siteId = backStackEntry.arguments?.getLong("siteId") ?: -1L

            WorkerListScreen(
                siteId = siteId,
                navController = navController,
                viewModel = workerViewModel
            )
        }
    }
}
