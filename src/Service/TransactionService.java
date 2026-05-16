package Service;

import Model.Compte;
import Model.Transaction;
import DAO.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    private CompteService compteService = new CompteService();

    //transfer de compte source vers compte destination
    public void effectuerTransfert(Compte source, String numeroDestination, BigDecimal montant, String description) throws Exception {

        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Le montant doit être supérieur à 0.");
        }
        if (source.getSolde().compareTo(montant) < 0) {
            throw new Exception("Solde insuffisant pour effectuer ce transfert.");
        }

        Compte dest = compteService.trouverParNumero(numeroDestination);

        if (dest.getId() == source.getId()) {
            throw new Exception("Impossible de transférer vers le même compte.");
        }

        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);          //BEGIN de Transaction SQL

        try {
            //Débiter le compte source
            String debit = "UPDATE comptes SET solde = solde - ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(debit)) {
                ps.setBigDecimal(1, montant);
                ps.setInt(2, source.getId());
                ps.executeUpdate(); // Trigger before_update_solde vérifie ici
            }

            //Créditer le compte destination
            String credit = "UPDATE comptes SET solde = solde + ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(credit)) {
                ps.setBigDecimal(1, montant);
                ps.setInt(2, dest.getId());
                ps.executeUpdate();
            }

            //Enregistrer la transaction qui déclenche after_transaction_insert
            String desc = (description == null || description.isBlank())
                    ? "Transfert vers " + dest.getNumeroCompte()
                    : description;
            enregistrerTransaction(conn, source.getId(), dest.getId(),
                    montant, Transaction.Type.TRANSFERT, desc, Transaction.Statut.SUCCES);

            conn.commit();                  //COMMIT

        } catch (SQLException ex) {
            conn.rollback();                //ROLLBACK
            throw new Exception("Transfert annulé (ROLLBACK) : " + ex.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }
    //  créditer un compte
    public void effectuerDepot(Compte compte, BigDecimal montant, String description) throws Exception {

        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Le montant doit être supérieur à 0.");
        }

        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            String sql = "UPDATE comptes SET solde = solde + ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBigDecimal(1, montant);
                ps.setInt(2, compte.getId());
                ps.executeUpdate();
            }

            String desc = (description == null || description.isBlank()) ? "Dépôt" : description;
            enregistrerTransaction(conn, null, compte.getId(),
                    montant, Transaction.Type.DEPOT, desc, Transaction.Statut.SUCCES);

            conn.commit();

        } catch (SQLException ex) {
            conn.rollback();
            throw new Exception("Dépôt annulé (ROLLBACK) : " + ex.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    //  RETRAIT : débiter un compte
    public void effectuerRetrait(Compte compte, BigDecimal montant, String description) throws Exception {

        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Le montant doit être supérieur à 0.");
        }
        if (compte.getSolde().compareTo(montant) < 0) {
            throw new Exception("Solde insuffisant.");
        }

        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            String sql = "UPDATE comptes SET solde = solde - ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBigDecimal(1, montant);
                ps.setInt(2, compte.getId());
                ps.executeUpdate();// Trigger before_update_solde vérifie ici
            }

            String desc = (description == null || description.isBlank()) ? "Retrait" : description;
            enregistrerTransaction(conn, compte.getId(), null,
                    montant, Transaction.Type.RETRAIT, desc, Transaction.Statut.SUCCES);

            conn.commit();

        } catch (SQLException ex) {
            conn.rollback();
            throw new Exception("Retrait annulé (ROLLBACK) : " + ex.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }
    //  historique des transactions d'un compte

    public List<Transaction> getHistorique(int compteId) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
            SELECT t.*,
                   cs.numero_compte AS num_source,
                   cd.numero_compte AS num_dest
            FROM transactions t
            LEFT JOIN comptes cs ON t.compte_source_id = cs.id
            LEFT JOIN comptes cd ON t.compte_dest_id   = cd.id
            WHERE t.compte_source_id = ?
               OR t.compte_dest_id   = ?
            ORDER BY t.date_transaction DESC
            LIMIT 100
            """;

        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compteId);
            ps.setInt(2, compteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t = mapTransaction(rs);
                list.add(t);
            }
        }
        return list;
    }

    //  methode privée qui insere une ligne dans transactions
    private void enregistrerTransaction(Connection conn,
                                        Integer sourceId, Integer destId,
                                        BigDecimal montant,
                                        Transaction.Type type, String description,
                                        Transaction.Statut statut) throws SQLException {
        String sql = """
                INSERT INTO transactions
                    (compte_source_id, compte_dest_id, montant,
                     type_transaction, description, statut)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (sourceId != null) ps.setInt(1, sourceId); else ps.setNull(1, Types.INTEGER);
            if (destId   != null) ps.setInt(2, destId);   else ps.setNull(2, Types.INTEGER);
            ps.setBigDecimal(3, montant);
            ps.setString(4, type.name());
            ps.setString(5, description);
            ps.setString(6, statut.name());
            ps.executeUpdate();     // déclenche after_transaction_insert
        }
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        int src = rs.getInt("compte_source_id");
        if (!rs.wasNull()) t.setCompteSourceId(src);
        int dst = rs.getInt("compte_dest_id");
        if (!rs.wasNull()) t.setCompteDestId(dst);
        t.setMontant(rs.getBigDecimal("montant"));
        t.setTypeTransaction(Transaction.Type.valueOf(rs.getString("type_transaction")));
        t.setDescription(rs.getString("description"));
        t.setStatut(Transaction.Statut.valueOf(rs.getString("statut")));
        Timestamp ts = rs.getTimestamp("date_transaction");
        if (ts != null) t.setDateTransaction(ts.toLocalDateTime());
        t.setNumeroSource(rs.getString("num_source"));
        t.setNumeroDest(rs.getString("num_dest"));
        return t;
    }
}
