package com.challenge.vote_visualizer.dto;

/**
 * Represents the Agendas that have been closed and will have the result displayed
 */
public record AgendaResult(Long agendaId, String agendaName, String agendaDetails, int votesYes, int votesNo, String voteWin) {}
