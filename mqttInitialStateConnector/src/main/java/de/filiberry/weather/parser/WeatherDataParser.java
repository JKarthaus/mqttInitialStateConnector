package de.filiberry.weather.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.filiberry.weather.initialState.Data;
import de.filiberry.weather.model.WS2300Model;

public class WeatherDataParser {

	private Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * 
	 * @param rawData
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	public WS2300Model getDataModel(String rawData) throws JDOMException, IOException {
		WS2300Model result = new WS2300Model();
		SAXBuilder builder = new SAXBuilder();
		log.info("Parse XML Weater Data to Model");
		Document document;
		document = builder.build(new StringReader(rawData));
		Element rootNode = document.getRootElement();
		result.setDewpoint(new Float(rootNode.getChild("Dewpoint").getChild("Value").getText()));
		result.setForecast(rootNode.getChildText("Forecast"));
		result.setHumidity(new Integer(rootNode.getChild("Humidity").getChild("Outdoor").getChild("Value").getText()));
		result.setPressure(new Float(rootNode.getChild("Pressure").getChild("Value").getText()));
		result.setPressureTendency(rootNode.getChild("Pressure").getChild("Tendency").getText());
		result.setRain(new Float(rootNode.getChild("Rain").getChild("OneHour").getChildText("Value")));
		result.setTemperatur(new Float(rootNode.getChild("Temperature").getChild("Outdoor").getChildText("Value")));
		result.setWind(new Float(rootNode.getChild("Wind").getChildText("Value")));
		result.setWindchill(new Float(rootNode.getChild("Windchill").getChildText("Value")));
		return result;
	}

	/**
	 * 
	 * @param rawData
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Data[] getInitialStateData(String rawData) throws JDOMException, IOException {
		WS2300Model ws2300Model = getDataModel(rawData);

		Data[] result = new Data[9];
		result[0] = new Data("taupunkt", ws2300Model.getDewpoint());
		result[1] = new Data("vorhersage", ws2300Model.getForecast());
		result[2] = new Data("feuchte", ws2300Model.getHumidity());
		result[3] = new Data("druck", ws2300Model.getPressure());
		result[4] = new Data("drucktendenz", ws2300Model.getPressureTendency());
		result[5] = new Data("regenmenge", ws2300Model.getRain());
		result[6] = new Data("temperatur", ws2300Model.getTemperatur());
		result[7] = new Data("wind", ws2300Model.getWind());
		result[8] = new Data("windchill", ws2300Model.getWindchill());

		return result;
	}

}
