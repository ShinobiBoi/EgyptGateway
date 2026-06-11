package com.besha.egyptguide.appcore.navigation

import kotlinx.serialization.Serializable

sealed class ScreenResources {

    @Serializable
    object LoginRoute : ScreenResources()

    @Serializable
    object SignUpRoute : ScreenResources()

    @Serializable
    object ForgotPassword : ScreenResources()

    @Serializable
    object SplashRoute : ScreenResources()

    @Serializable
    object AuthRoute : ScreenResources()

    @Serializable
    object MainRoute : ScreenResources()

    @Serializable
    object HomeRoute : ScreenResources()

    @Serializable
    object CalendarRoute : ScreenResources()

    @Serializable
    object ExploreRoute : ScreenResources()

    @Serializable
    data class MapsRoute(val id: String?= null) : ScreenResources()

    @Serializable
    object ProfileRoute : ScreenResources()

    @Serializable
    object CameraRoute : ScreenResources()

    @Serializable
    object TicketsRoute : ScreenResources()

    @Serializable
    object SubmitTicketRoute : ScreenResources()

    @Serializable
    object ObjectivesRoute : ScreenResources()

    @Serializable
    object MonumentsRoute : ScreenResources()

    @Serializable
    data class MonumentDetailsRoute(
        val monumentId: String
    ) : ScreenResources()

    @Serializable
    data class TicketDetailsRoute(
        val ticketId: String
    ) : ScreenResources()

    @Serializable
    data class LeaderboardRoute(
        val userId: String,
    ) : ScreenResources()

    @Serializable
    data class PlaceDetailsRoute(
        val id: String? = null,
        val displayName: String? = null,
        val formattedAddress: String? = null,
        val imageUri: String? = null,
        val lat: Double = 0.0,
        val lng: Double = 0.0
    ) : ScreenResources()

    @Serializable
    data class QuizRoute(
        val id: String? = null,
        val name: String? = null,
    ) : ScreenResources()

    companion object {
        fun fromRoute(route: String): ScreenResources? {
            return when {
                route.contains(LoginRoute::class.qualifiedName ?: "") -> LoginRoute
                route.contains(SignUpRoute::class.qualifiedName ?: "") -> SignUpRoute
                route.contains(ForgotPassword::class.qualifiedName ?: "") -> ForgotPassword
                route.contains(SplashRoute::class.qualifiedName ?: "") -> SplashRoute
                route.contains(AuthRoute::class.qualifiedName ?: "") -> AuthRoute
                route.contains(MainRoute::class.qualifiedName ?: "") -> MainRoute
                route.contains(HomeRoute::class.qualifiedName ?: "") -> HomeRoute
                route.contains(CalendarRoute::class.qualifiedName ?: "") -> CalendarRoute
                route.contains(ExploreRoute::class.qualifiedName ?: "") -> ExploreRoute
                route.contains(MapsRoute::class.qualifiedName ?: "") -> MapsRoute()
                route.contains(ProfileRoute::class.qualifiedName ?: "") -> ProfileRoute
                route.contains(TicketsRoute::class.qualifiedName ?: "") -> TicketsRoute
                route.contains(SubmitTicketRoute::class.qualifiedName ?: "") -> SubmitTicketRoute
                route.contains(ObjectivesRoute::class.qualifiedName ?: "") -> ObjectivesRoute
                route.contains(MonumentsRoute::class.qualifiedName ?: "") -> MonumentsRoute
                route.contains(MonumentDetailsRoute::class.qualifiedName ?: "") -> MonumentDetailsRoute("")
                route.contains(TicketDetailsRoute::class.qualifiedName ?: "") -> TicketDetailsRoute("")
                route.contains(LeaderboardRoute::class.qualifiedName ?: "") -> LeaderboardRoute("")
                route.contains(PlaceDetailsRoute::class.qualifiedName ?: "") -> PlaceDetailsRoute()
                route.contains(QuizRoute::class.qualifiedName ?: "") -> QuizRoute()
                route.contains(CameraRoute::class.qualifiedName ?: "") -> CameraRoute
                else -> null
            }
        }
    }
}
