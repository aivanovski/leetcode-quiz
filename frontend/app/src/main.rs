use frontend::login::LoginPage;
use leptos::prelude::*;
use leptos_router::{components::*, path};

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
    view! { <LoginPage /> }.into_any()
}

#[component]
fn DashboardRoute() -> impl IntoView {
    view! { }
}
