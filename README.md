# SlotNavGraphPOC
This is a POC to illustrate how to introduce custom screen between a closed nav graph flow.

## 🔀 Dynamic Navigation Strategy (Type-Safe + Slot API)
One of the greatest strengths of this SDK is its ability to be extended without internal modifications, strictly adhering to the Open/Closed Principle (closed for modification, open for extension).

- We achieve this by combining Jetpack Compose Navigation (Type-Safe) via `kotlinx.serialization` with the Interceptor Pattern and the Compose Slot API.

### How it Works
- NavHostController Protection: The navigation engine is strictly internal. Predictable navigation dictates that triggers cannot happen from the layout layer. External clients cannot arbitrarily manipulate the SDK's backstack.

- The Interceptor (SdkFlowInterceptor): The host application provides a business rule configuration upon SDK initialization. Whenever the SDK attempts to navigate from Route A to Route B, the interceptor is consulted. The client can intercept this transition and redirect the flow to a custom Route C.

- Dynamic Slot API: Custom client screens are injected into the SDK via `SdkUiNavigationConfig` using class references `(KClass<out SdkRoutes>)`. The SDK natively renders this third-party UI inside its own NavGraph.

- Safe Flow Resumption (ResumeSdkFlowCallback): Once the injected client screen finishes its task, it invokes a callback passing the instance of the next desired SDK destination. The SDK securely clears the injected screen from the backstack and resumes its original flow.

```
// 1. The client defines a serializable custom route
@Serializable
object LoyaltyCustomRoute : SdkRoutes

// 2. The client injects the configuration during initialization
val sdkConfig = SdkUiNavigationConfig(
    interceptor = object : SdkFlowInterceptor {
        override fun overrideNextRoute(currentRoute: SdkRoutes, defaultNextRoute: SdkRoutes): SdkRoutes {
            // Intercepts the flow: if leaving Screen2 and heading to Screen3, go to Loyalty instead
            if (currentRoute is SdkRoutes.Screen2 && defaultNextRoute is SdkRoutes.Screen3) {
                return LoyaltyCustomRoute
            }
            return defaultNextRoute
        }
    },
    customScreens = mapOf(
        LoyaltyCustomRoute::class to { resumeFlow ->
            // Render the custom Compose screen
            MyLoyaltyScreen(
                onFinished = { 
                    // Return control to the SDK, providing the target destination instance
                    resumeFlow(SdkRoutes.Screen3) 
                } 
            )
        }
    )
)
```

## ⚡ Performance, Security, and Scalability
This architecture is designed to create a robust, scalable, and maintainable codebase, particularly tailored for the critical environment of financial and payment applications:

- Memory & State Isolation: By keeping the NavHostController private, external clients cannot leak it or manipulate the navigation history to force the SDK back into sensitive states (e.g., card processing screens), preventing race conditions and duplicated transactions.

- Optimized Recomposition: By leveraging StateFlow with .update and operators like combine in our ViewModels, we guarantee that the UI only recomposes when the aggregated data changes. State updates are atomic and thread-safe.

- Compile-Time Type Safety: Utilizing Compose Navigation 2.8.0+ eliminates brittle string-based routes (e.g., "route/123"). All navigation relies on serialized objects, ensuring that missing routes or invalid arguments are caught at compile-time rather than causing runtime crashes for the end-user.

- Automatic Backstack Cleanup: Utilizing popUpTo(routeClass) { inclusive = true } when resuming the flow after a custom injected screen ensures immediate memory release. It also prevents the user from inappropriately returning to the custom screen via the Android hardware back button.

- Trivial Testability: Isolating business rules inside Use Cases and maintaining pure state transitions within ViewModel Reducers makes writing unit tests straightforward and highly reliable.
