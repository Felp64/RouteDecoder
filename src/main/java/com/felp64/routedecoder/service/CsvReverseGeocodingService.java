package com.felp64.routedecoder.service;

import com.felp64.routedecoder.geocoding.ReverseGeocodingClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Service
public class CsvReverseGeocodingService {

    private static final String ADDRESS_COLUMN = "address";
    private final ReverseGeocodingClient reverseGeocodingClient;
    private final StringRedisTemplate redisTemplate;

    public CsvReverseGeocodingService(ReverseGeocodingClient reverseGeocodingClient,
                                      StringRedisTemplate redisTemplate) {
        this.reverseGeocodingClient = reverseGeocodingClient;
        this.redisTemplate = redisTemplate;
    }

    public void process(InputStream csvInputStream, Writer outputWriter) throws IOException {
        try (Reader reader = new InputStreamReader(csvInputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader);
             CSVPrinter printer = CSVFormat.DEFAULT.print(outputWriter)) {

            printer.printRecord("latitude", "longitude", ADDRESS_COLUMN);

            for (CSVRecord record : parser) {
                String latitude = record.get("latitude").trim();
                String longitude = record.get("longitude").trim();
                String cacheKey = cacheKey(latitude, longitude);

                String address = redisTemplate.opsForValue().get(cacheKey);
                if (address == null) {
                    address = reverseGeocodingClient.resolveAddress(latitude, longitude);
                    redisTemplate.opsForValue().set(cacheKey, address);
                }

                printer.printRecord(latitude, longitude, address);
            }
            printer.flush();
        }
    }

    private String cacheKey(String latitude, String longitude) {
        return latitude + ":" + longitude;
    }
}
