package pl.kuropatva.stocksim.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kuropatva.stocksim.model.entity.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.name = :name and s.wallet is null")
    Optional<Stock> findByNameAndWalletIsNullForUpdate(@Param("name") String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.name = :name and s.wallet.id = :walletId")
    Optional<Stock> findByNameAndWalletIdForUpdate(@Param("name") String name, @Param("walletId") String walletId);

    Optional<Stock> findByNameAndWalletIsNull(String name);

    Optional<Stock> findByNameAndWalletId(String name, String walletId);

    List<Stock> findAllByWalletId(String walletId);

    void deleteAllByWalletId(String walletId);
}