package com.aivanovski.leetcode.android.di

import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.data.settings.Settings
import com.aivanovski.leetcode.android.data.settings.SettingsImpl
import com.aivanovski.leetcode.android.domain.ProblemHtmlFormatter
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ThemeProvider
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ThemeProviderImpl
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.navigation.RouterImpl
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProviderImpl
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsCellFactory
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsInteractor
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsArgs
import com.aivanovski.leetcode.android.presentation.problemList.ProblemListCellFactory
import com.aivanovski.leetcode.android.presentation.problemList.ProblemListViewModel
import com.aivanovski.leetcode.android.presentation.quiz.QuizCellFactory
import com.aivanovski.leetcode.android.presentation.quiz.QuizInteractor
import com.aivanovski.leetcode.android.presentation.quiz.QuizViewModel
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
        singleOf(::ApiClient)
        singleOf(::RouterImpl).bind(Router::class)
        singleOf(::ThemeProviderImpl).bind(ThemeProvider::class)
        singleOf(::ProblemHtmlFormatter)

        // Cell factories
        singleOf(::SettingsCellFactory)
        singleOf(::QuizCellFactory)
        singleOf(::ProblemListCellFactory)
        singleOf(::ProblemDetailsCellFactory)

        // Interactors
        singleOf(::SettingsInteractor)
        singleOf(::QuizInteractor)
        singleOf(::ProblemDetailsInteractor)

        // ViewModels
        factory { (args: ProblemDetailsArgs) ->
            ProblemDetailsViewModel(get(), get(), get(), args)
        }
        factory { ProblemListViewModel(get(), get(), get(), get()) }
        factory { RootViewModel(get()) }
        factory { SettingsViewModel(get(), get(), get(), get()) }
        factory { QuizViewModel(get(), get(), get(), get()) }
    }
}