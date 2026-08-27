package com.felp64.routedecoder.service;

import com.felp64.routedecoder.geocoding.ReverseGeocodingClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvReverseGeocodingServiceTest {

    @Mock
    private ReverseGeocodingClient reverseGeocodingClient;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private CsvReverseGeocodingService service;

    @BeforeEach
    void setUp() {
        service = new CsvReverseGeocodingService(reverseGeocodingClient, redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldUseCacheAndWriteEnrichedCsv() throws Exception {
        String csv = "latitude,longitude\n-23.55,-46.63\n-23.55,-46.63\n";
        when(valueOperations.get("-23.55:-46.63")).thenReturn(null, "São Paulo, Brasil");
        when(reverseGeocodingClient.resolveAddress("-23.55", "-46.63")).thenReturn("São Paulo, Brasil");

        StringWriter output = new StringWriter();
        service.process(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), output);

        assertThat(output.toString()).contains("latitude,longitude,address");
        assertThat(output.toString()).contains("-23.55,-46.63,\"São Paulo, Brasil\"");
        verify(reverseGeocodingClient, times(1)).resolveAddress("-23.55", "-46.63");
        verify(valueOperations, times(1)).set("-23.55:-46.63", "São Paulo, Brasil");
    }
}
