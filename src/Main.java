import View.AuthView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public  class Main {
    public static void main(String[] arg) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banque_appli",
                    "root",
                    ""
            );
            System.out.println("Connexion réussie !");

        } catch (Exception e) {
            e.printStackTrace();
        }

        new AuthView().setVisible(true);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}


        SwingUtilities.invokeLater(() -> {
            AuthView authView = new AuthView();
            authView.setVisible(true);
        });
    }
}
