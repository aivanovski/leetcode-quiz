use std::{env, fs, path::PathBuf};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let manifest_dir = PathBuf::from(env::var("CARGO_MANIFEST_DIR")?);
    let schema = manifest_dir.join("../../backend/api/src/main/protobuf/api.proto");

    println!("cargo:rerun-if-changed={}", schema.display());

    // ScalaPB annotations only control Scala code generation. Strip them from a
    // temporary copy so the Rust generator does not need ScalaPB's descriptor.
    let source = fs::read_to_string(&schema)?;
    let mut in_scalapb_options = false;
    let rust_schema = source
        .lines()
        .filter_map(|line| {
            if line.starts_with("import \"scalapb/scalapb.proto\"") {
                return None;
            }
            if line.starts_with("option (scalapb.options)") {
                in_scalapb_options = true;
                return None;
            }
            if in_scalapb_options {
                if line.trim() == "};" {
                    in_scalapb_options = false;
                }
                return None;
            }

            Some(line.replace(" [(scalapb.field).no_box = true]", ""))
        })
        .collect::<Vec<_>>()
        .join("\n");

    let out_dir = PathBuf::from(env::var("OUT_DIR")?);
    let rust_schema_path = out_dir.join("api.proto");
    fs::write(&rust_schema_path, rust_schema)?;

    let mut config = prost_build::Config::new();
    config.protoc_executable(protoc_bin_vendored::protoc_bin_path()?);
    config.compile_protos(&[rust_schema_path], &[out_dir])?;

    Ok(())
}
