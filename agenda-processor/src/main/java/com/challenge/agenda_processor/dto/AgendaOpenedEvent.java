package com.challenge.agenda_processor.dto;

import java.time.LocalDateTime;

/**
 * Represent the open Agenda event to send into kafka
 */
public record AgendaOpenedEvent(Long agendaId, LocalDateTime expiration) {}
