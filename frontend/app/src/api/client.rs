use std::{error::Error, fmt};

use backend_api::api::{LoginRequest, LoginResponse, ResponseDto, response_dto};
use gloo_net::http::Request;
use prost::Message;

const BASE_URL: &str = "http://127.0.0.1:8080";

#[derive(Debug)]
pub enum ApiError {
    Network(String),
    InvalidResponse(String),
    Server { status: u16, message: String },
    UnexpectedResponse,
}

impl fmt::Display for ApiError {
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

enum HttpRequest<Body> {
    Get,
    Post { body: Body },
}

pub async fn login(email: String, password: String) -> Result<LoginResponse, ApiError> {
    let request = LoginRequest { email, password };

    send(
        HttpRequest::Post { body: request },
        format!("{BASE_URL}/api/login"),
        |dto| match dto.body {
            Some(response_dto::Body::LoginResponse(login_response)) => Some(login_response),
            _ => None,
        },
    )
        .await
}

async fn send<Req, Resp, Mapper>(
    request: HttpRequest<Req>,
    url: String,
    transform: Mapper,
) -> Result<Resp, ApiError>
where
    Req: Message,
    Mapper: FnOnce(ResponseDto) -> Option<Resp>,
{
    let response = match request {
        HttpRequest::Get => Request::get(url.as_str())
            .send()
            .await
            .map_err(|error| ApiError::Network(error.to_string()))?,

        HttpRequest::Post { body } => Request::post(url.as_str())
            .header("Content-Type", "application/octet-stream")
            .body(body.encode_to_vec())
            .map_err(|error| ApiError::Network(error.to_string()))?
            .send()
            .await
            .map_err(|error| ApiError::Network(error.to_string()))?,
    };

    let status = response.status();

    let bytes = response
        .binary()
        .await
        .map_err(|error| ApiError::Network(error.to_string()))?;

    let response_dto = ResponseDto::decode(bytes.as_slice())
        .map_err(|error| ApiError::InvalidResponse(error.to_string()))?;

    if let Some(error) = response_dto.error_message_dto {
        return Err(ApiError::Server {
            status,
            message: error.message,
        });
    }

    if !(200..300).contains(&status) {
        return Err(ApiError::Server {
            status,
            message: "request failed".to_owned(),
        });
    }

    match transform(response_dto) {
        Some(resp) => Ok(resp),
        None => Err(ApiError::UnexpectedResponse),
    }
}
