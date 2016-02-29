package de.filiberry.weather.model;

public class WS2300Model {

	private float temperatur;
	private int humidity;
	private float dewpoint;
	private float wind;
	private float windchill;
	private float rain;
	private float pressure;
	private String pressureTendency;
	private String forecast;

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public float getDewpoint() {
		return dewpoint;
	}

	public void setDewpoint(float dewpoint) {
		this.dewpoint = dewpoint;
	}

	public float getWind() {
		return wind;
	}

	public void setWind(float wind) {
		this.wind = wind;
	}

	public float getWindchill() {
		return windchill;
	}

	public void setWindchill(float windchill) {
		this.windchill = windchill;
	}

	public float getRain() {
		return rain;
	}

	public void setRain(float rain) {
		this.rain = rain;
	}

	public String getForecast() {
		return forecast;
	}

	public void setForecast(String forecast) {
		this.forecast = forecast;
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure;
	}

	public String getPressureTendency() {
		return pressureTendency;
	}

	public void setPressureTendency(String pressureTendency) {
		this.pressureTendency = pressureTendency;
	}

	public float getTemperatur() {
		return temperatur;
	}

	public void setTemperatur(float temperatur) {
		this.temperatur = temperatur;
	}

}
