-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : sam. 16 mai 2026 à 06:23
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `banque_appli`
--

-- --------------------------------------------------------

--
-- Structure de la table `clients`
--

CREATE TABLE `clients` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `date_inscription` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `clients`
--

INSERT INTO `clients` (`id`, `nom`, `prenom`, `email`, `mot_de_passe`, `date_inscription`) VALUES
(1, 'DEMO', 'Admin', 'admin@banque.mg', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '2026-05-15 20:33:51');

--
-- Déclencheurs `clients`
--
DELIMITER $$
CREATE TRIGGER `after_client_delete` AFTER DELETE ON `clients` FOR EACH ROW BEGIN
    INSERT INTO logs_audit (table_name, operation, message)
    VALUES ('clients', 'DELETE',
            CONCAT('Client supprimé : ', OLD.prenom, ' ', OLD.nom,
                   ' (', OLD.email, ')'));
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_client_insert` AFTER INSERT ON `clients` FOR EACH ROW BEGIN
    DECLARE numero VARCHAR(20);
    -- Générer un numéro de compte unique : MG + id + timestamp
    SET numero = CONCAT('MG', LPAD(NEW.id, 5, '0'),
                        FLOOR(RAND() * 9000 + 1000));

    -- Créer le compte avec solde initial 0
    INSERT INTO comptes (client_id, numero_compte, solde)
    VALUES (NEW.id, numero, 0.00);

    -- Log de l'opération
    INSERT INTO logs_audit (table_name, operation, message)
    VALUES ('comptes', 'AUTO_CREATE',
            CONCAT('Compte créé automatiquement pour le client : ',
                   NEW.prenom, ' ', NEW.nom,
                   ' | Numéro : ', numero));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `comptes`
--

CREATE TABLE `comptes` (
  `id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `numero_compte` varchar(20) NOT NULL,
  `solde` decimal(15,2) DEFAULT 0.00,
  `date_creation` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `comptes`
--

INSERT INTO `comptes` (`id`, `client_id`, `numero_compte`, `solde`, `date_creation`) VALUES
(1, 1, 'MG000011830', 0.00, '2026-05-15 20:33:51');

--
-- Déclencheurs `comptes`
--
DELIMITER $$
CREATE TRIGGER `before_update_solde` BEFORE UPDATE ON `comptes` FOR EACH ROW BEGIN
    IF NEW.solde < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Solde insuffisant : le solde ne peut pas être négatif.';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `logs_audit`
--

CREATE TABLE `logs_audit` (
  `id` int(11) NOT NULL,
  `table_name` varchar(50) DEFAULT NULL,
  `operation` varchar(50) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `date_log` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `logs_audit`
--

INSERT INTO `logs_audit` (`id`, `table_name`, `operation`, `message`, `date_log`) VALUES
(1, 'comptes', 'AUTO_CREATE', 'Compte créé automatiquement pour le client : Admin DEMO | Numéro : MG000011830', '2026-05-15 20:33:51');

-- --------------------------------------------------------

--
-- Structure de la table `transactions`
--

CREATE TABLE `transactions` (
  `id` int(11) NOT NULL,
  `compte_source_id` int(11) DEFAULT NULL,
  `compte_dest_id` int(11) DEFAULT NULL,
  `montant` decimal(15,2) NOT NULL,
  `type_transaction` enum('DEPOT','RETRAIT','TRANSFERT') NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `date_transaction` datetime DEFAULT current_timestamp(),
  `statut` enum('SUCCES','ECHEC') DEFAULT 'SUCCES'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déclencheurs `transactions`
--
DELIMITER $$
CREATE TRIGGER `after_transaction_insert` AFTER INSERT ON `transactions` FOR EACH ROW BEGIN
    INSERT INTO logs_audit (table_name, operation, message)
    VALUES ('transactions', NEW.type_transaction,
            CONCAT('[', NEW.statut, '] ',
                   NEW.type_transaction,
                   ' de ', NEW.montant, ' Ar',
                   CASE
                       WHEN NEW.type_transaction = 'TRANSFERT'
                           THEN CONCAT(' | Compte src: ', NEW.compte_source_id,
                                       ' → dest: ', NEW.compte_dest_id)
                       WHEN NEW.type_transaction = 'DEPOT'
                           THEN CONCAT(' | Compte: ', NEW.compte_dest_id)
                       ELSE CONCAT(' | Compte: ', NEW.compte_source_id)
                   END,
                   ' | ', NEW.description));
END
$$
DELIMITER ;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `clients`
--
ALTER TABLE `clients`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Index pour la table `comptes`
--
ALTER TABLE `comptes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero_compte` (`numero_compte`),
  ADD KEY `client_id` (`client_id`);

--
-- Index pour la table `logs_audit`
--
ALTER TABLE `logs_audit`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `compte_source_id` (`compte_source_id`),
  ADD KEY `compte_dest_id` (`compte_dest_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `clients`
--
ALTER TABLE `clients`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `comptes`
--
ALTER TABLE `comptes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `logs_audit`
--
ALTER TABLE `logs_audit`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `comptes`
--
ALTER TABLE `comptes`
  ADD CONSTRAINT `comptes_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`compte_source_id`) REFERENCES `comptes` (`id`),
  ADD CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`compte_dest_id`) REFERENCES `comptes` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
