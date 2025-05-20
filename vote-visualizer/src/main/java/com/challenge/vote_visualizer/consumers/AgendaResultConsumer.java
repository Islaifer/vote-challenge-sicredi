package com.challenge.vote_visualizer.consumers;

import com.challenge.vote_visualizer.controllers.AgendaResultSseController;
import com.challenge.vote_visualizer.dto.AgendaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


/**
 * Class with consumers of the agenda closing events
 */
@Service
public class AgendaResultConsumer {
    private static final Logger log = LoggerFactory.getLogger(AgendaResultConsumer.class);

    private final AgendaResultSseController controller;

    @Autowired
    public AgendaResultConsumer(AgendaResultSseController controller) {
        this.controller = controller;
    }

    /**
     * Method to get data from the closed Agenda and send it to SSE connections
     * @param result the AgendaResult represent the closed Agenda
     */
    @KafkaListener(topics = "agenda-finished", groupId = "vote-visualizer", containerFactory = "agendaResultKafkaListenerContainerFactory")
    public void showAgendaResult(AgendaResult result){
        log.info("Agenda with agendaId: '{}' was closed and listened to show her results", result.agendaId());
        controller.sendResultToClients(result);
    }
}
