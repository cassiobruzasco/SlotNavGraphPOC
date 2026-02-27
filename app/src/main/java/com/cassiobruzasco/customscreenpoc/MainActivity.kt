package com.cassiobruzasco.customscreenpoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                // 1. O cliente cria sua configuração injetando a regra de desvio
                val mySdkConfig = SdkUiNavigationConfig(
                    interceptor = object : SdkFlowInterceptor {
                        override fun overrideNextRoute(currentRoute: SdkRoutes, defaultNextRoute: SdkRoutes): SdkRoutes {
                            // REGRA DE NEGÓCIO DO CLIENTE:
                            // Se o SDK está saindo da Tela 2 e indo pra 3, desvie para a minha Tela Customizada.
                            if (currentRoute == SdkRoutes.Screen2 && defaultNextRoute == SdkRoutes.Screen3) {
                                return CustomClientRoute
                            }
                            return defaultNextRoute
                        }
                    },
                    customScreens = mapOf(
                        CustomClientRoute::class to { resumeFlow ->
                            // Renderiza o composable da Tela Customizada
                            ClientCustomScreen(
                                onFinished = {
                                    // Cliente devolve o controle mandando o SDK ir para a Tela 4
                                    resumeFlow(SdkRoutes.Screen3)
                                }
                            )
                        }
                    )
                )

                // 2. Inicializa o SDK passando a configuração
                SdkNavGraph(navController = navController, sdkConfig = mySdkConfig)
            }
        }
    }
}

@Serializable
object CustomClientRoute : SdkRoutes

// Composable criado 100% pelo cliente fora do módulo do SDK
@Composable
fun ClientCustomScreen(onFinished: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tela - Customizada pelo Cliente",
            fontSize = 24.sp,
            color = Color.Blue
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onFinished) {
            Text("Resume SDK Flow (Go to 4)")
        }
    }
}