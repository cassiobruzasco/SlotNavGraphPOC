package com.cassiobruzasco.customscreenpoc

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SdkNavGraph(
    navController: NavHostController,
    sdkConfig: SdkUiNavigationConfig
) {
    // Função auxiliar para processar a interceptação de rotas mantendo a lógica DRY
    fun navigateWithInterceptor(currentRoute: SdkRoutes, defaultNextRoute: SdkRoutes) {
        val targetRouteInstance = sdkConfig.interceptor?.overrideNextRoute(
            currentRoute = currentRoute,
            defaultNextRoute = defaultNextRoute
        ) ?: defaultNextRoute

        // Navega passando a instância do objeto serializado
        navController.navigate(targetRouteInstance)
    }


    NavHost(
        navController = navController,
        startDestination = SdkRoutes.Screen1
    ) {

        composable<SdkRoutes.Screen1> {
            SdkGenericScreen(title = "Tela 1 (SDK Native)") {
                navigateWithInterceptor(
                    currentRoute = SdkRoutes.Screen1,
                    defaultNextRoute = SdkRoutes.Screen2
                )
            }
        }

        composable<SdkRoutes.Screen2> {
            SdkGenericScreen(title = "Tela 2 (SDK Native)") {
                // Aqui o fluxo natural do SDK seria ir para a Tela 3.
                // Mas o interceptor vai checar essa transição.
                navigateWithInterceptor(
                    currentRoute = SdkRoutes.Screen2,
                    defaultNextRoute = SdkRoutes.Screen3
                )
            }
        }

        composable<SdkRoutes.Screen3> {
            // Última tela do SDK. Não há próxima rota.
            SdkGenericScreen(title = "Tela 3 (SDK Native) - End of Flow") {
                navController.navigate(SdkRoutes.Screen1) {
                    popUpTo(SdkRoutes.Screen1) { inclusive = true }
                }
            }
        }


        /**
         * Injeção de tals customizadas em slots do NavGraph
         */
        sdkConfig.customScreens.forEach { (customRoute, customScreenContent) ->
            composable(route = customRoute) {
                // Renderiza a tela criada pelo app cliente.
                customScreenContent { nextSdkRouteInstance ->
                    // Quando o cliente termina, ele passa para qual tela do SDK quer voltar/avançar.
                    navController.navigate(nextSdkRouteInstance) {
                        // Limpa a tela customizada do backstack (ou configure para não)
                        popUpTo(customRoute) { inclusive = true }
                    }
                }
            }
        }
    }
}

