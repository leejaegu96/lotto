package com.example.lotto;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/lotto")
public class LottoController {

    private final LottoService lottoService;

    public LottoController(LottoService lottoService) {
        this.lottoService = lottoService;
    }

    @GetMapping("/random")
    public List<Integer> getRandomNumbers() {
        return lottoService.generate();
    }
}