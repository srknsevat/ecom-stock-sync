
package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.service.PlatformService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/credentials")
@CrossOrigin(origins = "*")
public class CredentialController {

    private final PlatformService platformService;

    public CredentialController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @PostMapping("/test/{platformId}")
    public ResponseEntity<Map<String, Object>> test(@PathVariable Long platformId,
                                                    @RequestBody Map<String, String> req) {
        String type = req.getOrDefault("credentialType", "API_KEY");
        String value = platformService.getCredential(platformId, type);
        boolean ok = value != null && !value.isBlank();
        return ResponseEntity.ok(Map.of(
            "success", ok,
            "credentialType", type,
            "present", ok
        ));
    }
}
