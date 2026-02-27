package com.cassiobruzasco.customscreenpoc

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

/**
 * Callback para a tela customizada devolver o controle da navegação ao SDK.
 */
typealias ResumeSdkFlowCallback = (nextSdkRouteInstance: SdkRoutes) -> Unit

/**
 * Definição de um "Slot" do Compose para injeção de telas de terceiros.
 */
typealias ExternalCustomScreen = @Composable (resumeFlow: ResumeSdkFlowCallback) -> Unit

/**
 * Interface que permite ao cliente alterar o destino natural do fluxo do SDK.
 */
interface SdkFlowInterceptor {
    fun overrideNextRoute(currentRoute: SdkRoutes, defaultNextRoute: SdkRoutes): SdkRoutes
}

/**
 * Configuração injetada pelo app cliente na inicialização do SDK.
 */
data class SdkUiNavigationConfig(
    val interceptor: SdkFlowInterceptor? = null,
    val customScreens: Map<KClass<out SdkRoutes>, ExternalCustomScreen> = emptyMap()
)