package View;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

//
public class UIComponents {

    //  Bouton arrondi avec survol animé
    public static class RoundButton extends JButton {
        private Color bgColor;
        private Color hoverColor;
        private int radius;

        public RoundButton(String text, Color bg, Color hover) {
            super(text);
            this.bgColor    = bg;
            this.hoverColor = hover;
            this.radius     = Theme.RADIUS;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Theme.BLANC);
            setFont(Theme.FONT_BOLD);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(getPreferredSize().width + 20, 44));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
                @Override public void mouseExited (MouseEvent e) { setBackground(bgColor);    repaint(); }
            });
            setBackground(bg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            super.paintComponent(g);
            g2.dispose();
        }
    }
    //  Champ de texte arrondi stylisé
    public static class RoundTextField extends JTextField {
        public RoundTextField(int cols) {
            super(cols);
            setOpaque(false);
            setBackground(Theme.INPUT_BG);
            setForeground(Theme.BLANC);
            setCaretColor(Theme.OR);
            setFont(Theme.FONT_NORMAL);
            setBorder(new CompoundBorder(
                    new RoundBorder(Theme.INPUT_BORDER, Theme.RADIUS),
                    new EmptyBorder(8, 14, 8, 14)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS * 2, Theme.RADIUS * 2);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    //  Champ mot de passe arrondi
    public static class RoundPasswordField extends JPasswordField {
        public RoundPasswordField(int cols) {
            super(cols);
            setOpaque(false);
            setBackground(Theme.INPUT_BG);
            setForeground(Theme.BLANC);
            setCaretColor(Theme.OR);
            setFont(Theme.FONT_NORMAL);
            setBorder(new CompoundBorder(
                    new RoundBorder(Theme.INPUT_BORDER, Theme.RADIUS),
                    new EmptyBorder(8, 14, 8, 14)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS * 2, Theme.RADIUS * 2);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    //  Panneau avec fond arrondi
    public static class CardPanel extends JPanel {
        private Color bg;
        private int radius;

        public CardPanel(Color bg) {
            this.bg = bg;
            this.radius = Theme.RADIUS;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    //  Bordure arrondie
    public static class RoundBorder extends AbstractBorder {
        private Color color;
        private int radius;

        public RoundBorder(Color color, int radius) {
            this.color  = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(radius, radius, radius, radius); }
    }

    //  Label avec icône colorée + texte
    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    //  Séparateur stylisé

    public static JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.SEPARATEUR);
        sep.setBackground(Theme.SEPARATEUR);
        return sep;
    }

    //  Afficher un message d'erreur stylisé
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
}
