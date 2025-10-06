
package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.PlatformProduct;
import com.ornek.ecomstocksync.entity.SyncHistory;

public interface SyncHistoryService {
    void record(PlatformProduct platformProduct, SyncHistory.Action action, SyncHistory.Status status, String detail);
}
