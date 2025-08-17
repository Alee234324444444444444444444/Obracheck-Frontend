package com.example.obracheck_frontend.navigation

object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val WELCOME = "welcome/{userId}/{name}"
    const val SITE_LIST = "sitelist/{userId}"
    const val SITE_FORM = "siteform?id={id}&userId={userId}"
    const val WORKER_LIST = "workerlist/{siteId}"
    const val WORKER_FORM = "workerform/{siteId}?editId={editId}"
    const val ATTENDANCE_LIST = "attendance/{siteId}/{date}"
    const val PROGRESS_LIST = "progresslist/{siteId}/{workerId}"
    const val PROGRESS_FORM = "progressform/{siteId}/{workerId}?editId={editId}"
    const val EVIDENCE_LIST = "evidencelist/{progressId}"
    const val EVIDENCE_FORM = "evidenceform/{progressId}?editId={editId}"

}
