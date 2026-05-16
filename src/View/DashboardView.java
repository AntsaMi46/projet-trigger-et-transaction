package View;

import Controller.DashboardController;
import Model.Client;
import Model.Compte;
import Model.Transaction;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

//Tableau de bord client.
public class DashboardView extends JFrame {

    private DashboardController controller;
    private Client  client;
    private Compte  compte;

    //Header
    private JLabel  lblNomClient;
    private JLabel  lblNumeroCompte;
    private JLabel  lblSolde;

    //Table historique
    private DefaultTableModel tableModel;
    private JTable            table;

    //Panel transfert
    private UIComponents.RoundTextField    tfNumDest;
    private UIComponents.RoundTextField    tfMontantTransfert;
    private UIComponents.RoundTextField    tfDescTransfert;
    private UIComponents.RoundButton       btnTransferer;
    private JLabel                         lblTransfertMsg;

    //Panel dépôt/retrait
    private UIComponents.RoundTextField    tfMontantOperation;
    private UIComponents.RoundTextField    tfDescOperation;
    private UIComponents.RoundButton       btnDeposer;
    private UIComponents.RoundButton       btnRetirer;
    private JLabel                         lblOperationMsg;

    private static final NumberFormat NF = NumberFormat.getInstance(Locale.FRANCE);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DashboardView(Client client, Compte compte) {
        this.client = client;
        this.compte = compte;

        setTitle("BankyMG – Tableau de bord");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1000, 650));

        buildUI();
        controller = new DashboardController(this, client, compte);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BLEU_FONCE);
        setContentPane(root);

        root.add(buildTopBar(),    BorderLayout.NORTH);
        root.add(buildCenter(),    BorderLayout.CENTER);
        root.add(buildSidePanel(), BorderLayout.EAST);
    }

    //barre supperieur
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BLEU_MOYEN);
        bar.setBorder(new EmptyBorder(16, 30, 16, 30));

        //nom client
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel(" Bank MIH");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Theme.OR);

        lblNomClient = UIComponents.makeLabel("Bonjour, " + client.getNomComplet(), Theme.FONT_BOLD, Theme.BLANC);
        left.add(logo);
        left.add(new JSeparator(SwingConstants.VERTICAL) {{ setPreferredSize(new Dimension(1, 30)); setForeground(Theme.SEPARATEUR); }});
        left.add(lblNomClient);

        //solde + déconnexion
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        lblSolde = new JLabel();
        lblSolde.setFont(Theme.FONT_SOUS_TITRE);
        updateSoldeLabel();

        UIComponents.RoundButton btnDeco = new UIComponents.RoundButton("Déconnexion", new Color(0x742A2A), new Color(0xC53030));
        btnDeco.setFont(Theme.FONT_SMALL);
        btnDeco.addActionListener(e -> controller.handleLogout());

        UIComponents.RoundButton btnRefresh = new UIComponents.RoundButton("⟳ Actualiser", Theme.BLEU_BTN, new Color(0x2C5282));
        btnRefresh.setFont(Theme.FONT_SMALL);
        btnRefresh.addActionListener(e -> controller.handleRefresh());

        right.add(lblSolde);
        right.add(btnRefresh);
        right.add(btnDeco);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    //carte solde + historique
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 24, 20, 12));

        // Carte solde
        center.add(buildSoldeCard(), BorderLayout.NORTH);

        // Historique
        center.add(buildHistoriquePanel(), BorderLayout.CENTER);

        return center;
    }

    private JPanel buildSoldeCard() {
        UIComponents.CardPanel card = new UIComponents.CardPanel(Theme.BLEU_CLAIR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 28, 20, 28));
        card.setPreferredSize(new Dimension(0, 130));

        JPanel left = new JPanel(new GridLayout(3, 1, 0, 4));
        left.setOpaque(false);
        left.add(UIComponents.makeLabel("SOLDE DISPONIBLE", Theme.FONT_SMALL, Theme.GRIS_CLAIR));
        lblSolde = new JLabel(formatMontant(compte.getSolde()) + " Ar");
        lblSolde.setFont(Theme.FONT_AMOUNT);
        lblSolde.setForeground(Theme.OR);
        left.add(lblSolde);
        lblNumeroCompte = UIComponents.makeLabel("N° " + compte.getNumeroCompte(), Theme.FONT_MONO, Theme.GRIS_CLAIR);
        left.add(lblNumeroCompte);

        JPanel right = new JPanel(new GridLayout(2, 1, 0, 6));
        right.setOpaque(false);
        right.add(UIComponents.makeLabel("Compte créé le " +
                        (compte.getDateCreation() != null ? compte.getDateCreation().format(DTF) : "-"),
                Theme.FONT_SMALL, Theme.GRIS_CLAIR));
        right.add(UIComponents.makeLabel("Client n°" + client.getId() + " | " + client.getEmail(),
                Theme.FONT_SMALL, Theme.GRIS_CLAIR));

        card.add(left,  BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildHistoriquePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel titre = UIComponents.makeLabel(" Historique des transactions", Theme.FONT_SOUS_TITRE, Theme.BLANC);
        panel.add(titre, BorderLayout.NORTH);

        String[] cols = { "#", "Date", "Type", "Description", "Depuis / Vers", "Montant (Ar)", "Statut" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Theme.BLEU_MOYEN);
        scroll.getViewport().setBackground(Theme.BLEU_MOYEN);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.SEPARATEUR, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    //  Panneau latéral de transfert et opération
    private JPanel buildSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Theme.BLEU_MOYEN);
        side.setBorder(new EmptyBorder(20, 16, 20, 20));
        side.setPreferredSize(new Dimension(300, 0));

        side.add(buildTransfertForm());
        side.add(Box.createVerticalStrut(20));
        side.add(UIComponents.makeSeparator());
        side.add(Box.createVerticalStrut(20));
        side.add(buildOperationForm());

        return side;
    }

    private JPanel buildTransfertForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        p.add(UIComponents.makeLabel("Transfert", Theme.FONT_BOLD, Theme.OR));
        p.add(Box.createVerticalStrut(14));

        tfNumDest = new UIComponents.RoundTextField(15);
        tfMontantTransfert = new UIComponents.RoundTextField(15);
        tfDescTransfert    = new UIComponents.RoundTextField(15);

        p.add(makeFieldRow("Numéro de compte destinataire", tfNumDest));
        p.add(Box.createVerticalStrut(10));
        p.add(makeFieldRow("Montant (Ar)", tfMontantTransfert));
        p.add(Box.createVerticalStrut(10));
        p.add(makeFieldRow("Description (optionnel)", tfDescTransfert));
        p.add(Box.createVerticalStrut(14));

        btnTransferer = new UIComponents.RoundButton("  Transférer  ", Theme.OR, Theme.OR_CLAIR);
        btnTransferer.setForeground(Theme.BLEU_FONCE);
        btnTransferer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTransferer.addActionListener(e -> controller.handleTransfert());

        lblTransfertMsg = new JLabel("");
        lblTransfertMsg.setFont(Theme.FONT_SMALL);
        lblTransfertMsg.setForeground(Theme.ROUGE);
        lblTransfertMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(btnTransferer);
        p.add(Box.createVerticalStrut(6));
        p.add(lblTransfertMsg);
        return p;
    }

    private JPanel buildOperationForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        p.add(UIComponents.makeLabel("Dépôt / Retrait", Theme.FONT_BOLD, Theme.OR));
        p.add(Box.createVerticalStrut(14));

        tfMontantOperation = new UIComponents.RoundTextField(15);
        tfDescOperation    = new UIComponents.RoundTextField(15);

        p.add(makeFieldRow("Montant (Ar)", tfMontantOperation));
        p.add(Box.createVerticalStrut(10));
        p.add(makeFieldRow("Description", tfDescOperation));
        p.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btnDeposer = new UIComponents.RoundButton("Déposer", new Color(0x276749), new Color(0x38A169));
        btnRetirer = new UIComponents.RoundButton("Retirer",  new Color(0x742A2A), new Color(0xC53030));
        btnDeposer.addActionListener(e -> controller.handleDepot());
        btnRetirer.addActionListener(e -> controller.handleRetrait());
        btnRow.add(btnDeposer);
        btnRow.add(btnRetirer);

        lblOperationMsg = new JLabel("");
        lblOperationMsg.setFont(Theme.FONT_SMALL);
        lblOperationMsg.setForeground(Theme.ROUGE);
        lblOperationMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(btnRow);
        p.add(Box.createVerticalStrut(6));
        p.add(lblOperationMsg);
        return p;
    }

    //  Helper
    private JPanel makeFieldRow(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(UIComponents.makeLabel(label, Theme.FONT_SMALL, Theme.GRIS_CLAIR), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        return p;
    }

    private void styleTable(JTable t) {
        t.setBackground(Theme.BLEU_MOYEN);
        t.setForeground(Theme.BLANC);
        t.setFont(Theme.FONT_SMALL);
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(Theme.BLEU_CLAIR);
        t.setSelectionForeground(Theme.OR);

        JTableHeader header = t.getTableHeader();
        header.setBackground(Theme.BLEU_FONCE);
        header.setForeground(Theme.OR);
        header.setFont(Theme.FONT_BOLD);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // Largeurs colonnes
        int[] widths = {40, 130, 90, 160, 140, 120, 70};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Renderer coloré pour montant et type
        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBackground(sel ? Theme.BLEU_CLAIR : Theme.BLEU_MOYEN);
                String v = val != null ? val.toString() : "";
                if (v.startsWith("+")) setForeground(Theme.VERT);
                else if (v.startsWith("-")) setForeground(Theme.ROUGE);
                else setForeground(Theme.BLANC);
                return this;
            }
        });

        // Renderer statut
        t.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(sel ? Theme.BLEU_CLAIR : Theme.BLEU_MOYEN);
                String v = val != null ? val.toString() : "";
                setForeground("SUCCES".equals(v) ? Theme.VERT : Theme.ROUGE);
                return this;
            }
        });

        // Renderer ligne alternée
        DefaultTableCellRenderer baseRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                setBackground(sel ? Theme.BLEU_CLAIR : (row % 2 == 0 ? Theme.BLEU_MOYEN : Theme.BLEU_FONCE));
                setForeground(Theme.BLANC);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        for (int i = 0; i < 5; i++) t.getColumnModel().getColumn(i).setCellRenderer(baseRenderer);
    }

    private void updateSoldeLabel() {
        if (lblSolde != null) {
            lblSolde.setText(formatMontant(compte.getSolde()) + " Ar");
        }
    }

    private String formatMontant(BigDecimal v) {
        return NF.format(v);
    }
    //  Méthodes appelées par le controller
    public void updateCompte(Compte c) {
        this.compte = c;
        lblSolde.setText(formatMontant(c.getSolde()) + " Ar");
        lblNumeroCompte.setText("N° " + c.getNumeroCompte());
    }

    public void loadHistorique(List<Transaction> transactions, int compteId) {
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            boolean isCredit = t.getCompteDestId() != null && t.getCompteDestId() == compteId;
            boolean isDebit  = t.getCompteSourceId() != null && t.getCompteSourceId() == compteId;

            String montantStr;
            if (t.getTypeTransaction() == Transaction.Type.DEPOT) {
                montantStr = "+ " + formatMontant(t.getMontant());
            } else if (t.getTypeTransaction() == Transaction.Type.RETRAIT) {
                montantStr = "- " + formatMontant(t.getMontant());
            } else {
                montantStr = isCredit
                        ? "+ " + formatMontant(t.getMontant())
                        : "- " + formatMontant(t.getMontant());
            }

            String direction = "";
            if (t.getTypeTransaction() == Transaction.Type.TRANSFERT) {
                direction = isCredit
                        ? "← " + (t.getNumeroSource() != null ? t.getNumeroSource() : "-")
                        : "→ " + (t.getNumeroDest()   != null ? t.getNumeroDest()   : "-");
            }

            String dateStr = t.getDateTransaction() != null ? t.getDateTransaction().format(DTF) : "-";

            tableModel.addRow(new Object[]{
                    t.getId(),
                    dateStr,
                    t.getTypeTransaction().name(),
                    t.getDescription(),
                    direction,
                    montantStr,
                    t.getStatut().name()
            });
        }
    }

    public void setTransfertMessage(String msg, boolean isError) {
        lblTransfertMsg.setForeground(isError ? Theme.ROUGE : Theme.VERT);
        lblTransfertMsg.setText("<html><center>" + msg + "</center></html>");
    }

    public void setOperationMessage(String msg, boolean isError) {
        lblOperationMsg.setForeground(isError ? Theme.ROUGE : Theme.VERT);
        lblOperationMsg.setText("<html><center>" + msg + "</center></html>");
    }

    public void clearTransfertForm() {
        tfNumDest.setText("");
        tfMontantTransfert.setText("");
        tfDescTransfert.setText("");
    }

    public void clearOperationForm() {
        tfMontantOperation.setText("");
        tfDescOperation.setText("");
    }

    // Getters pour controller
    public String getNumDest()              { return tfNumDest.getText().trim(); }
    public String getMontantTransfert()     { return tfMontantTransfert.getText().trim(); }
    public String getDescTransfert()        { return tfDescTransfert.getText().trim(); }
    public String getMontantOperation()     { return tfMontantOperation.getText().trim(); }
    public String getDescOperation()        { return tfDescOperation.getText().trim(); }
    public Compte getCompte()               { return compte; }
}
