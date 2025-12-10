/*M!999999- enable the sandbox mode */
-- MariaDB dump 10.19  Distrib 10.11.10-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: hobbyhandwerker
-- ------------------------------------------------------
-- Server version	10.11.10-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;



/* -----------------------------------------------------
   TABLE: role
----------------------------------------------------- */

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 /*!40101 SET character_set_client = utf8 */;

CREATE TABLE `role` (
  `ID_Role` INT(11) NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`ID_Role`),
  UNIQUE KEY `ID_Role_UNIQUE` (`ID_Role`),
  UNIQUE KEY `Name_UNIQUE` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

/* Insert roles */
LOCK TABLES `role` WRITE;
INSERT INTO `role` (`ID_Role`, `Name`) VALUES
(1,'visitor'),
(2,'user'),
(3,'admin');
UNLOCK TABLES;



/* -----------------------------------------------------
   TABLE: tool
----------------------------------------------------- */

DROP TABLE IF EXISTS `tool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 /*!40101 SET character_set_client = utf8 */;

CREATE TABLE `tool` (
  `ID_Tool` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(40) NOT NULL,
  PRIMARY KEY (`ID_Tool`),
  UNIQUE KEY `ID_Tool_UNIQUE` (`ID_Tool`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

/* Data for table tool */

LOCK TABLES `tool` WRITE;
/*!40000 ALTER TABLE `tool` DISABLE KEYS */;
INSERT INTO `tool` VALUES
(1,'Hammer'),
(2,'Kreuzschraubenzieher'),
(3,'Schlitzschraubenzieher'),
(4,'Aale'),
(5,'Säge'),
(6,'Motorsäge'),
(7,'Kreissäge'),
(8,'Stichsäge'),
(9,'Flex'),
(10,'Winkelschleifer');
/*!40000 ALTER TABLE `tool` ENABLE KEYS */;
UNLOCK TABLES;



/* -----------------------------------------------------
   TABLE: user
----------------------------------------------------- */

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 /*!40101 SET character_set_client = utf8 */;

CREATE TABLE `user` (
  `ID_User` int(11) NOT NULL AUTO_INCREMENT,
  `Email` varchar(60) DEFAULT NULL,
  `Username` varchar(30) NOT NULL,
  `Password` varchar(100) NOT NULL,
  `FS_Role` int(11) NOT NULL DEFAULT 2,
  PRIMARY KEY (`ID_User`),
  UNIQUE KEY `ID_User_UNIQUE` (`ID_User`),
  UNIQUE KEY `Email_UNIQUE` (`Email`),
  KEY `REL_Role_User_idx` (`FS_Role`),
  CONSTRAINT `REL_Role_User` FOREIGN KEY (`FS_Role`) REFERENCES `role` (`ID_Role`) ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

/*!40101 SET character_set_client = @saved_cs_client */;



/* -----------------------------------------------------
   TABLE: user_has_tool
----------------------------------------------------- */

DROP TABLE IF EXISTS `user_has_tool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 /*!40101 SET character_set_client = utf8 */;

CREATE TABLE `user_has_tool` (
  `FS_User` int(11) NOT NULL,
  `FS_Tool` int(11) NOT NULL,
  `Brand` varchar(40) DEFAULT NULL,
  `Model` varchar(40) DEFAULT NULL,
  `Description` mediumtext NOT NULL,
  `Picture` blob DEFAULT NULL,
  PRIMARY KEY (`FS_User`,`FS_Tool`),
  KEY `REL_Tool_User_has_Tool1_idx` (`FS_Tool`),
  KEY `REL_User_User_has_Tool_idx` (`FS_User`),
  CONSTRAINT `REL_Tool_User_has_Tool1` FOREIGN KEY (`FS_Tool`) REFERENCES `tool` (`ID_Tool`) ON UPDATE NO ACTION,
  CONSTRAINT `REL_User_User_has_Tool` FOREIGN KEY (`FS_User`) REFERENCES `user` (`ID_User`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

/*!40101 SET character_set_client = @saved_cs_client */;



/* -----------------------------------------------------
   Data for table user_has_tool
----------------------------------------------------- */

LOCK TABLES `user_has_tool` WRITE;
/*!40000 ALTER TABLE `user_has_tool` DISABLE KEYS */;
INSERT INTO `user_has_tool` VALUES
(1,2,'Twister','Criss-Cross Master 3000','Ist da, wenn mal eine Schraube locker ist',NULL),
(2,1,'Hammermarke','Hammermodell','Kann hämmern, guter Zustand',NULL),
(2,2,'Superzieher','Kreuz','Wie neu',NULL),
(2,3,'Superzieher','Schlitz','Mit Trauma versehen',NULL),
(2,4,'AAAAAAA','Penetrator','Kann alles löchern :)',NULL),
(2,5,'Saugh','UIIAI','Des Magiers treuester Begleiter',NULL),
(2,6,'Saugh','Brrrrrrr','Leatherface Cosplay Prop',NULL),
(2,7,'FidgetTools','Spinner','Zum Gebrauch im Kreissaal oder im Garten',NULL),
(2,8,'Bosch','RRRRRAAAAA','Sticht wie Gangster in London',NULL),
(2,9,'IcedOut','Elastigirl','Zum Posen und zum Arbeiten geeignet',NULL),
(2,10,'Smooth','EdgePetter','Macht selbst die bösesten Kanten zart',NULL),
(3,1,'Quack','Whack','Anders als der Name sagt, nicht whack ;)',NULL),
(4,9,'Flexinator','XY Premium','Made By Dr.Doofenschmirtz',NULL);
/*!40000 ALTER TABLE `user_has_tool` ENABLE KEYS */;
UNLOCK TABLES;



/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-24  8:59:50
