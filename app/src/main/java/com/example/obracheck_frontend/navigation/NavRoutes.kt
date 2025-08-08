package com.example.obracheck_frontend.navigation

object NavRoutes {
    const val LOGIN = "login"
    const val WELCOME = "welcome/{userId}/{name}"
    const val SITE_LIST = "sitelist/{userId}"
    const val SITE_FORM = "siteform?id={id}&userId={userId}"
    const val WORKER_LIST = "workerlist/{siteId}"
    const val WORKER_FORM = "workerform/{siteId}?editId={editId}"


}
