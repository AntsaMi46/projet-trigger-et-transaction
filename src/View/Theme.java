package View;

import java.awt.*;

public class Theme {

    //Couleurs principale
    public static final Color BLEU_FONCE    = new Color(0x0D1B2A);   // fond sombre
    public static final Color BLEU_MOYEN    = new Color(0x1B2A3B);   // panneaux
    public static final Color BLEU_CLAIR    = new Color(0x243447);   // cartes
    public static final Color OR            = new Color(0xF5A623);   // accent doré
    public static final Color OR_CLAIR      = new Color(0xFFD080);   // texte doré clair
    public static final Color BLANC         = new Color(0xF0F4F8);   // texte principal
    public static final Color GRIS_CLAIR    = new Color(0xA0AEC0);   // texte secondaire
    public static final Color VERT          = new Color(0x48BB78);   // crédit / succès
    public static final Color ROUGE         = new Color(0xFC8181);   // débit / erreur
    public static final Color BLEU_BTN      = new Color(0x2B6CB0);   // bouton secondaire
    public static final Color SEPARATEUR    = new Color(0x2D3748);   // ligne séparatrice
    public static final Color INPUT_BG      = new Color(0x1A2332);   // fond input
    public static final Color INPUT_BORDER  = new Color(0x3A4A5C);   // bordure input

    //Police
    public static final Font FONT_TITRE     = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font FONT_SOUS_TITRE= new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_NORMAL    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD      = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_MONO      = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FONT_AMOUNT    = new Font("Segoe UI", Font.BOLD,  32);

    //Dimension
    public static final int  RADIUS         = 12;
    public static final int  PAD            = 20;
}
