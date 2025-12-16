use std::{env, io};

use weather_proxy::WeatherClient;

#[tokio::main]
async fn main() -> Result<(), anyhow::Error> {
    _ = dotenvy::dotenv();
    let api_key = env::var("OPENWEATHERMAP_API_KEY")?;

    let client = WeatherClient::new(api_key);

    loop {
        let mut buf = String::new();
        io::stdin().read_line(&mut buf)?;

        let params: Vec<&str> = buf.split_ascii_whitespace().collect();

        match params[..] {
            ["geocode", query] => {
                let results = client.get_geo_info(query).await?;
                println!("{}", results.len());
                for r in results {
                    println!("{}", r.name);
                    println!("{}", r.lat);
                    println!("{}", r.lon);
                }
            }
            ["weather", lat, lon] => {
                let result = client.get_weather_data(lat.parse()?, lon.parse()?).await?;
                println!("{}", result.time);
                println!("{}", result.icon);
                println!("{}", result.temperature);
                println!("{}", result.feels_like);
                println!("{}", result.condition);
            }
            ["forecast", lat, lon] => {
                let results = client.get_forecast(lat.parse()?, lon.parse()?).await?;
                println!("{}", results.len());
                for r in results {
                    println!("{}", r.time);
                    println!("{}", r.icon);
                    println!("{}", r.temperature);
                    println!("{}", r.feels_like);
                    println!("{}", r.condition);
                }
            }
            _ => println!("unknown command"),
        }
    }

    Ok(())
}
