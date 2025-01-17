-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema HobbyHandwerker
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema HobbyHandwerker
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `HobbyHandwerker` DEFAULT CHARACTER SET utf8 ;
USE `HobbyHandwerker` ;

-- -----------------------------------------------------
-- Table `HobbyHandwerker`.`User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HobbyHandwerker`.`User` (
  `ID_User` INT NOT NULL AUTO_INCREMENT,
  `Email` VARCHAR(60) NULL,
  `Username` VARCHAR(30) NOT NULL,
  `Password` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`ID_User`),
  UNIQUE INDEX `ID_User_UNIQUE` (`ID_User` ASC) VISIBLE,
  UNIQUE INDEX `Email_UNIQUE` (`Email` ASC) VISIBLE)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HobbyHandwerker`.`Tool`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HobbyHandwerker`.`Tool` (
  `ID_Tool` INT NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`ID_Tool`),
  UNIQUE INDEX `ID_Tool_UNIQUE` (`ID_Tool` ASC) VISIBLE)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HobbyHandwerker`.`User_has_tool`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HobbyHandwerker`.`User_has_tool` (
  `FS_User` INT NOT NULL,
  `FS_Tool` INT NOT NULL,
  `Brand` VARCHAR(40) NULL,
  `Model` VARCHAR(40) NULL,
  `Description` MEDIUMTEXT NOT NULL,
  `Picture` BLOB NULL,
  PRIMARY KEY (`FS_User`, `FS_Tool`),
  INDEX `REL_Tool_User_has_Tool1_idx` (`FS_Tool` ASC) VISIBLE,
  INDEX `REL_User_User_has_Tool_idx` (`FS_User` ASC) VISIBLE,
  CONSTRAINT `REL_User_User_has_Tool`
  FOREIGN KEY (`FS_User`)
  REFERENCES `HobbyHandwerker`.`User` (`ID_User`)
  ON DELETE RESTRICT
  ON UPDATE NO ACTION,
  CONSTRAINT `REL_Tool_User_has_Tool1`
  FOREIGN KEY (`FS_Tool`)
  REFERENCES `HobbyHandwerker`.`Tool` (`ID_Tool`)
  ON DELETE RESTRICT
  ON UPDATE NO ACTION)
  ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
