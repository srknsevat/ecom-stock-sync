
package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.PlatformProduct;
import com.ornek.ecomstocksync.entity.SyncHistory;
import com.ornek.ecomstocksync.repository.SyncHistoryRepository;
import com.ornek.ecomstocksync.service.SyncHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SyncHistoryServiceImpl implements SyncHistoryService {

    private final SyncHistoryRepository repo;

    public SyncHistoryServiceImpl(SyncHistoryRepository repo) { this.repo = repo; }

    @Override
    public void record(PlatformProduct platformProduct, SyncHistory.Action action, SyncHistory.Status status, String detail) {
        SyncHistory sh = new SyncHistory();
        sh.setPlatform(platformProduct.getPlatform());
        sh.setProduct(platformProduct.getProduct());
        sh.setPlatformProduct(platformProduct);
        sh.setAction(action);
        sh.setStatus(status);
        sh.setDetail(detail);
        repo.save(sh);
    }
}
