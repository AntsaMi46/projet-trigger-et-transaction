package Controller;

import Model.Client;
import Model.Compte;
import Model.Transaction;
import Service.CompteService;
import Service.TransactionService;
import View.AuthView;
import View.DashboardView;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

public class DashboardController {
    private final DashboardView view;
    private final Client client;
    private Compte compte;
    private final TransactionService txService= new TransactionService();
    private final CompteService compteService=new CompteService();
    public DashboardController(DashboardView view, Client client, Compte compte){
        this.view=view;
        this.client=client;
        this.compte = compte;
        handleRefresh();
    }
    public void handleRefresh(){
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            Compte compteRefreshed;
            List<Transaction> history;

            @Override
            protected Void doInBackground() throws Exception {
                compteRefreshed = compteService.rafraichir(compte);
                history = txService.getHistorique(compteRefreshed.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    compte = compteRefreshed;
                    view.updateCompte(compte);
                    view.loadHistorique(history, compte.getId());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Erreur d'actualisation : " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    public void handleTransfert() {
        view.setTransfertMessage("", false);

        String numDest = view.getNumDest();
        String montantS = view.getMontantTransfert();
        String desc = view.getDescTransfert();

        if (numDest.isBlank()) {
            view.setTransfertMessage("Numéro de compte destinataire requis.", true);
            return;
        }
        if (montantS.isBlank()) {
            view.setTransfertMessage("Veuillez saisir un montant.", true);
            return;
        }

        BigDecimal montant;
        try {
            montant = new BigDecimal(montantS.replace(",", ".").replace(" ", ""));
        } catch (NumberFormatException e) {
            view.setTransfertMessage("Montant invalide.", true);
            return;
        }
        final Compte currentCompte = compte;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                //TRANSACTION SQL,trigger before_updatesole,after_transaction_insert
                txService.effectuerTransfert(currentCompte, numDest, montant, desc);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.setTransfertMessage("✅ Transfert effectué avec succès !", false);
                    view.clearTransfertForm();
                    handleRefresh();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.setTransfertMessage(msg, true);
                }
            }
        };
        worker.execute();
    }
    //dépot
    public void handleDepot() {
        view.setOperationMessage("", false);
        String montantS = view.getMontantOperation();
        String desc     = view.getDescOperation();

        BigDecimal montant = parseMontant(montantS);
        if (montant == null) { view.setOperationMessage("Montant invalide.", true); return; }

        final Compte currentCompte = compte;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                txService.effectuerDepot(currentCompte, montant, desc.isBlank() ? "Dépôt" : desc);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    view.setOperationMessage("Dépôt effectué !", false);
                    view.clearOperationForm();
                    handleRefresh();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.setOperationMessage(msg, true);
                }
            }
        };
        worker.execute();
    }
    //retrait
    public void handleRetrait() {
        view.setOperationMessage("", false);
        String montantS = view.getMontantOperation();
        String desc     = view.getDescOperation();

        BigDecimal montant = parseMontant(montantS);
        if (montant == null) { view.setOperationMessage("Montant invalide.", true); return; }

        final Compte currentCompte = compte;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                txService.effectuerRetrait(currentCompte, montant, desc.isBlank() ? "Retrait" : desc);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    view.setOperationMessage("✅ Retrait effectué !", false);
                    view.clearOperationForm();
                    handleRefresh();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.setOperationMessage(msg, true);
                }
            }
        };
        worker.execute();
    }
    //déconnexion
    public void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment vous déconnecter ?",
                "Déconnexion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthView auth = new AuthView();
            auth.setVisible(true);
            view.dispose();
        }
    }
    //aide
    private BigDecimal parseMontant(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new BigDecimal(s.replace(",", ".").replace(" ", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
