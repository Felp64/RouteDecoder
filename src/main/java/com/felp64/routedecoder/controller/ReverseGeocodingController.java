package com.felp64.routedecoder.controller;

import com.felp64.routedecoder.service.CsvReverseGeocodingService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/api/reverse-geocoding")
public class ReverseGeocodingController {

    private final CsvReverseGeocodingService csvReverseGeocodingService;

    public ReverseGeocodingController(CsvReverseGeocodingService csvReverseGeocodingService) {
        this.csvReverseGeocodingService = csvReverseGeocodingService;
    }

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> processCsv(@RequestParam("file") MultipartFile file) {
        StreamingResponseBody stream = outputStream -> {
            try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                csvReverseGeocodingService.process(file.getInputStream(), writer);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("enriched-coordinates.csv").build().toString())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(stream);
    }
}
