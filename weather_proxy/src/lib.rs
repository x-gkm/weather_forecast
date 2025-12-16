use chrono::{DateTime, Local};
use reqwest::Client;
use serde::{Deserialize, Serialize, de::DeserializeOwned};
use url::Url;

pub struct WeatherClient {
    api_key: String,
    client: Client,
}

#[derive(Debug, thiserror::Error)]
pub enum WeatherError {
    #[error("request error: {0}")]
    Request(#[from] reqwest::Error),
}

impl WeatherClient {
    pub fn new(api_key: String) -> WeatherClient {
        WeatherClient {
            api_key,
            client: Client::new(),
        }
    }

    async fn get<T>(&self, path: &str, params: &[(&str, &str)]) -> Result<T, WeatherError>
    where
        T: DeserializeOwned,
    {
        let mut url = Url::parse(&format!("https://api.openweathermap.org/{path}")).unwrap();
        url.query_pairs_mut()
            .extend_pairs(params)
            .append_pair("appid", &self.api_key);

        Ok(self.client.get(url).send().await?.json().await?)
    }

    pub async fn get_geo_info(&self, query: &str) -> Result<Vec<GeoInfo>, WeatherError> {
        Ok(self.get("geo/1.0/direct", &[("q", query), ("limit", "5")]).await?)
    }

    pub async fn get_weather_data(&self, lat: f64, lon: f64) -> Result<WeatherData, WeatherError> {
        Ok(self
            .get::<WeatherDto>(
                "data/2.5/weather",
                &[
                    ("lat", &lat.to_string()),
                    ("lon", &lon.to_string()),
                    ("units", "metric"),
                ],
            )
            .await?
            .into())
    }

    pub async fn get_forecast(&self, lat: f64, lon: f64) -> Result<Vec<WeatherData>, WeatherError> {
        Ok(self
            .get::<WeatherForecastDto>(
                "data/2.5/forecast",
                &[
                    ("lat", &lat.to_string()),
                    ("lon", &lon.to_string()),
                    ("units", "metric"),
                ],
            )
            .await?
            .into())
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct GeoInfo {
    pub name: String,
    pub country: String,
    pub lat: f64,
    pub lon: f64,
}

#[derive(Debug, Clone, Serialize)]
pub struct WeatherData {
    pub temperature: f64,
    pub feels_like: f64,
    pub time: DateTime<Local>,
    pub condition: String,
    pub icon: String,
}

impl From<WeatherDto> for WeatherData {
    fn from(dto: WeatherDto) -> WeatherData {
        WeatherData {
            temperature: dto.main.temp,
            feels_like: dto.main.feels_like,
            time: DateTime::from_timestamp_secs(dto.dt).unwrap().into(),
            condition: dto.weather[0].description.clone(),
            icon: match &dto.weather[0].icon[..2] {
                "01" => "clear",
                "02" => "partly_cloudy",
                "03" | "04" => "clouds",
                "09" | "10" => "rain",
                "11" => "thunderstorm",
                "13" => "snow",
                "50" => "mist",
                _ => unreachable!(),
            }.to_string(),
        }
    }
}

impl From<WeatherForecastDto> for Vec<WeatherData> {
    fn from(dto: WeatherForecastDto) -> Vec<WeatherData> {
        dto.list.into_iter().map(|dto| dto.into()).collect()
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
struct WeatherForecastDto {
    list: Vec<WeatherDto>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
struct WeatherDto {
    dt: i64,
    main: WeatherMainDto,
    weather: Vec<WeatherWeaterDto>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
struct WeatherMainDto {
    temp: f64,
    feels_like: f64,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
struct WeatherWeaterDto {
    description: String,
    icon: String,
}
