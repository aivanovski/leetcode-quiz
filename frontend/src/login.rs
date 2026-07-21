use leptos::{ev::SubmitEvent, prelude::*, task::spawn_local};
use leptos_router::{NavigateOptions, hooks::use_navigate};
use std::os::macos::raw::stat;
use std::time::Duration;

#[derive(Clone)]
struct LoginState {
    username: RwSignal<String>,
    password: RwSignal<String>,
    error: RwSignal<Option<String>>,
    is_loading: RwSignal<bool>,
}

impl LoginState {
    fn new() -> Self {
        Self {
            username: RwSignal::new(String::new()),
            password: RwSignal::new(String::new()),
            is_loading: RwSignal::new(false),
            error: RwSignal::new(None),
        }
    }
}

#[component]
pub fn LoginPage() -> impl IntoView {
    let state = LoginState::new();

    view! {
        <main class="login-page">
            <section class="form-panel">
                <form on:submit=move |event| submit_login(event, &state) novalidate>
                        <label for="username">"Username"</label>
                        <input
                            id="username"
                            name="username"
                            type="text"
                            autocomplete="username"
                            autofocus
                            placeholder="Enter your username"
                            prop:value=move || state.username.get()
                            on:input=move |event| {
                                state.username.set(event_target_value(&event));
                                state.error.set(None);
                            }
                        />

                        <div class="password-label">
                            <label for="password">"Password"</label>
                        </div>
                        <input
                            id="password"
                            name="password"
                            type="password"
                            autocomplete="current-password"
                            placeholder="Enter your password"
                            prop:value=move || state.password.get()
                            on:input=move |event| {
                                state.password.set(event_target_value(&event));
                                state.error.set(None);
                            }
                        />

                        <Show when=move || state.error.get().is_some()>
                            <p class="form-error" role="alert">
                                {move || state.error.get().unwrap_or_default()}
                            </p>
                        </Show>

                        <button type="submit" disabled=move || state.is_loading.get()>
                            <Show
                                when=move || !state.is_loading.get()
                                fallback=|| view! { <span class="loader" aria-label="Logging in"></span> }
                            >
                                "Log in"
                            </Show>
                        </button>
                    </form>
            </section>
        </main>
    }
}

fn submit_login(event: SubmitEvent, state: &LoginState) {
    event.prevent_default();

    let username = state.username.get().trim().to_owned();
    let password = state.password.get().trim().to_owned();

    if username.is_empty() {
        state.error.set(Some("Enter your username.".to_string()));
        return;
    }

    if password.is_empty() {
        state.error.set(Some("Enter your password.".to_string()));
        return;
    }

    state.error.set(None);
    state.is_loading.set(true);

    let error = state.error;
    let is_loading = state.is_loading;

    set_timeout(
        move || {
            error.set(Some("Login is currently unavailable".to_string()));
            is_loading.set(true);
        },
        Duration::from_secs(2),
    );
}
