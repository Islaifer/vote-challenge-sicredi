package com.challenge.agenda_processor.dto;

/**
 * Represent the close Agenda event to send into kafka
 */
public record AgendaResult(Long agendaId, String agendaName, String agendaDetails, int votesYes, int votesNo, String voteWin) {}
