package com.aidart.backend.game.engine;

import com.aidart.backend.game.Game;
import com.aidart.backend.game.GameMapper;
import com.aidart.backend.game.GameRepository;
import com.aidart.backend.game.GameState;
import com.aidart.backend.game.dto.GamePlayerDto;
import com.aidart.backend.visit.VisitDto;
import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.shot.ShotDto;
import com.aidart.backend.shot.enums.ScoreMultiplier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class X01GameEngine implements GameEngine {

    private GameMapper gameMapper;
    private GameRepository gameRepository;

    @Override
    public boolean supportsGameType(GameType gameType) {
        return gameType != GameType.CRICKET;
    }

    @Override
    public GameState handleShot(GameState gameState, ShotDto shotDto) {
        GamePlayerDto activePlayer = findActivePlayer(gameState);
        GamePlayerDto playerAfterShot = applyShotToActivePlayer(gameState, activePlayer, shotDto);

        List<GamePlayerDto> players = replacePlayer(gameState.getGamePlayers(), playerAfterShot);

        if (isJustFinished(playerAfterShot)) {
            int currentRank = countFinished(players) + 1;

            GamePlayerDto finishedShooter = playerAfterShot.toBuilder()
                    .finalPosition(currentRank)
                    .build();

            players = replacePlayer(players, finishedShooter);

            long finishedCount = players.stream().filter(p -> p.finalPosition() != null).count();
            int totalPlayers = players.size();

            if (totalPlayers > 1 && finishedCount == totalPlayers - 1) {

                GamePlayerDto loser = players.stream()
                        .filter(p -> p.finalPosition() == null)
                        .findFirst()
                        .orElseThrow();

                GamePlayerDto finishedLoser = loser.toBuilder()
                        .finalPosition(totalPlayers)
                        .build();

                players = replacePlayer(players, finishedLoser);
            }

            boolean isAllFinished = players.stream().allMatch(p -> p.finalPosition() != null);

            if (isAllFinished) {
                return gameState.toBuilder()
                        .gameStatus(GameStatus.FINISHED)
                        .gamePlayers(players)
                        .build();
            }
        }

        GameState updated = gameState.toBuilder()
                .gamePlayers(players)
                .build();

        if (isTurnedEnd(playerAfterShot)) {
            updated = advanceTurn(updated);
        }

        return updated;
    }

    @Override
    public GameState deleteShot(GameState gameState) {
        GamePlayerDto activePlayer = findActivePlayer(gameState);
        List<VisitDto> visits = safeList(activePlayer.visits());
        VisitDto lastVisit = visits.isEmpty() ? null : visits.getLast();

        List<GamePlayerDto> updatedPlayers;
        Long nextActiveId = activePlayer.id();
        int nextRound = gameState.getCurrentRound();

        if (lastVisit != null && !lastVisit.shots().isEmpty()) {
            updatedPlayers = revertLastShot(gameState, lastVisit, visits, activePlayer);
        } else {
            GamePlayerDto previousPlayer = getLastPlayer(gameState.getGamePlayers(), activePlayer.id());
            List<VisitDto> previousPlayerVisits = new ArrayList<>(safeList(previousPlayer.visits()));

            if (previousPlayerVisits.isEmpty()) return gameState;

            GamePlayerDto currentCleaned = activePlayer;
            List<VisitDto> currentVisits = new ArrayList<>(safeList(activePlayer.visits()));
            if (!currentVisits.isEmpty() && safeList(currentVisits.getLast().shots()).isEmpty()) {
                currentVisits.removeLast();
                currentCleaned = activePlayer.toBuilder().visits(currentVisits).build();
            }

            List<GamePlayerDto> playersWithCleanCurrent = replacePlayer(gameState.getGamePlayers(), currentCleaned);
            GameState tempState = gameState.toBuilder().gamePlayers(playersWithCleanCurrent).build();

            updatedPlayers = revertLastShot(
                    tempState,
                    previousPlayerVisits.getLast(),
                    previousPlayerVisits,
                    previousPlayer
            );

            nextActiveId = previousPlayer.id();
            if (indexOfPlayer(gameState.getGamePlayers(), activePlayer.id()) == 0) {
                nextRound = Math.max(1, nextRound - 1);
            }
        }

        List<GamePlayerDto> finalPlayers = updatedPlayers.stream()
                .map(p -> (p.scoreLeft() != null && p.scoreLeft() > 0)
                        ? p.toBuilder().finalPosition(null).build()
                        : p)
                .toList();

        return gameState.toBuilder()
                .activePlayerId(nextActiveId)
                .gamePlayers(finalPlayers)
                .currentRound(nextRound)
                .gameStatus(GameStatus.IN_PROGRESS)
                .build();
    }

    @Override
    public GameState finishGame(GameState gameState) {
        return gameState;
    }

    private List<GamePlayerDto> revertLastShot(
            GameState gameState,
            VisitDto lastVisit,
            List<VisitDto> visits,
            GamePlayerDto activePlayer
    ) {
        List<ShotDto> shots = new ArrayList<>( safeList(lastVisit.shots()) );

        ShotDto removedShot = shots.removeLast();

        VisitDto updatedVisit = lastVisit.toBuilder()
                .shots(shots)
                .totalScore(lastVisit.totalScore() - removedShot.totalScore())
                .isBust(false)
                .build();

        List<VisitDto> updatedVisits = replaceLast(visits, updatedVisit);

        GamePlayerDto updatedPlayer = activePlayer
                .toBuilder()
                .visits(updatedVisits)
                .scoreLeft(activePlayer.scoreLeft() + removedShot.totalScore())
                .finalPosition(null)
                .build();

        return replacePlayer(gameState.getGamePlayers(), updatedPlayer);
    }

    private GamePlayerDto getLastPlayer(List<GamePlayerDto> players, Long activePlayerId) {
        int idx = indexOfPlayer(players, activePlayerId);
        int size = players.size();
        int previousIndex = (idx - 1 + size) % size;
        return players.get(previousIndex);
    }


    private GamePlayerDto findActivePlayer(GameState gameState) {
        return gameState.getGamePlayers().stream()
                .filter(player -> player.id().equals(gameState.getActivePlayerId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active player not found. activePlayerId=" + gameState.getActivePlayerId()));
    }

    private List<GamePlayerDto> replacePlayer(List<GamePlayerDto> players, GamePlayerDto replacement) {
        return players.stream()
                .map(p -> p.id().equals(replacement.id()) ? replacement : p)
                .toList();
    }

    private GamePlayerDto applyShotToActivePlayer(
            GameState gameState,
            GamePlayerDto activePlayer,
            ShotDto shotDto
    ) {
        List<VisitDto> visits = activePlayer.visits() == null
                ? List.of()
                : activePlayer.visits();

        VisitDto lastVisit = visits.isEmpty()
                ? new VisitDto(activePlayer.id(), List.of(), 0, false)
                : visits.getLast();

        boolean canAppendToLast = lastVisit.shots().size() < 3 && !lastVisit.isBust();

        VisitDto baseVisit = canAppendToLast
                ? lastVisit
                : new VisitDto(activePlayer.id(), List.of(), 0, false);

        VisitDto updatedVisit = appendShot(baseVisit, shotDto);

        List<VisitDto> visitsAfterAppend = canAppendToLast
                ? replaceLast(visits, updatedVisit)
                : append(visits, updatedVisit);

        int startingScore = startingScoreFor(gameState.getGameType());
        int scoreLeft = startingScore - sumNonBustVisits(visitsAfterAppend);

        if (isBust(scoreLeft, shotDto.multiplier(), gameState.getFinishRule())) {
            VisitDto bustedVisit = updatedVisit.toBuilder()
                    .isBust(true)
                    .build();
            visitsAfterAppend = replaceLast(visitsAfterAppend, bustedVisit);
            scoreLeft = startingScore - sumNonBustVisits(visitsAfterAppend);
        }

        return copyPlayer(activePlayer, visitsAfterAppend, scoreLeft);
    }

    private boolean isBust(int scoreLeft, ScoreMultiplier multiplier, GameFinishRule finishRule) {
        if (scoreLeft < 0) return true;

        if ((finishRule == GameFinishRule.DOUBLE_OUT || finishRule == GameFinishRule.MASTER_OUT)
                && scoreLeft == 1) return true;

        if (scoreLeft == 0) {
            if (finishRule == GameFinishRule.DOUBLE_OUT) {
                return multiplier != ScoreMultiplier.DOUBLE;
            }
            if (finishRule == GameFinishRule.MASTER_OUT) {
                return multiplier != ScoreMultiplier.DOUBLE && multiplier != ScoreMultiplier.TRIPLE;
            }
        }
        return false;
    }

    private boolean isTurnedEnd(GamePlayerDto player) {
        VisitDto lastVisit = player.visits().getLast();
        return lastVisit.isBust() || lastVisit.shots().size() == 3 || player.scoreLeft() == 0;
    }


    private boolean isJustFinished(GamePlayerDto p) {
        return p.scoreLeft() != null && p.scoreLeft() == 0 && p.finalPosition() == null;
    }

    private int countFinished(List<GamePlayerDto> players) {
        int finished = 0;
        for (GamePlayerDto p : players) {
            if (p.finalPosition() != null) finished++;
        }
        return finished;
    }


    private GameState advanceTurn(GameState gameState) {
        List<GamePlayerDto> players = gameState.getGamePlayers();
        Long currentActiveId = gameState.getActivePlayerId();

        int idx = indexOfPlayer(players, currentActiveId);
        int nextIdx = idx;

        int round = gameState.getCurrentRound() == null ? 1 : gameState.getCurrentRound();

        do {
            nextIdx = (nextIdx + 1) % players.size();

            if (nextIdx == 0) {
                round++;
            }
        } while (players.get(nextIdx).finalPosition() != null && nextIdx != idx);

        GamePlayerDto nextPlayer = players.get(nextIdx);

        GamePlayerDto nextPlayerWithVisit = addEmptyVisit(nextPlayer);

        List<GamePlayerDto> updatedPlayers = players.stream()
                .map(p -> p.id().equals(nextPlayerWithVisit.id()) ? nextPlayerWithVisit : p)
                .toList();

        return gameState.toBuilder()
                .gamePlayers(updatedPlayers)
                .activePlayerId(nextPlayerWithVisit.id())
                .currentRound(round)
                .build();
    }

    private GamePlayerDto addEmptyVisit(GamePlayerDto gamePlayerDto) {
        List<VisitDto> currentVisits = gamePlayerDto.visits() == null
                ? new ArrayList<>()
                : new ArrayList<>(gamePlayerDto.visits());

        VisitDto newVisit = VisitDto.builder()
                .gamePlayerId(gamePlayerDto.id())
                .shots(new ArrayList<>())
                .totalScore(0)
                .isBust(false)
                .build();

        currentVisits.add(newVisit);

        return gamePlayerDto.toBuilder()
                .visits(currentVisits)
                .build();
    }

    private int indexOfPlayer(List<GamePlayerDto> players, Long id) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).id().equals(id)) return i;
        }
        throw new IllegalStateException("Player not found");
    }


    private VisitDto appendShot(VisitDto visit, ShotDto shotDto) {
        List<ShotDto> shots = safeList(visit.shots());
        List<ShotDto> newShots = append(shots, shotDto);
        int total = newShots.stream().mapToInt(ShotDto::totalScore).sum();
        return visit.toBuilder()
                .shots(newShots)
                .totalScore(total)
                .build();
    }

    private int sumNonBustVisits(List<VisitDto> visits) {
        return safeList(visits).stream()
                .filter(visit -> !visit.isBust())
                .mapToInt(VisitDto::totalScore)
                .sum();
    }

    private int startingScoreFor(GameType type) {
        return switch (type) {
            case TYPE_301 -> 301;
            case TYPE_701 -> 701;
            case TYPE_901 -> 901;
            default -> 501;
        };
    }

    private GamePlayerDto copyPlayer(
            GamePlayerDto player,
            List<VisitDto> newVisits,
            int newScoreLeft
    ) {
        return player.toBuilder()
                .visits(newVisits)
                .scoreLeft(newScoreLeft)
                .build();
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private <T> List<T> append(List<T> list, T item) {
        List<T> copy = new ArrayList<>(safeList(list));
        copy.add(item);
        return List.copyOf(copy);
    }

    private <T> List<T> replaceLast(List<T> list, T newLast) {
        List<T> copy = new ArrayList<>(safeList(list));
        if (copy.isEmpty()) {
            copy.add(newLast);
        } else {
            copy.set(copy.size() - 1, newLast);
        }
        return List.copyOf(copy);
    }
}
