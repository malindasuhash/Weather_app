package app.com.example.malindasuhash.weatherapptake1.utils;

/**
 * Builds the endpoint urls that is used to call
 * Open weather service.
 */
public class EndpointBuilder {

    private static String mOpenWebEndpointPrefix = "http://api.openweathermap.org/data/2.5/weather?q=";

    public static String build(String location) {

        // Does the endpoint needs to URL encoded?
        String endpoint = mOpenWebEndpointPrefix + location;

        return endpoint;
    }
}
