package Controller;

import Model.Client;
import Model.Compte;
import Service.ClientService;
import Service.CompteService;
import View.AuthView;
import View.DashboardView;

import javax.swing.*;

//connexionet et inscription
public class AuthController {

    private final AuthView      view;
    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();

    public AuthController(AuthView view) {
        this.view = view;
    }

    //  Connexion
    public void handleLogin() {
        view.setLoginError("");

        String email = view.getLoginEmail();
        String pass  = view.getLoginPassword();

        if (email.isBlank() || email.equals("Adresse email")) {
            view.setLoginError("Veuillez saisir votre email.");
            return;
        }
        if (pass.isBlank() || pass.equals("Mot de passe")) {
            view.setLoginError("Veuillez saisir votre mot de passe.");
            return;
        }

        SwingWorker<Client, Void> worker = new SwingWorker<>() {
            @Override
            protected Client doInBackground() throws Exception {
                return clientService.connecter(email, pass);
            }
            @Override
            protected void done() {
                try {
                    Client client = get();
                    Compte compte = compteService.trouverParClientId(client.getId());
                    openDashboard(client, compte);
                } catch (Exception ex) {
                    view.setLoginError(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    //  Inscription
    public void handleRegister() {
        view.setRegisterError("");

        String nom     = view.getRegisterNom();
        String prenom  = view.getRegisterPrenom();
        String email   = view.getRegisterEmail();
        String pass    = view.getRegisterPassword();
        String confirm = view.getRegisterConfirm();

        // Validations côté client
        if (nom.isBlank()) { view.setRegisterError("Veuillez saisir votre nom."); return; }
        if (prenom.isBlank()) { view.setRegisterError("Veuillez saisir votre prénom."); return; }
        if (email.isBlank() || !email.contains("@")) { view.setRegisterError("Email invalide."); return; }
        if (pass.length() < 6) { view.setRegisterError("Le mot de passe doit contenir au moins 6 caractères."); return; }
        if (!pass.equals(confirm)) { view.setRegisterError("Les mots de passe ne correspondent pas."); return; }

        SwingWorker<Client, Void> worker = new SwingWorker<>() {
            @Override
            protected Client doInBackground() throws Exception {
                // L'inscription insère dans clients
                //TRIGGER MySQL crée automatiquement le commpte
                return clientService.inscrire(nom, prenom, email, pass);
            }
            @Override
            protected void done() {
                try {
                    get();
                    view.setRegisterSuccess(" Compte créé ! Un compte bancaire vous a été attribué automatiquement. Connectez-vous !");
                    view.clearRegisterForm();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.setRegisterError(msg);
                }
            }
        };
        worker.execute();
    }

    //  Ouvrir le tableau de bord
    private void openDashboard(Client client, Compte compte) {
        DashboardView dashboard = new DashboardView(client, compte);
        dashboard.setVisible(true);
        view.dispose();
    }
}
