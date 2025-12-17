-- Datenbank verwenden
USE hobbyhandwerker;

-- ----------------------------
-- Table structure for `tool`
-- ----------------------------

DROP TABLE IF EXISTS `tool`;
CREATE TABLE `tool` (
  `ID_Tool` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(40) NOT NULL,
  PRIMARY KEY (`ID_Tool`),
  UNIQUE KEY `ID_Tool_UNIQUE` (`ID_Tool`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- Insert data into `tool`
INSERT INTO `tool` (`ID_Tool`, `Name`) VALUES
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

-- ----------------------------
-- Table structure for `role`
-- ----------------------------

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `ID_Role` INT(11) NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`ID_Role`),
  UNIQUE KEY `ID_Role_UNIQUE` (`ID_Role`),
  UNIQUE KEY `Name_UNIQUE` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

LOCK TABLES `role` WRITE;
INSERT INTO `role` (`ID_Role`, `Name`) VALUES
(1,'visitor'),
(2,'user'),
(3,'admin');
UNLOCK TABLES;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------

DROP TABLE IF EXISTS `user`;
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
  CONSTRAINT `REL_Role_User`
      FOREIGN KEY (`FS_Role`)
      REFERENCES `role` (`ID_Role`)
      ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- Insert dummy users
-- PWs dummy2: pass2, dummy3: pass3 etc.
INSERT INTO `user` (`ID_User`, `Email`, `Username`, `Password`, `FS_Role`) VALUES
(1, 'visitor', 'visitor', '5f14f9e6d80f802a65269804f2552ef9889f2c7ccec5067214e58a1e48e0b3ff', 1),
(2, 'dummy2@mail.de', 'dummy2', '1ba3d16e9881959f8c9a9762854f72c6e6321cdd44358a10a4e939033117eab9', 2),
(3, 'dummy3@mail.de', 'dummy3', '3acb59306ef6e660cf832d1d34c4fba3d88d616f0bb5c2a9e0f82d18ef6fc167', 2),
(4, 'dummy4@mail.de', 'dummy4', 'a417b5dc3d06d15d91c6687e27fc1705ebc56b3b2d813abe03066e5643fe4e74', 2),
(5, 'dummy5@mail.de', 'dummy5', '0eeac8171768d0cdef3a20fee6db4362d019c91e10662a6b55186336e1a42778', 2);

-- ----------------------------
-- Table structure for `user_has_tool`
-- ----------------------------

DROP TABLE IF EXISTS `user_has_tool`;
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
  CONSTRAINT `REL_Tool_User_has_Tool1`
      FOREIGN KEY (`FS_Tool`)
      REFERENCES `tool` (`ID_Tool`)
      ON UPDATE NO ACTION,
  CONSTRAINT `REL_User_User_has_Tool`
      FOREIGN KEY (`FS_User`)
      REFERENCES `user` (`ID_User`)
      ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- Insert data into `user_has_tool`
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
