package com.example.obracheck_frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.obracheck_frontend.ui.site.SiteFormScreen
import com.example.obracheck_frontend.ui.site.SiteListScreen
import com.example.obracheck_frontend.viewmodel.SiteViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val siteViewModel = remember { SiteViewModel() }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SITE_LIST
    ) {
        composable(NavRoutes.SITE_LIST) {
            SiteListScreen(
                siteViewModel = siteViewModel,
                onNavigateToForm = { id ->
                    navController.navigate("siteform?id=${id ?: -1}")
                }
            )
        }

        composable(
            route = NavRoutes.SITE_FORM,
            arguments = listOf(navArgument("id") { defaultValue = -1L })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")?.takeIf { it != -1L }
            SiteFormScreen(
                siteViewModel = siteViewModel,
                siteId = id,
                onSubmitComplete = {
                    navController.popBackStack()
                }
            )
        }
    }
}
