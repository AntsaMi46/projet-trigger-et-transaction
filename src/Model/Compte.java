package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Compte {
    private int id;
    private int clientId;
    private String numeroCompte;
    private BigDecimal solde;
    private LocalDateTime dateCreation;

    public Compte() {}

    public Compte(int id, int clientId, String numeroCompte, BigDecimal solde, LocalDateTime dateCreation) {
        this.id = id;
        this.clientId = clientId;
        this.numeroCompte = numeroCompte;
        this.solde = solde;
        this.dateCreation = dateCreation;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }

    @Override
    public String toString() { return numeroCompte + " | Solde : " + solde + " Ar"; }
}

