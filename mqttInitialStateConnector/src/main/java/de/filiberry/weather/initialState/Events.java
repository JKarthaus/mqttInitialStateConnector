package de.filiberry.weather.initialState;

interface Events {
  final static String API_BASEURL = "https://groker.initialstate.com/api/";
  String getEndpoint();
}
