# AGENTS.md

## Project Overview

This repository contains two related applications:

- `backend/`: Scala 3 backend built with sbt, zio, zio-http, Slick, SQLite.
- `android/`: Android app built with Gradle/Kotlin, Jetpack Compose, Room, Koin, Ktor client, Arrow `Either`, and a generated `:backend-api` module.

The Scala backend uses protobuf to define it's API, the schema located in `backend/api` project

## Common Commands

Run backend commands from `backend/`:

- Compile backend app: `sbt app/compile`
- Run backend tests: `sbt app/test`
- Compile API client: `sbt apiClient/compile`
- Build backend fat jar: `sbt app/assembly`

Run Android commands from `android/`:

- Compile debug Kotlin: `./gradlew :app:compileDebugKotlin`
- Run ktlint: `./gradlew ktlintCheck`
- Apply ktlint formatting: `./gradlew ktlintFormat`
- Build debug APK: `./gradlew :app:assembleDebug`

Use targeted compile/test commands for small changes when possible, then broaden verification when touching shared contracts, dependency injection, persistence, networking, or navigation.

## Scala Backend Guidelines

- Prefer the existing ZIO style and layer structure. Services, repositories, controllers, routes, jobs, and use cases are wired through `Layers.scala`.
- For ZIO code, prefer direct style with `defer { ... }` from `zio.direct.*` when it makes the flow clearer.
- Keep DTOs in `backend/api` simple and serializable with `zio-json`; update generated Kotlin DTOs when DTO contracts change.

## Android Guidelines

- UI is Jetpack Compose. Reuse existing theme primitives in `presentation/core/compose/theme` and shared cell components in `presentation/core/compose/cells`.
- Presentation code generally follows Screen/ViewModel/Interactor/model conventions. Keep data loading and business logic in interactors/repositories, not in composables.
- Register new dependencies in `di/KoinModule.kt` when introducing injectable classes.
- Do not hand-edit generated files in `android/backend-api`;

## Generated Code And Data

- Treat `android/backend-api` as generated from `backend/api`.
- Treat `backend/app-data`, `backend/dev-data`, `.gradle`, `.bloop`, `.metals`, `target`, and build outputs as local/generated data.
