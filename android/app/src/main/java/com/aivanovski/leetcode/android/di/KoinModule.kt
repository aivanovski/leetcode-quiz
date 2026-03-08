package com.aivanovski.leetcode.android.di

import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.data.database.AppDatabase
import com.aivanovski.leetcode.android.data.database.dao.ProblemEntityDao
import com.aivanovski.leetcode.android.data.database.dao.SyncEntityDao
import com.aivanovski.leetcode.android.data.repository.AuthRepository
import com.aivanovski.leetcode.android.data.repository.ProblemRepository
import com.aivanovski.leetcode.android.data.settings.Settings
import com.aivanovski.leetcode.android.data.settings.SettingsImpl
import com.aivanovski.leetcode.android.data.settings.encryption.DataCipherProvider
import com.aivanovski.leetcode.android.data.settings.encryption.DataCipherProviderImpl
import com.aivanovski.leetcode.android.domain.ProblemHtmlFormatter
import com.aivanovski.leetcode.android.domain.usecases.GetDebugCredentialsUseCase
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ThemeProvider
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ThemeProviderImpl
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.navigation.RouterImpl
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProviderImpl
import com.aivanovski.leetcode.android.presentation.login.LoginCellFactory
import com.aivanovski.leetcode.android.presentation.login.LoginInteractor
import com.aivanovski.leetcode.android.presentation.login.LoginViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsCellFactory
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsInteractor
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsArgs
import com.aivanovski.leetcode.android.presentation.problemList.ProblemListCellFactory
import com.aivanovski.leetcode.android.presentation.problemList.ProblemListInteractor
import com.aivanovski.leetcode.android.presentation.problemList.ProblemListViewModel
import com.aivanovski.leetcode.android.presentation.quiz.QuizCellFactory
import com.aivanovski.leetcode.android.presentation.quiz.QuizInteractor
import com.aivanovski.leetcode.android.presentation.quiz.QuizViewModel
import com.aivanovski.leetcode.android.presentation.quizStart.QuizStartInteractor
import com.aivanovski.leetcode.android.presentation.quizStart.QuizStartViewModel
import com.aivanovski.leetcode.android.presentation.root.RootInteractor
import com.aivanovski.leetcode.android.presentation.root.RootViewModel
import com.aivanovski.leetcode.android.presentation.settings.SettingsCellFactory
import com.aivanovski.leetcode.android.presentation.settings.SettingsInteractor
import com.aivanovski.leetcode.android.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object KoinModule {

    val module = module {
        singleOf(::ResourceProviderImpl).bind(ResourceProvider::class)
        singleOf(::SettingsImpl).bind(Settings::class)
        singleOf(::DataCipherProviderImpl).bind(DataCipherProvider::class)
        singleOf(::ApiClient)
        singleOf(::RouterImpl).bind(Router::class)
        singleOf(::ThemeProviderImpl).bind(ThemeProvider::class)
        singleOf(::ProblemHtmlFormatter)

        // Database
        single { AppDatabase.buildDatabase(get()) }
        single { provideProblemDao(get()) }
        single { provideSyncEntityDao(get()) }

        // UseCases
        singleOf(::GetDebugCredentialsUseCase)

        // Repositories
        singleOf(::ProblemRepository)
        singleOf(::AuthRepository)

        // Cell factories
        singleOf(::SettingsCellFactory)
        singleOf(::QuizCellFactory)
        singleOf(::ProblemListCellFactory)
        singleOf(::ProblemDetailsCellFactory)
        singleOf(::LoginCellFactory)

        // Interactors
        singleOf(::RootInteractor)
        singleOf(::SettingsInteractor)
        singleOf(::QuizInteractor)
        singleOf(::ProblemDetailsInteractor)
        singleOf(::ProblemListInteractor)
        singleOf(::QuizStartInteractor)
        singleOf(::LoginInteractor)

        // ViewModels
        factory { (args: ProblemDetailsArgs) ->
            ProblemDetailsViewModel(get(), get(), get(), get(), args)
        }
        factory { ProblemListViewModel(get(), get(), get(), get()) }
        factory { RootViewModel(get(), get()) }
        factory { SettingsViewModel(get(), get(), get(), get()) }
        factory { QuizViewModel(get(), get(), get(), get()) }
        factory { QuizStartViewModel(get(), get(), get()) }
        factory { LoginViewModel(get(), get(), get(), get()) }
    }

    private fun provideProblemDao(db: AppDatabase): ProblemEntityDao = db.problemDao
    private fun provideSyncEntityDao(db: AppDatabase): SyncEntityDao = db.syncEntityDao
}