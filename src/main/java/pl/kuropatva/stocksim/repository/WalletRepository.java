package pl.kuropatva.stocksim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import pl.kuropatva.stocksim.model.entity.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {
}
