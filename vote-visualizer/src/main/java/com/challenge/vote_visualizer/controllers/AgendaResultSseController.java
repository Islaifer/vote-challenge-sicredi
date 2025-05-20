package com.challenge.vote_visualizer.controllers;

import com.challenge.vote_visualizer.dto.AgendaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Control class that provides an SSE connection
 */
@RestController
@RequestMapping("/api/v1/view")
public class AgendaResultSseController {
    private static final Logger log = LoggerFactory.getLogger(AgendaResultSseController.class);

    private final CopyOnWriteArrayList<SseEmitter> emitters;

    public AgendaResultSseController(){
        emitters = new CopyOnWriteArrayList<>();
    }

    /**
     * Method that establishes an SSE connection
     * @return the SseEmitter to establishes the SSE connection
     */
    @GetMapping("/result-stream")
    public SseEmitter streamResults(){
        log.info("Trying to establish SSE connection");
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        log.info("SSE connection established");
        return emitter;
    }

    /**
     * Sends the result of the closed agenda to all open SSE connections
     * @param result the Closed Agenda result
     */
    public void sendResultToClients(AgendaResult result) {
        log.info("Send result to emitters");
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(result);
                log.debug("Result sent");
            } catch (IOException e) {
                log.warn("Connection with emitter '{}' lost", emitter.hashCode());
                emitters.remove(emitter);
            }
        }
    }
}
