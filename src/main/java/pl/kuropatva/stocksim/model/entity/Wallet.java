package pl.kuropatva.stocksim.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Wallet {

    @Id
    private String id;

    @OneToMany(
            mappedBy = "wallet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Stock> stocks = new ArrayList<>();


    public Wallet() {
    }

    public Wallet(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Stock> getStocks() {
        return stocks;
    }
}


