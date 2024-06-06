package org.kassen.rawtotif.Controller;

import org.kassen.rawtotif.Service.rawToTifService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@EnableAsync
@CrossOrigin(origins = "*")
public class rawToTifController {

    private static final Logger logger = LoggerFactory.getLogger(rawToTifController.class);

    @Autowired
    private rawToTifService rawToTifService;

    @GetMapping("/raw")
    public SseEmitter processRaw(@RequestParam("rawDir") String rawDir) {
        SseEmitter emitter = new SseEmitter();
        rawToTifService.processDirectoryAsync(rawDir, emitter);
        logger.info("Returning SseEmitter: {}", emitter);
        return emitter;
    }
}
