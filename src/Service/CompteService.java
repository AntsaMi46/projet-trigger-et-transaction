package Service;

import DAO.DatabaseConnection;
import Model.Compte;

import java.sql.*;

public class CompteService {public Compte trouverParClientId(int clientId) throws Exception {
    Connection conn = DatabaseConnection.getConnection();
    String sql = "SELECT * FROM comptes WHERE client_id = ? LIMIT 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapCompte(rs);
    }
    throw new Exception("Aucun compte trouvé pour ce client.");
}

     //Récupère un compte par numéro.

    public Compte trouverParNumero(String numero) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM comptes WHERE numero_compte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCompte(rs);
        }
        throw new Exception("Compte numéro " + numero + " introuvable.");
    }

     //Récupère un compte par son id.
    public Compte trouverParId(int id) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM comptes WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCompte(rs);
        }
        throw new Exception("Compte id=" + id + " introuvable.");
    }


     //Recharge le solde depuis la base (après une transaction).
    public Compte rafraichir(Compte compte) throws Exception {
        return trouverParId(compte.getId());
    }

    private Compte mapCompte(ResultSet rs) throws SQLException {
        Compte c = new Compte();
        c.setId(rs.getInt("id"));
        c.setClientId(rs.getInt("client_id"));
        c.setNumeroCompte(rs.getString("numero_compte"));
        c.setSolde(rs.getBigDecimal("solde"));
        Timestamp ts = rs.getTimestamp("date_creation");
        if (ts != null) c.setDateCreation(ts.toLocalDateTime());
        return c;
    }
}
