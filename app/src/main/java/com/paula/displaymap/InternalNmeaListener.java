package com.paula.displaymap;// Register the listener

import android.location.GpsStatus;

/**
 * Because many existing data sets and Esri services use orthometric (MSL) Z values,
 * it is convenient to get MSL values from the location data source.
 * Although Android natively provides values in WGS84 HAE, you can listen for NMEA messages
 * from the on-board GPS to get elevations relative to MSL if the device supports it.
 * @see https://developers.arcgis.com/android/latest/guide/display-scenes-in-augmented-reality.htm
 */
public class InternalNmeaListener implements GpsStatus.NmeaListener {
    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
        if (nmea.startsWith("$GPGGA") || nmea.startsWith("$GNGNS") || nmea.startsWith("$GNGGA")) {
            String[] messageParts = nmea.split(",");
            if (messageParts.length < 10){
                return; // Not enough parts
            }

            String mslAltitude = messageParts[9];

            if (mslAltitude.isEmpty()) {
                return;
            }

            Double altitudeParsed = null;

            try {
                altitudeParsed = Double.parseDouble(mslAltitude);
            } catch (NumberFormatException e) {
                return;
            }

            if (altitudeParsed != null) {
                //lastNmeaHeight = altitudeParsed;
                //lastNmeaUpdateTimestamp = timestamp;
            }
        }
    }
}