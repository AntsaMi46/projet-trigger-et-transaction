package Service;

import DAO.DatabaseConnection;
import DAO.SecurityDAO;
import Model.Client;

import java.sql.*;

public class ClientService {
    public Client inscrire(String nom, String prenom, String email, String motDePasse) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        //verifier l'email si il existe déja
        String checkSql = "SELECT id FROM clients WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                throw new Exception("Un compte avec cet email existe déjà.");
            }
        }
        String sql = "INSERT INTO clients (nom, prenom, email, mot_de_passe) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nom.toUpperCase().trim());
            ps.setString(2, prenom.trim());
            ps.setString(3, email.trim().toLowerCase());
            ps.setString(4, SecurityDAO.sha256(motDePasse));

            int rows = ps.executeUpdate();
            if (rows == 0) throw new Exception("Échec de l'inscription.");

            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) {
                int newId = gk.getInt(1);
                return trouverParId(newId);
            }
        }
        throw new Exception("Erreur lors de la récupération du client créé.");
    }

    //connexion client
    public Client connecter(String email, String motDePasse) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM clients WHERE email = ? AND mot_de_passe = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, SecurityDAO.sha256(motDePasse));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapClient(rs);
            } else {
                throw new Exception("Email ou mot de passe incorrect.");
            }
        }
    }

    public Client trouverParId(int id) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapClient(rs);
        }
        throw new Exception("Client introuvable (id=" + id + ")");
    }

    private Client mapClient(ResultSet rs) throws SQLException {
        Client c = new Client();
        c.setId(rs.getInt("id"));
        c.setNom(rs.getString("nom"));
        c.setPrenom(rs.getString("prenom"));
        c.setEmail(rs.getString("email"));
        Timestamp ts = rs.getTimestamp("date_inscription");
        if (ts != null) c.setDateInscription(ts.toLocalDateTime());
        return c;
    }
}
