package com.felp64.routedecoder.controller;

import com.felp64.routedecoder.service.CsvReverseGeocodingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.io.Writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReverseGeocodingController.class)
class ReverseGeocodingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvReverseGeocodingService service;

    @Test
    void shouldReturnEnrichedCsvAsAttachment() throws Exception {
        doAnswer(invocation -> {
            Writer writer = invocation.getArgument(1, Writer.class);
            writer.write("latitude,longitude,address\n-23.55,-46.63,Address\n");
            return null;
        }).when(service).process(any(InputStream.class), any(Writer.class));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "coordinates.csv",
                "text/csv",
                "latitude,longitude\n-23.55,-46.63\n".getBytes()
        );

        mockMvc.perform(multipart("/api/reverse-geocoding/csv").file(file))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"enriched-coordinates.csv\""));
    }
}
