package org.oops.domain.news;

import org.oops.domain.coin.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n JOIN n.newsCoinRelation ncr WHERE ncr.coin.coinId = :coinId")
    List<News> findByCoinId_CoinId(Long coinId);
}
