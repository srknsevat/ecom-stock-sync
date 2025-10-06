
package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.SyncHistory;
import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.Product;
import com.ornek.ecomstocksync.entity.PlatformProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncHistoryRepository extends JpaRepository<SyncHistory, Long> {
    List<SyncHistory> findByProduct(Product product);
    List<SyncHistory> findByPlatform(Platform platform);
    List<SyncHistory> findByPlatformProduct(PlatformProduct platformProduct);
}
