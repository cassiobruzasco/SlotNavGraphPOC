package com.cassiobruzasco.customscreenpoc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Tela base "burra" do SDK.
@Composable
internal fun SdkGenericScreen(
    title: String,
    onNextEvent: () -> Unit // Essa callback simula o envio de um Effect (só para não complicar)
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNextEvent) {
            Text(
                text = if (title == "Tela 3 (SDK Native) - End of Flow") "Return" else "Next")
        }
    }
}