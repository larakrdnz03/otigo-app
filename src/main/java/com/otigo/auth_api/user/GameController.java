package com.otigo.auth_api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // GET: http://localhost:8080/api/games/child/5
    // 5 numaralı çocuğun oyunlarını listele
    @GetMapping("/child/{childId}")
    public ResponseEntity<List<Game>> getChildGames(@PathVariable Long childId) {
        List<Game> games = gameService.getGamesForChild(childId);
        return ResponseEntity.ok(games);
    }
}