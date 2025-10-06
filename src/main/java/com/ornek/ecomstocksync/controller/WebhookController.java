
package com.ornek.ecomstocksync.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/webhooks")
@CrossOrigin(origins = "*")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/shopify")
    public ResponseEntity<Map<String, Object>> shopifyWebhook(
            @RequestHeader(value = "X-Shopify-Topic", required = false) String topic,
            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String hmac,
            @RequestBody(required = false) String body
    ) {
        log.info("[Webhook] Shopify topic={}, hmac={}, body={}", topic, hmac, body);
        Map<String, Object> res = new HashMap<>();
        res.put("received", true);
        res.put("platform", "shopify");
        res.put("topic", topic);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/ebay")
    public ResponseEntity<Map<String, Object>> ebayWebhook(
            @RequestHeader(value = "X-EBAY-SIGNATURE", required = false) String signature,
            @RequestBody(required = false) String body
    ) {
        log.info("[Webhook] eBay signature={}, body={}", signature, body);
        Map<String, Object> res = new HashMap<>();
        res.put("received", true);
        res.put("platform", "ebay");
        return ResponseEntity.ok(res);
    }
}
