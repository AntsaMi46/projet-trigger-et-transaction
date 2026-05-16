package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Transaction {
    public enum Type { DEPOT, RETRAIT, TRANSFERT }
    public enum Statut { SUCCES, ECHEC }

    private int id;
    private Integer compteSourceId;
    private Integer compteDestId;
    private BigDecimal montant;
    private Type typeTransaction;
    private String description;
    private LocalDateTime dateTransaction;
    private Statut statut;
    private String numeroSource;
    private String numeroDest;

    public Transaction() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getCompteSourceId() { return compteSourceId; }
    public void setCompteSourceId(Integer compteSourceId) { this.compteSourceId = compteSourceId; }

    public Integer getCompteDestId() { return compteDestId; }
    public void setCompteDestId(Integer compteDestId) { this.compteDestId = compteDestId; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public Type getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(Type t) { this.typeTransaction = t; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDateTime d) { this.dateTransaction = d; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public String getNumeroSource() { return numeroSource; }
    public void setNumeroSource(String s) { this.numeroSource = s; }

    public String getNumeroDest() { return numeroDest; }
    public void setNumeroDest(String s) { this.numeroDest = s; }

    // indique si la transaction est un credit ou debit pour un compte donne
    public boolean isCredit(int compteId) {
        return compteId == (compteDestId != null ? compteDestId : -1);
    }
}