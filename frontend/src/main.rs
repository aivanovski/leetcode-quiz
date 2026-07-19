mod login;

use leptos::prelude::*;
use leptos_router::{components::*, path};
use crate::login::LoginPage;

fn main() {
    mount_to_body(App)
}

#[component]
fn App() -> impl IntoView {
    // let session = RwSignal::new(load_session());

    view! {
        <Router>
            <Routes fallback=|| view! { <Redirect path="/" /> }>
                <Route
                    path=path!("/")
                    view=move || view! { <LoginRoute /> }
                />
            </Routes>
        </Router>
    }
}

#[component]
fn LoginRoute() -> impl IntoView {
    // if session.get_untracked().is_some() {
    //     view! { <Redirect path="/dashboard" /> }.into_any()
    // } else {
    //     view! { <LoginPage session /> }.into_any()
    // }
    view! { <LoginPage /> }.into_any()
}