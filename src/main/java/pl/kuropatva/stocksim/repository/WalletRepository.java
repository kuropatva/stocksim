package pl.kuropatva.stocksim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kuropatva.stocksim.model.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, String> {
}
