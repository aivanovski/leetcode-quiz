//! Rust types generated from the backend's protobuf API schema.

pub mod api {
    include!(concat!(
        env!("OUT_DIR"),
        "/com.github.ai.leetcodequiz.api.rs"
    ));
}

