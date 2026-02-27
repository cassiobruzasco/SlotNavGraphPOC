package com.cassiobruzasco.customscreenpoc

import kotlinx.serialization.Serializable

@Serializable
sealed interface SdkRoutes {

    @Serializable
    data object Screen1 : SdkRoutes

    @Serializable
    data object Screen2 : SdkRoutes

    @Serializable
    data object Screen3 : SdkRoutes
}