package com.example.obracheck_frontend.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.obracheck_frontend.ui.attendance.AttendanceScreen
import com.example.obracheck_frontend.ui.evidence.EvidenceFormScreen
import com.example.obracheck_frontend.ui.evidence.EvidenceListScreen
import com.example.obracheck_frontend.ui.login.LoginScreen
import com.example.obracheck_frontend.ui.login.RegisterScreen
import com.example.obracheck_frontend.ui.login.WelcomeScreen
import com.example.obracheck_frontend.ui.progress.ProgressFormScreen
import com.example.obracheck_frontend.ui.site.SiteFormScreen
import com.example.obracheck_frontend.ui.site.SiteListScreen
import com.example.obracheck_frontend.ui.worker.WorkerFormScreen
import com.example.obracheck_frontend.ui.worker.WorkerListScreen
import com.example.obracheck_frontend.ui.progress.ProgressListScreen
import com.example.obracheck_frontend.viewmodel.AttendanceViewModel
import com.example.obracheck_frontend.viewmodel.EvidenceViewModel
import com.example.obracheck_frontend.viewmodel.SiteViewModel
import com.example.obracheck_frontend.viewmodel.UserViewModel
import com.example.obracheck_frontend.viewmodel.WorkerViewModel
import com.example.obracheck_frontend.viewmodel.ProgressViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val siteViewModel = remember { SiteViewModel() }
    val userViewModel = remember { UserViewModel() }
    val workerViewModel = remember { WorkerViewModel() }
    val attendanceViewModel = remember { AttendanceViewModel() }
    val progressViewModel = remember { ProgressViewModel() }
    val evidenceViewModel = remember { EvidenceViewModel() }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {

        // 🟢 LOGIN
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                navController = navController,
                viewModel = userViewModel
            )
        }

        // 🟢 REGISTRO
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                navController = navController,
                viewModel = userViewModel
            )
        }

        // 🔵 BIENVENIDA
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

        // 🔵 ASISTENCIA
        composable(
            route = NavRoutes.ATTENDANCE_LIST,
            arguments = listOf(
                navArgument("siteId") { type = NavType.LongType },
                navArgument("date")   { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val siteId = backStackEntry.arguments?.getLong("siteId") ?: 0L
            val dateIso = backStackEntry.arguments?.getString("date") ?: ""

            AttendanceScreen(
                siteId = siteId,
                dateIso = dateIso,
                viewModel = attendanceViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToWorkerList = {
                    navController.navigate("workerlist/$siteId")
                }
            )
        }

        // 🔵 LISTA DE SITIOS
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

        // 🔵 FORM SITIO
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
                onSubmitComplete = { navController.popBackStack() }
            )
        }

        // 🔵 FORM TRABAJADOR
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
                onSubmitComplete = { navController.popBackStack() }
            )
        }

        // 🔵 LISTA DE TRABAJADORES
        composable(
            route = NavRoutes.WORKER_LIST,
            arguments = listOf(navArgument("siteId") { defaultValue = -1L })
        ) { backStackEntry ->
            val siteId = backStackEntry.arguments?.getLong("siteId") ?: -1L

            WorkerListScreen(
                siteId = siteId,
                navController = navController,
                viewModel = workerViewModel,
                attendanceVm = attendanceViewModel
            )
        }


        // import com.example.obracheck_frontend.ui.progress.ProgressFormScreen
        composable(
            route = NavRoutes.PROGRESS_FORM,
            arguments = listOf(
                navArgument("siteId") { type = NavType.LongType },
                navArgument("workerId") { type = NavType.LongType },
                navArgument("editId") { defaultValue = -1L }
            )
        ) { backStackEntry ->
            val siteId = backStackEntry.arguments?.getLong("siteId") ?: -1L
            val workerId = backStackEntry.arguments?.getLong("workerId") ?: -1L
            val editId = backStackEntry.arguments?.getLong("editId")?.takeIf { it != -1L }

            ProgressFormScreen(
                siteId = siteId,
                workerId = workerId,
                editId = editId,
                navController = navController,
                viewModel = progressViewModel
            )
        }

        // 🟣 LISTA DE EVIDENCIAS POR PROGRESS
        composable(
            route = NavRoutes.EVIDENCE_LIST,
            arguments = listOf(
                navArgument("progressId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val progressId = backStackEntry.arguments?.getLong("progressId") ?: -1L

            EvidenceListScreen(
                progressId = progressId,
                navController = navController,
                viewModel = evidenceViewModel   // ✅ antes estaba null
            )
        }

        // 🟣 FORM DE EVIDENCIA (crear/editar)
        composable(
            route = NavRoutes.EVIDENCE_FORM,
            arguments = listOf(
                navArgument("progressId") { type = NavType.LongType },
                navArgument("editId") { defaultValue = -1L }
            )
        ) { backStackEntry ->
            val progressId = backStackEntry.arguments?.getLong("progressId") ?: -1L
            val editId = backStackEntry.arguments?.getLong("editId")?.takeIf { it != -1L }

            EvidenceFormScreen(
                progressId = progressId,
                editId = editId,               // null => crear; != null => editar
                navController = navController,
                viewModel = evidenceViewModel
            )
        }




        // 🟣 LISTA DE PROGRESOS POR WORKER
        composable(
            route = NavRoutes.PROGRESS_LIST,
            arguments = listOf(
                navArgument("siteId") { type = NavType.LongType },
                navArgument("workerId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val siteId = backStackEntry.arguments?.getLong("siteId") ?: -1L
            val workerId = backStackEntry.arguments?.getLong("workerId") ?: -1L

            ProgressListScreen(
                siteId = siteId,
                workerId = workerId,
                navController = navController,
                viewModel = progressViewModel
            )
        }
    }
}