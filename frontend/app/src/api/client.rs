use std::{error::Error, fmt};

use backend_api::api::{LoginRequest, LoginResponse, ResponseDto, response_dto};
use gloo_net::http::Request;
use prost::Message;

const BASE_URL: &str = "http://127.0.0.1:8080";

#[derive(Debug)]
pub enum ClientError {
    Network(String),
    InvalidResponse(String),
    Server { status: u16, message: String },
    UnexpectedResponse,
}

impl fmt::Display for ClientError {
    fn fmt(&self, formatter: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Network(message) => write!(formatter, "network error: {message}"),
            Self::InvalidResponse(message) => {
                write!(formatter, "invalid server response: {message}")
            }
            Self::Server { status, message } => {
                write!(formatter, "server returned {status}: {message}")
            }
            Self::UnexpectedResponse => {
                formatter.write_str("server returned an unexpected response")
            }
        }
    }
}

impl Error for ClientError {}

pub async fn login(email: String, password: String) -> Result<LoginResponse, ClientError> {
    let request = LoginRequest { email, password };

    let response = Request::post(&format!("{BASE_URL}/api/login"))
        .header("Content-Type", "application/octet-stream")
        .body(request.encode_to_vec())
        .map_err(|error| ClientError::Network(error.to_string()))?
        .send()
        .await
        .map_err(|error| ClientError::Network(error.to_string()))?;

    let status = response.status();

    let bytes = response
        .binary()
        .await
        .map_err(|error| ClientError::Network(error.to_string()))?;
    let response = ResponseDto::decode(bytes.as_slice())
        .map_err(|error| ClientError::InvalidResponse(error.to_string()))?;

    if let Some(error) = response.error_message_dto {
        return Err(ClientError::Server {
            status,
            message: error.message,
        });
    }

    if !(200..300).contains(&status) {
        return Err(ClientError::Server {
            status,
            message: "request failed".to_owned(),
        });
    }

    match response.body {
        Some(response_dto::Body::LoginResponse(login_response)) => Ok(login_response),
        _ => Err(ClientError::UnexpectedResponse),
    }
}
