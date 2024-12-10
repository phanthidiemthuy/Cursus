-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: fsoft
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`admin_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,1),(2,2),(3,3);
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_action`
--

DROP TABLE IF EXISTS `admin_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_action` (
  `admin_action_id` int NOT NULL,
  `action_type` varchar(255) NOT NULL,
  `target_id` int NOT NULL,
  `action_date` date NOT NULL,
  `admin_id` int NOT NULL,
  PRIMARY KEY (`admin_action_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `admin_action_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_action`
--

LOCK TABLES `admin_action` WRITE;
/*!40000 ALTER TABLE `admin_action` DISABLE KEYS */;
INSERT INTO `admin_action` VALUES (1,'Ban User',3,'2023-04-01',1),(2,'Approve Course',2,'2023-05-01',1),(3,'Update Role',1,'2023-06-01',1);
/*!40000 ALTER TABLE `admin_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL,
  `category_name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Programming','Courses related to programming'),(2,'Design','Courses related to design'),(3,'Marketing','Courses related to marketing');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `course_id` int NOT NULL,
  `course_title` varchar(255) NOT NULL,
  `short_description` varchar(1000) NOT NULL,
  `description` json NOT NULL,
  `requirements` varchar(1000) NOT NULL,
  `is_blocked` int NOT NULL,
  `course_level` varchar(255) NOT NULL,
  `thumbnail` varchar(255) NOT NULL,
  `regular_price` float NOT NULL,
  `discount_price` float NOT NULL,
  `category_id` int NOT NULL,
  `instructor_id` int NOT NULL,
  PRIMARY KEY (`course_id`),
  KEY `category_id` (`category_id`),
  KEY `instructor_id` (`instructor_id`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`),
  CONSTRAINT `course_ibfk_2` FOREIGN KEY (`instructor_id`) REFERENCES `instructor` (`instructor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'Learn Web Development','A comprehensive course on web development','{\"details\": \"Full stack development\"}','Basic HTML knowledge',0,'Beginner','thumb1.jpg',200,150,1,1),(2,'Graphic Design Basics','An introductory course on graphic design','{\"details\": \"Photoshop, Illustrator\"}','No prior knowledge required',0,'Beginner','thumb2.jpg',150,100,2,2),(3,'Digital Marketing 101','Learn the basics of digital marketing','{\"details\": \"SEO, SEM\"}','Basic marketing knowledge',0,'Beginner','thumb3.jpg',180,130,3,3);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment_course`
--

DROP TABLE IF EXISTS `enrollment_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment_course` (
  `enrollment_course_id` int NOT NULL,
  `enrollment_date` date NOT NULL,
  `student_id` int NOT NULL,
  PRIMARY KEY (`enrollment_course_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `enrollment_course_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment_course`
--

LOCK TABLES `enrollment_course` WRITE;
/*!40000 ALTER TABLE `enrollment_course` DISABLE KEYS */;
INSERT INTO `enrollment_course` VALUES (1,'2023-01-01',1),(2,'2023-02-01',2),(3,'2023-03-01',3);
/*!40000 ALTER TABLE `enrollment_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment_course_details`
--

DROP TABLE IF EXISTS `enrollment_course_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment_course_details` (
  `ID` int NOT NULL,
  `progress` int NOT NULL,
  `review_contents` varchar(1000) NOT NULL,
  `report_contents` varchar(1000) NOT NULL,
  `enrollment_course_id` int NOT NULL,
  `course_id` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `enrollment_course_id` (`enrollment_course_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `enrollment_course_details_ibfk_1` FOREIGN KEY (`enrollment_course_id`) REFERENCES `enrollment_course` (`enrollment_course_id`),
  CONSTRAINT `enrollment_course_details_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment_course_details`
--

LOCK TABLES `enrollment_course_details` WRITE;
/*!40000 ALTER TABLE `enrollment_course_details` DISABLE KEYS */;
INSERT INTO `enrollment_course_details` VALUES (1,50,'Great course','No issues',1,1),(2,75,'Very informative','Some minor issues',2,2),(3,100,'Excellent course','Perfect',3,3);
/*!40000 ALTER TABLE `enrollment_course_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instructor`
--

DROP TABLE IF EXISTS `instructor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instructor` (
  `instructor_id` int NOT NULL,
  `experience` varchar(1000) NOT NULL,
  `salary` decimal(10,0) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`instructor_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `instructor_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instructor`
--

LOCK TABLES `instructor` WRITE;
/*!40000 ALTER TABLE `instructor` DISABLE KEYS */;
INSERT INTO `instructor` VALUES (1,'5 years in web development',50000,2),(2,'3 years in graphic design',40000,2),(3,'2 years in digital marketing',35000,2);
/*!40000 ALTER TABLE `instructor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payout`
--

DROP TABLE IF EXISTS `payout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payout` (
  `payout_id` int NOT NULL,
  `amount` decimal(10,0) NOT NULL,
  `payout_date` date NOT NULL,
  `status` varchar(255) NOT NULL,
  `instructor_id` int NOT NULL,
  PRIMARY KEY (`payout_id`),
  KEY `instructor_id` (`instructor_id`),
  CONSTRAINT `payout_ibfk_1` FOREIGN KEY (`instructor_id`) REFERENCES `instructor` (`instructor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payout`
--

LOCK TABLES `payout` WRITE;
/*!40000 ALTER TABLE `payout` DISABLE KEYS */;
INSERT INTO `payout` VALUES (1,1000,'2023-07-01','Completed',1),(2,800,'2023-08-01','Pending',2),(3,600,'2023-09-01','Failed',3);
/*!40000 ALTER TABLE `payout` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `role_id` int NOT NULL,
  `role_name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Admin','Administrator with full access'),(2,'Instructor','Instructor with course creation rights'),(3,'Student','Student with course access rights');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `student_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`student_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (3,1),(2,2),(1,3);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subcategory`
--

DROP TABLE IF EXISTS `subcategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subcategory` (
  `subcategory_id` int NOT NULL,
  `subcategory_name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`subcategory_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `subcategory_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subcategory`
--

LOCK TABLES `subcategory` WRITE;
/*!40000 ALTER TABLE `subcategory` DISABLE KEYS */;
INSERT INTO `subcategory` VALUES (1,'Web Development','Courses on web development',1),(2,'Graphic Design','Courses on graphic design',2),(3,'Digital Marketing','Courses on digital marketing',3);
/*!40000 ALTER TABLE `subcategory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `transaction_id` int NOT NULL,
  `amount` decimal(10,0) NOT NULL,
  `transaction_date` date NOT NULL,
  `status` varchar(255) NOT NULL,
  `student_id` int NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,150,'2023-10-01','Completed',1),(2,100,'2023-11-01','Pending',2),(3,130,'2023-12-01','Failed',3);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_details`
--

DROP TABLE IF EXISTS `transaction_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_details` (
  `transaction_id` int NOT NULL,
  `course_id` int NOT NULL,
  `unit_price` float NOT NULL,
  PRIMARY KEY (`transaction_id`,`course_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `transaction_details_ibfk_1` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`transaction_id`),
  CONSTRAINT `transaction_details_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_details`
--

LOCK TABLES `transaction_details` WRITE;
/*!40000 ALTER TABLE `transaction_details` DISABLE KEYS */;
INSERT INTO `transaction_details` VALUES (1,1,50),(2,2,75),(3,3,100);
/*!40000 ALTER TABLE `transaction_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `registration_date` date NOT NULL,
  `is_blocked` int NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin@example.com','password123','2023-01-01',0,'Admin User',1),(2,'instructor@example.com','password123','2023-02-01',0,'Instructor User',2),(3,'student@example.com','password123','2023-03-01',0,'Student User',3);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-26 12:34:39
