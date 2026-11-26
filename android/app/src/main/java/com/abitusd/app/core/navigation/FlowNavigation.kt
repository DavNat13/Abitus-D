package com.abitusd.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.abitusd.app.feature.auth.ui.WelcomeScreen
import com.abitusd.app.feature.auth.ui.login.LoginScreen
import com.abitusd.app.feature.auth.ui.recovery.ForgotPasswordScreen
import com.abitusd.app.feature.auth.ui.recovery.ResetPasswordScreen
import com.abitusd.app.feature.auth.ui.register.RegisterScreen
import com.abitusd.app.feature.auth.ui.verify.VerifyScreen
import com.abitusd.app.feature.home.ui.MainShell
import com.abitusd.app.feature.legal.LegalDocument
import com.abitusd.app.feature.legal.LegalDocumentScreen
import com.abitusd.app.feature.library.ui.detail.ExerciseDetailScreen
import com.abitusd.app.feature.library.ui.form.OwnExerciseFormScreen
import com.abitusd.app.feature.onboarding.ui.AthleteScreen
import com.abitusd.app.feature.profile.ui.ProfileScreen
import com.abitusd.app.feature.train.ui.WorkoutActions
import com.abitusd.app.feature.train.ui.programs.ProgramEditorScreen
import com.abitusd.app.feature.train.ui.routines.RoutineEditorScreen

@Composable
private fun FlowDisplay(
    backStack: NavBackStack<NavKey>,
    content: androidx.navigation3.runtime.EntryProviderScope<NavKey>.() -> Unit,
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators =
            listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()),
        entryProvider = entryProvider(builder = content),
    )
}

/** Welcome, registration, verification, sign in and recovery (05-UI-UX.md, Acceso). */
@Composable
fun AuthNavigation() {
    val stack = rememberNavBackStack(WelcomeRoute)
    val back: () -> Unit = { stack.removeLastOrNull() }
    FlowDisplay(stack) {
        entry<WelcomeRoute> {
            WelcomeScreen(onCreateAccount = { stack.add(RegisterRoute) }, onSignIn = { stack.add(LoginRoute()) })
        }
        entry<RegisterRoute> {
            RegisterScreen(
                onBack = back,
                onOpenTerms = { stack.add(LegalRoute(LegalDocument.TERMS)) },
                onOpenPrivacy = { stack.add(LegalRoute(LegalDocument.PRIVACY)) },
                onRegistered = { email -> stack.add(VerifyRoute(email)) },
            )
        }
        entry<VerifyRoute> { key -> VerifyScreen(email = key.email, onBack = back) }
        entry<LoginRoute> { key ->
            LoginScreen(
                passwordWasReset = key.passwordWasReset,
                onBack = back,
                onForgotPassword = { stack.add(ForgotPasswordRoute) },
                onCreateAccount = { stack.add(RegisterRoute) },
                onNeedsVerification = { email -> stack.add(VerifyRoute(email)) },
            )
        }
        entry<ForgotPasswordRoute> {
            ForgotPasswordScreen(onBack = back, onCodeSent = { email -> stack.add(ResetPasswordRoute(email)) })
        }
        entry<ResetPasswordRoute> { key ->
            ResetPasswordScreen(email = key.email, onBack = back, onDone = {
                stack.clear()
                stack.addAll(listOf(WelcomeRoute, LoginRoute(passwordWasReset = true)))
            })
        }
        entry<LegalRoute> { key -> LegalDocumentScreen(document = key.document, onBack = back) }
    }
}

/** Signed-in app: main shell with the bottom bar, profile and legal documents. */
@Composable
fun MainNavigation() {
    val stack = rememberNavBackStack(MainRoute)
    val back: () -> Unit = { stack.removeLastOrNull() }
    androidx.compose.runtime.CompositionLocalProvider(
        com.abitusd.app.core.designsystem.component.LocalOpenGlossary provides { stack.add(GlossaryRoute) },
        com.abitusd.app.core.designsystem.component.LocalOpenClock provides { stack.add(ClockRoute) },
    ) { MainEntries(stack, back) }
}

@Composable
private fun MainEntries(
    stack: NavBackStack<NavKey>,
    back: () -> Unit,
) {
    FlowDisplay(stack) {
        entry<MainRoute> {
            MainShell(
                onOpenProfile = { stack.add(ProfileRoute) },
                onOpenClock = { stack.add(ClockRoute) },
                onOpenExercise = { stack.add(ExerciseDetailRoute(it)) },
                onCreateExercise = { stack.add(OwnExerciseFormRoute()) },
                onOpenRoutine = { stack.add(RoutineEditorRoute(it)) },
                onOpenProgram = { stack.add(ProgramEditorRoute(it)) },
                workout =
                    WorkoutActions({ r, sch -> stack.add(PreSessionRoute(r, sch)) }, {
                        stack.add(ActiveSessionRoute)
                    }, { stack.add(HistoryRoute) }),
            )
        }
        entry<ExerciseDetailRoute> { key ->
            ExerciseDetailScreen(
                id = key.id,
                onBack = back,
                onOpenExercise = { stack.add(ExerciseDetailRoute(it)) },
                onEdit = { stack.add(OwnExerciseFormRoute(it)) },
            )
        }
        entry<OwnExerciseFormRoute> { key ->
            OwnExerciseFormScreen(
                editId = key.editId,
                onBack = back,
                onSaved = { id ->
                    stack.removeLastOrNull()
                    if (key.editId == null) stack.add(ExerciseDetailRoute(id))
                },
                onOpenExisting = { stack.add(ExerciseDetailRoute(it)) },
            )
        }
        entry<RoutineEditorRoute> { key -> RoutineEditorScreen(key.id, onBack = back) }
        entry<ProgramEditorRoute> { key -> ProgramEditorScreen(key.id, onBack = back) }
        workoutEntries(stack, back)
        clockEntries(back)
        entry<ProfileRoute> {
            ProfileScreen(
                onBack = back,
                onOpenAthlete = { stack.add(AthleteRoute) },
                onOpenGlossary = { stack.add(GlossaryRoute) },
                onOpenPrivacy = { stack.add(LegalRoute(LegalDocument.PRIVACY)) },
                onOpenTerms = { stack.add(LegalRoute(LegalDocument.TERMS)) },
                onOpenCredits = { stack.add(LegalRoute(LegalDocument.CREDITS)) },
            )
        }
        entry<AthleteRoute> { AthleteScreen(onDone = back) }
        entry<LegalRoute> { key -> LegalDocumentScreen(document = key.document, onBack = back) }
    }
}
