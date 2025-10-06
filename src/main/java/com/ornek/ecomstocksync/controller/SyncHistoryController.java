
package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.SyncHistory;
import com.ornek.ecomstocksync.repository.SyncHistoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync-history")
@CrossOrigin(origins = "*")
public class SyncHistoryController {

    private final SyncHistoryRepository repo;

    public SyncHistoryController(SyncHistoryRepository repo) { this.repo = repo; }

    @GetMapping
    public List<SyncHistory> list() { return repo.findAll(); }
}
