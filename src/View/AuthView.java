package View;

import Controller.AuthController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

//connexion /inscription.
public class AuthView extends JFrame {

    private AuthController controller;

    //Connexion
    private UIComponents.RoundTextField    tfEmailLogin;
    private UIComponents.RoundPasswordField tfPassLogin;
    private UIComponents.RoundButton       btnLogin;
    private JLabel                         lblLoginError;

    //Inscription
    private UIComponents.RoundTextField    tfNom;
    private UIComponents.RoundTextField    tfPrenom;
    private UIComponents.RoundTextField    tfEmailRegister;
    private UIComponents.RoundPasswordField tfPassRegister;
    private UIComponents.RoundPasswordField tfPassConfirm;
    private UIComponents.RoundButton       btnRegister;
    private JLabel                         lblRegisterError;

    public AuthView() {
        setTitle("BankyMG – Banque Numérique");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        controller = new AuthController(this);
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(Theme.BLEU_FONCE);
        setContentPane(root);

        root.add(buildLoginPanel());
        root.add(buildRegisterPanel());
    }

    //  Panneau de connexxion
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.BLEU_MOYEN);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(60, 50, 60, 50));

        //titre
        JLabel logo = new JLabel("Bank MIH");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logo.setForeground(Theme.OR);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UIComponents.makeLabel("Connexion à votre espace", Theme.FONT_NORMAL, Theme.GRIS_CLAIR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Champs
        tfEmailLogin = new UIComponents.RoundTextField(20);
        tfEmailLogin.putClientProperty("placeholder", "Email");
        setPlaceholder(tfEmailLogin, "Adresse email");

        tfPassLogin = new UIComponents.RoundPasswordField(20);
        setPlaceholder(tfPassLogin, "Mot de passe");

        btnLogin = new UIComponents.RoundButton("  Se connecter  ", Theme.OR, Theme.OR_CLAIR);
        btnLogin.setForeground(Theme.BLEU_FONCE);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> controller.handleLogin());

        lblLoginError = UIComponents.makeLabel("", Theme.FONT_SMALL, Theme.ROUGE);
        lblLoginError.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Assembl
        panel.add(logo);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        panel.add(makeFieldBlock("Email", tfEmailLogin));
        panel.add(Box.createVerticalStrut(16));
        panel.add(makeFieldBlock("Mot de passe", tfPassLogin));
        panel.add(Box.createVerticalStrut(30));
        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblLoginError);

        return panel;
    }

    //panneau inscription
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.BLEU_CLAIR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel titre = UIComponents.makeLabel("Créer un compte", Theme.FONT_SOUS_TITRE, Theme.BLANC);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = UIComponents.makeLabel("Un compte bancaire est créé automatiquement ✨", Theme.FONT_SMALL, Theme.OR_CLAIR);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        tfNom           = new UIComponents.RoundTextField(20);
        tfPrenom        = new UIComponents.RoundTextField(20);
        tfEmailRegister = new UIComponents.RoundTextField(20);
        tfPassRegister  = new UIComponents.RoundPasswordField(20);
        tfPassConfirm   = new UIComponents.RoundPasswordField(20);

        btnRegister = new UIComponents.RoundButton("  S'inscrire  ", new Color(0x2C7A4B), new Color(0x38A169));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> controller.handleRegister());

        lblRegisterError = UIComponents.makeLabel("", Theme.FONT_SMALL, Theme.ROUGE);
        lblRegisterError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ligne nom et prénom
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 10, 0));
        nameRow.setOpaque(false);
        nameRow.add(makeFieldBlock("Nom", tfNom));
        nameRow.add(makeFieldBlock("Prénom", tfPrenom));

        panel.add(titre);
        panel.add(Box.createVerticalStrut(6));
        panel.add(info);
        panel.add(Box.createVerticalStrut(24));
        panel.add(nameRow);
        panel.add(Box.createVerticalStrut(12));
        panel.add(makeFieldBlock("Email", tfEmailRegister));
        panel.add(Box.createVerticalStrut(12));
        panel.add(makeFieldBlock("Mot de passe", tfPassRegister));
        panel.add(Box.createVerticalStrut(12));
        panel.add(makeFieldBlock("Confirmer le mot de passe", tfPassConfirm));
        panel.add(Box.createVerticalStrut(24));
        panel.add(btnRegister);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblRegisterError);

        return panel;
    }

    // aide
    private JPanel makeFieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = UIComponents.makeLabel(labelText, Theme.FONT_SMALL, Theme.GRIS_CLAIR);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Theme.GRIS_CLAIR);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Theme.BLANC);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Theme.GRIS_CLAIR);
                }
            }
        });
    }

    private void setPlaceholder(JPasswordField field, String placeholder) {
        field.setForeground(Theme.GRIS_CLAIR);
        field.setText(placeholder);
        field.setEchoChar((char) 0);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String text = new String(field.getPassword());
                if (text.equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Theme.BLANC);
                    field.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(Theme.GRIS_CLAIR);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }

    // getters pour le controller
    public String getLoginEmail()    { return tfEmailLogin.getText().trim(); }
    public String getLoginPassword() { return new String(tfPassLogin.getPassword()); }

    public String getRegisterNom()    { return tfNom.getText().trim(); }
    public String getRegisterPrenom() { return tfPrenom.getText().trim(); }
    public String getRegisterEmail()  { return tfEmailRegister.getText().trim(); }
    public String getRegisterPassword()  { return new String(tfPassRegister.getPassword()); }
    public String getRegisterConfirm()   { return new String(tfPassConfirm.getPassword()); }

    public void setLoginError(String msg)    { lblLoginError.setText(msg); }
    public void setRegisterError(String msg) { lblRegisterError.setText("<html><center>" + msg + "</center></html>"); }
    public void setRegisterSuccess(String msg) {
        lblRegisterError.setForeground(Theme.VERT);
        lblRegisterError.setText("<html><center>" + msg + "</center></html>");
    }

    public void clearRegisterForm() {
        tfNom.setText("");
        tfPrenom.setText("");
        tfEmailRegister.setText("");
        tfPassRegister.setText("");
        tfPassConfirm.setText("");
        lblRegisterError.setForeground(Theme.ROUGE);
        lblRegisterError.setText("");
    }
}
