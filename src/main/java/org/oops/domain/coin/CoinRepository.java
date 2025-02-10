package org.oops.domain.coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {

    List<Coin> findByIsVisibleTrue();
    Optional<Coin> findByName(String name);
}
