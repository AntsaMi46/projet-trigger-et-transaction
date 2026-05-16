package Model;

import java.time.LocalDateTime;

public class Client {
        private int id;
        private String nom;
        private String prenom;
        private String email;
        private String motDePasse;
        private LocalDateTime dateInscription;

        public Client() {}

        public Client(int id, String nom, String prenom, String email, LocalDateTime dateInscription) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.dateInscription = dateInscription;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMotDePasse() {
            return motDePasse;
        }

        public void setMotDePasse(String motDePasse) {
            this.motDePasse = motDePasse;
        }

        public LocalDateTime getDateInscription() {
            return dateInscription;
        }

        public void setDateInscription(LocalDateTime d) {
            this.dateInscription = dateInscription=d;
        }
    public String getNomComplet() { return prenom + " " + nom.toUpperCase(); }

    @Override
    public String toString() { return getNomComplet() + " <" + email + ">"; }
    }

