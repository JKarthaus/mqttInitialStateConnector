/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.filiberry.mqttInitialStateConnector;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import de.filiberry.weather.initialState.API;
import de.filiberry.weather.initialState.Bucket;
import de.filiberry.weather.initialState.Data;
import de.filiberry.weather.parser.WeatherDataParser;

public class Activator implements BundleActivator, ManagedService, MqttCallback {

	private ServiceRegistration serviceReg;
	private Logger log = Logger.getLogger(this.getClass().getName());
	private API initialStateAccount;
	private WeatherDataParser weatherDataParser;
	private MqttClient client = null;
	private String host;
	private String topic;
	private String apiKey;

	@Override
	public void start(BundleContext context) {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, "mqttInitialStateConnector");
		serviceReg = context.registerService(ManagedService.class.getName(), this, properties);
		weatherDataParser = new WeatherDataParser();
		log.info("The mqttInitialStateConnector Bundle startet.");
	}

	@Override
	public void stop(BundleContext context) {
		log.info("The mqttInitialStateConnector Bundle stopped.");
		if (client != null && client.isConnected()) {
			try {
				client.disconnect();

			} catch (MqttException e) {
				log.warning(e.getMessage());
			}
		}
		client = null;
	}

	/**
	 * 
	 */
	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties == null) {
			log.info("config is null - Please give me a config File");
			return;
		}
		log.info("Mqtt and Initialstate Config was set.");
		this.host = (String) properties.get("mqttHost");
		this.topic = (String) properties.get("mqttTopic");
		this.apiKey = (String) properties.get("apiKey");
		connectToBroker();
	}

	/**
	 * 
	 */
	private boolean connectToBroker() {
		try {
			log.info("Try to connect to: " + host + " Topic: " + topic);
			if (client == null) {
				client = new MqttClient(host, "mqttInitialStateConnector");
			}
			if (!client.isConnected()) {
				client.connect();
			}
			client.subscribe(topic);
			client.setCallback(this);
			log.info("connected !");
			// --
			if (initialStateAccount == null) {
				initialStateAccount = new API(apiKey);
				log.info("Initial State Account is connected");
			}

		} catch (MqttException e) {
			client = null;
			log.info(e.getMessage());
			return false;
		}
		return true;

	}

	@Override
	public void connectionLost(Throwable arg0) {
		log.warning("connection to Broker is LOST");
		initialStateAccount.terminate();
		initialStateAccount = null;
		boolean isConnected = false;
		while (!isConnected) {
			log.info("Wait a Minute before reconnect...");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				log.warning("Wait Thread was interrupted");
				e1.printStackTrace();
			}
			isConnected = connectToBroker();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String messageData = new String(message.getPayload());
		// --
		try {
			log.info("Message Arrived...");
			Bucket bucket = new Bucket("ws2300", "Wetterstation");
			initialStateAccount.createBucket(bucket);
			// create bulk data
			Data data[] = weatherDataParser.getInitialStateData(messageData);
			log.info("parsed:" + data.length + " Data for initial State");
			initialStateAccount.createBulkData(bucket, data);
			// close
			initialStateAccount.terminate();
			log.info("Data write to Bucket");
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

}