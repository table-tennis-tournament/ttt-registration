
-- Exportiere Struktur von Tabelle test.bezirkekreise
CREATE TABLE IF NOT EXISTS `bezirkekreise`
(
    `BeKr_ID`      int(10) unsigned NOT NULL AUTO_INCREMENT,
    `BeKr_Wert`    varchar(10)  DEFAULT NULL,
    `BeKr_Club`    varchar(100) DEFAULT NULL,
    `BeKr_Bezirk`  varchar(100) DEFAULT NULL,
    `BeKr_Kreis`   varchar(100) DEFAULT NULL,
    `BeKr_Verband` varchar(45)  DEFAULT NULL,
    PRIMARY KEY (`BeKr_ID`)
);

-- Exportiere Daten aus Tabelle test.bezirkekreise: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.cashjournal
CREATE TABLE IF NOT EXISTS `cashjournal`
(
    `Cash_ID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `Cash_Club_ID`   int(10) unsigned          DEFAULT NULL,
    `Cash_Betrag`    double                    DEFAULT NULL,
    `Cash_Date`      datetime                  DEFAULT NULL,
    `Cash_Text`      text             NOT NULL,
    `Cash_Timestamp` timestamp        NOT NULL DEFAULT current_timestamp(),
    `Cash_BezahlArt` int(10) unsigned          DEFAULT NULL,
    PRIMARY KEY (`Cash_ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.cashjournal: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.club
CREATE TABLE IF NOT EXISTS `club`
(
    `Club_ID`             int(10)   NOT NULL AUTO_INCREMENT,
    `Club_Name`           varchar(255)       DEFAULT NULL,
    `Club_Verband`        varchar(45)        DEFAULT NULL,
    `Club_ShortName`      varchar(45)        DEFAULT NULL,
    `Club_AdresseName`    varchar(100)       DEFAULT NULL,
    `Club_AdresseStrasse` varchar(100)       DEFAULT NULL,
    `Club_AdresseOrt`     varchar(100)       DEFAULT NULL,
    `Club_Email`          varchar(100)       DEFAULT NULL,
    `Club_ClickTTID`      int(11)            DEFAULT NULL,
    `Club_Nr`             varchar(50)        DEFAULT NULL,
    `Club_Timestamp`      timestamp NOT NULL DEFAULT current_timestamp(),
    `Club_Bezirk`         varchar(50)        DEFAULT NULL,
    `Club_Kreis`          varchar(50)        DEFAULT NULL,
    `Club_Region`         varchar(20)        DEFAULT NULL,
    PRIMARY KEY (`Club_ID`),
    KEY `Club_Name` (`Club_Name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 95
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.club: ~94 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.doubles
CREATE TABLE IF NOT EXISTS `doubles`
(
    `Doub_ID`              int(10) unsigned NOT NULL AUTO_INCREMENT,
    `Doub_Play1_ID`        int(10) unsigned NOT NULL,
    `Doub_Play2_ID`        int(10) unsigned NOT NULL,
    `Doub_Type_ID`         int(10) unsigned NOT NULL,
    `Doub_Kind`            int(10) unsigned NOT NULL,
    `Doub_Timestamp`       timestamp        NOT NULL DEFAULT current_timestamp(),
    `Doub_Paid`            int(10) unsigned NOT NULL,
    `Doub_Play1_Name`      varchar(45)      NOT NULL,
    `Doub_Play2_Name`      varchar(45)      NOT NULL,
    `Doub_Play1_FirstName` varchar(45)      NOT NULL,
    `Doub_Play2_FirstName` varchar(45)      NOT NULL,
    `Doub_Seed`            int(10)                   DEFAULT NULL,
    `Doub_ExternalID`      varchar(45)               DEFAULT NULL,
    `Doub_StartNr`         int(10) unsigned          DEFAULT NULL,
    PRIMARY KEY (`Doub_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.doubles: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.grouprounds
CREATE TABLE IF NOT EXISTS `grouprounds`
(
    `GrRo_ID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `GrRo_GroupSize` int(11)                   DEFAULT NULL,
    `GrRo_ReSort`    int(11)                   DEFAULT 0,
    `GrRo_timestamp` timestamp        NOT NULL DEFAULT current_timestamp(),
    `GrRo_Rounds`    blob                      DEFAULT NULL,
    PRIMARY KEY (`GrRo_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.grouprounds: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.groupschedule
CREATE TABLE IF NOT EXISTS `groupschedule`
(
    `gsch_ID`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `gsch_GroupSize`   int(11)                   DEFAULT NULL,
    `gsch_RoundNumber` int(11)                   DEFAULT NULL,
    `gsch_timestamp`   timestamp        NOT NULL DEFAULT current_timestamp(),
    `gsch_MatchNr`     int(11)                   DEFAULT NULL,
    PRIMARY KEY (`gsch_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.groupschedule: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.handicap
CREATE TABLE IF NOT EXISTS `handicap`
(
    `hand_ID`             int(10) unsigned NOT NULL AUTO_INCREMENT,
    `hand_QTTRdifference` int(11)                   DEFAULT NULL,
    `hand_timestamp`      timestamp        NOT NULL DEFAULT current_timestamp(),
    `hand_value`          int(11)                   DEFAULT 0,
    PRIMARY KEY (`hand_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.handicap: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.kind
CREATE TABLE IF NOT EXISTS `kind`
(
    `Kind_ID`        int(10)   NOT NULL AUTO_INCREMENT,
    `Kind_Name`      varchar(255)       DEFAULT NULL,
    `Kind_Timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`Kind_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.kind: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.lastchange
CREATE TABLE IF NOT EXISTS `lastchange`
(
    `last_ID`        int(11)   NOT NULL AUTO_INCREMENT,
    `last_TableName` varchar(50)    DEFAULT NULL,
    `last_TableID`   int(11)        DEFAULT NULL,
    `last_TimeStamp` timestamp NULL DEFAULT NULL,
    PRIMARY KEY (`last_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.lastchange: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.masterdataplayer
CREATE TABLE IF NOT EXISTS `masterdataplayer`
(
    `VereinNummer`     int(6) unsigned NOT NULL DEFAULT 0,
    `NuNummer`         varchar(10)     NOT NULL DEFAULT '',
    `Timestamp`        timestamp       NOT NULL DEFAULT current_timestamp(),
    `VereinName`       varchar(100)             DEFAULT NULL,
    `Vorname`          blob            NOT NULL,
    `Nachname`         blob            NOT NULL,
    `Geburtsdatum`     datetime        NOT NULL,
    `Verband`          varchar(45)     NOT NULL,
    `Bezirk`           varchar(45)              DEFAULT NULL,
    `Kreis`            varchar(45)              DEFAULT NULL,
    `TTRWert`          int(10) unsigned         DEFAULT NULL,
    `TTRPosition`      int(10) unsigned         DEFAULT NULL,
    `BilanzwertGesamt` double                   DEFAULT NULL,
    `Spielklasse`      varchar(45)              DEFAULT NULL,
    `Geschlecht`       varchar(1)               DEFAULT NULL,
    `UpdateDatum`      datetime                 DEFAULT NULL,
    `BilanzwertStr`    varchar(45)              DEFAULT NULL,
    PRIMARY KEY (`NuNummer`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.masterdataplayer: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.matches
CREATE TABLE IF NOT EXISTS `matches`
(
    `Matc_Play1_ID`        int(10)            DEFAULT NULL,
    `Matc_Play2_ID`        int(10)            DEFAULT NULL,
    `Matc_IsPlaying`       int(10)            DEFAULT NULL,
    `Matc_Winner_ID`       int(10)            DEFAULT NULL,
    `Matc_Tabl_ID`         int(10)            DEFAULT NULL,
    `Matc_Tabl2_ID`        int(10)            DEFAULT NULL,
    `Matc_PlannedTable_ID` int(10)            DEFAULT NULL,
    `Matc_PlannedPosition` int(10)            DEFAULT NULL,
    `Matc_StartTime`       datetime           DEFAULT NULL,
    `Matc_RoundNumber`     int(10)            DEFAULT NULL,
    `Matc_ResultRaw`       varchar(600)       DEFAULT NULL,
    `Matc_Grou_ID`         int(10)            DEFAULT NULL,
    `Matc_MaTy_ID`         int(10)            DEFAULT NULL,
    `Matc_ID`              int(10)   NOT NULL AUTO_INCREMENT,
    `Matc_Type_ID`         int(10)            DEFAULT NULL,
    `Matc_Result`          varchar(255)       DEFAULT NULL,
    `Matc_Played`          int(10)            DEFAULT NULL,
    `Matc_Printed`         int(11)   NOT NULL DEFAULT 0,
    `Matc_Uebernommen`     int(10)   NOT NULL DEFAULT 0,
    `Matc_Waitinglist`     int(10)   NOT NULL DEFAULT 0,
    `Matc_kampflos`        int(10)   NOT NULL DEFAULT 0,
    `Matc_Hidden`          int(10)   NOT NULL DEFAULT 0,
    `Matc_Nr`              int(10)   NOT NULL DEFAULT 0,
    `Matc_Parent_ID`       int(10)            DEFAULT NULL,
    `Matc_TeSM_ID`         int(10)            DEFAULT NULL,
    `Matc_ResultTeam1`     int(10)            DEFAULT NULL,
    `Matc_ResultTeam2`     int(10)            DEFAULT NULL,
    `Matc_Balls1`          int(10)            DEFAULT 0,
    `Matc_Balls2`          int(10)            DEFAULT 0,
    `Matc_Sets1`           int(10)            DEFAULT 0,
    `Matc_Sets2`           int(10)            DEFAULT 0,
    `Matc_Code`            varchar(10)        DEFAULT NULL,
    `Matc_Schiri_Play_ID`  int(10)            DEFAULT 0,
    `Matc_Timestamp`       timestamp NOT NULL DEFAULT current_timestamp(),
    `Matc_Control`         int(10)            DEFAULT 0,
    `Matc_TeSy_ID`         int(10)            DEFAULT 0,
    `Matc_PlayedTable_ID`  int(10)            DEFAULT 0,
    PRIMARY KEY (`Matc_ID`),
    KEY `Matc_Group_ID` (`Matc_Grou_ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 272
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.matches: ~271 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.matchtable
CREATE TABLE IF NOT EXISTS `matchtable`
(
    `id`        char(50)  NOT NULL,
    `tableId`   int(10)   NOT NULL,
    `matchId`   int(10)   NOT NULL,
    `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.matchtable: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.matchtype
CREATE TABLE IF NOT EXISTS `matchtype`
(
    `MaTy_ID`        int(10)   NOT NULL AUTO_INCREMENT,
    `MaTy_Name`      varchar(255)       DEFAULT NULL,
    `MaTy_Timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
    `MaTy_Player`    int(10)            DEFAULT NULL,
    PRIMARY KEY (`MaTy_ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 15
  DEFAULT CHARSET = latin1;

-- Exportiere Daten aus Tabelle test.matchtype: ~14 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.platzierungen
CREATE TABLE IF NOT EXISTS `platzierungen`
(
    `plat_id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `plat_position`    int(10) unsigned NOT NULL,
    `plat_positionUrk` int(10) unsigned NOT NULL,
    `plat_grou_ID`     int(10) unsigned          DEFAULT NULL,
    `plat_Round`       int(10) unsigned          DEFAULT NULL,
    `plat_type_id`     int(10) unsigned NOT NULL,
    `plat_play1_id`    int(10) unsigned          DEFAULT NULL,
    `plat_play2_id`    int(10) unsigned          DEFAULT NULL,
    `Plat_timestamp`   timestamp        NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`plat_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.platzierungen: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.player
CREATE TABLE IF NOT EXISTS `player`
(
    `Play_ID`             int(10)   NOT NULL AUTO_INCREMENT,
    `Play_FirstName`      varchar(255)       DEFAULT NULL,
    `Play_LastName`       varchar(255)       DEFAULT NULL,
    `Play_Type_ID`        int(10)            DEFAULT NULL,
    `Play_BirthDate`      datetime           DEFAULT NULL,
    `Play_Club_ID`        int(10)            DEFAULT NULL,
    `Play_Paid`           int(10)            DEFAULT NULL,
    `Play_seed`           int(10)            DEFAULT NULL,
    `Play_FirstNameShort` varchar(45)        DEFAULT NULL,
    `Play_ExternalID`     int(10) unsigned   DEFAULT NULL,
    `Play_Quote`          double             DEFAULT NULL,
    `Play_Foreigner`      int(10)            DEFAULT 0,
    `Play_Nationality`    varchar(10)        DEFAULT NULL,
    `Play_TTR`            double             DEFAULT NULL,
    `Play_TTRPos`         double             DEFAULT NULL,
    `Play_TTRStr`         varchar(45)        DEFAULT NULL,
    `Play_ClickTTID`      varchar(50)        DEFAULT NULL,
    `Play_StartNr`        int(10) unsigned   DEFAULT NULL,
    `Play_Sex`            varchar(19)        DEFAULT NULL,
    `Play_Email`          varchar(70)        DEFAULT NULL,
    `Play_PLZ`            varchar(10)        DEFAULT NULL,
    `Play_Location`       varchar(50)        DEFAULT NULL,
    `Play_Street`         varchar(50)        DEFAULT NULL,
    `Play_TelNr`          varchar(30)        DEFAULT NULL,
    `Play_Timestamp`      timestamp NOT NULL DEFAULT current_timestamp(),
    `Play_LicenseNr`      varchar(50)        DEFAULT NULL,
    PRIMARY KEY (`Play_ID`),
    KEY `Play_Paid` (`Play_Paid`),
    KEY `Play_Type_ID` (`Play_Type_ID`),
    KEY `Play_Club_ID` (`Play_Club_ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 287
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.player: ~233 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.playerpergroup
CREATE TABLE IF NOT EXISTS `playerpergroup`
(
    `PPGr_ID`            int(10)   NOT NULL AUTO_INCREMENT,
    `PPGr_Play_ID`       int(10)            DEFAULT NULL,
    `PPGr_Grou_ID`       int(10)            DEFAULT NULL,
    `PPGr_Type`          int(10)            DEFAULT NULL,
    `PPGr_Position`      int(10)            DEFAULT NULL,
    `PPGr_StartPosition` int(10)            DEFAULT NULL,
    `PPGr_Timestamp`     timestamp NOT NULL DEFAULT current_timestamp(),
    `PPGr_Name`          varchar(50)        DEFAULT NULL,
    `PPGr_GamesW`        int(10) unsigned   DEFAULT NULL,
    `PPGr_GamesL`        int(10) unsigned   DEFAULT NULL,
    `PPGr_SetW`          int(10) unsigned   DEFAULT NULL,
    `PPGr_SetL`          int(10) unsigned   DEFAULT NULL,
    `PPGr_PointsW`       int(10) unsigned   DEFAULT NULL,
    `PPGr_PointsL`       int(10) unsigned   DEFAULT NULL,
    `PPGr_BallDiff`      int(10)            DEFAULT NULL,
    `PPGr_ClubName`      varchar(100)       DEFAULT NULL,
    `PPGr_Games`         varchar(45)        DEFAULT NULL,
    `PPGr_Sets`          varchar(45)        DEFAULT NULL,
    `PPGr_Points`        varchar(45)        DEFAULT NULL,
    `PPGR_Checked`       int(10) unsigned   DEFAULT NULL,
    `PPGr_SetDiff`       int(11)            DEFAULT NULL,
    `PPGr_SetzPos`       int(11)            DEFAULT NULL,
    `PPGr_VsPos1`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos2`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos3`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos4`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos5`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos6`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos7`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos8`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos9`        varchar(7)         DEFAULT NULL,
    `PPGr_VsPos10`       varchar(7)         DEFAULT NULL,
    `PPGr_VsPos11`       varchar(7)         DEFAULT NULL,
    `PPGr_VsPos12`       varchar(7)         DEFAULT NULL,
    `PPGr_VsPos13`       varchar(7)         DEFAULT NULL,
    `PPGr_VsPos14`       varchar(7)         DEFAULT NULL,
    `PPGr_VsPos15`       varchar(7)         DEFAULT NULL,
    `PPGr_VsPos16`       varchar(7)         DEFAULT NULL,
    `PPGr_TTR`           int(10)            DEFAULT 0,
    `PPGr_inactive`      int(10)            DEFAULT 0,
    `PPGr_Buchholz`      int(10)            DEFAULT 0,
    `PPGr_FeinBuchholz`  int(10)            DEFAULT 0,
    `PPGr_ForcePos`      int(10) unsigned   DEFAULT NULL,
    `PPGr_Kreis`         varchar(50)        DEFAULT NULL,
    `PPGr_RegionName`    varchar(50)        DEFAULT NULL,
    `PPGr_Verband`       varchar(50)        DEFAULT NULL,
    PRIMARY KEY (`PPGr_ID`),
    KEY `PPGr_Grou_ID` (`PPGr_Grou_ID`),
    KEY `PPGr_Play_ID` (`PPGr_Play_ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 193
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.playerpergroup: ~192 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.playerpermatch
CREATE TABLE IF NOT EXISTS `playerpermatch`
(
    `PPMa_ID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `PPMa_Team_ID`   int(10) unsigned NOT NULL,
    `PPMa_Matc_ID`   int(10) unsigned NOT NULL,
    `PPMa_Play_ID`   int(10) unsigned NOT NULL,
    `PPMa_Position`  int(10)                   DEFAULT NULL,
    `PPMa_timestamp` timestamp        NOT NULL DEFAULT current_timestamp(),
    `PPMa_Kind`      int(10)                   DEFAULT NULL,
    PRIMARY KEY (`PPMa_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.playerpermatch: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.playerperteam
CREATE TABLE IF NOT EXISTS `playerperteam`
(
    `PPTe_ID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `PPTe_Team_ID`   int(10) unsigned NOT NULL,
    `PPTe_Play_ID`   int(10) unsigned NOT NULL,
    `PPTe_Doub_ID`   int(10) unsigned NOT NULL,
    `PPTe_timestamp` timestamp        NOT NULL DEFAULT current_timestamp(),
    `PPTe_Position`  int(10)                   DEFAULT NULL,
    PRIMARY KEY (`PPTe_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.playerperteam: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.playgroups
CREATE TABLE IF NOT EXISTS `playgroups`
(
    `Grou_ID`        int(10)   NOT NULL AUTO_INCREMENT,
    `Grou_Name`      varchar(255)       DEFAULT NULL,
    `Grou_Tour_ID`   int(10)            DEFAULT NULL,
    `Grou_Timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
    `Grou_QualPos`   varchar(255)       DEFAULT NULL,
    `Grou_Type_ID`   int(10)            DEFAULT NULL,
    `Grou_GroupType` int(10)            DEFAULT NULL,
    PRIMARY KEY (`Grou_ID`),
    KEY `Grou_Tour_ID` (`Grou_Tour_ID`),
    KEY `Grou_Type_ID` (`Grou_Type_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.playgroups: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.playsystem
CREATE TABLE IF NOT EXISTS `playsystem`
(
    `ID`        int(10)   NOT NULL AUTO_INCREMENT,
    `TypeID`    int(10)            DEFAULT NULL,
    `Timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
    `SystemID`  varchar(255)       DEFAULT NULL,
    PRIMARY KEY (`ID`),
    KEY `SystemID` (`SystemID`),
    KEY `TypeID` (`TypeID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.playsystem: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.play_evolutions
CREATE TABLE IF NOT EXISTS `play_evolutions`
(
    `id`            int(11)      NOT NULL,
    `hash`          varchar(255) NOT NULL,
    `applied_at`    timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `apply_script`  mediumtext            DEFAULT NULL,
    `revert_script` mediumtext            DEFAULT NULL,
    `state`         varchar(255)          DEFAULT NULL,
    `last_problem`  mediumtext            DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

-- Exportiere Daten aus Tabelle test.play_evolutions: ~1 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.schedule
CREATE TABLE IF NOT EXISTS `schedule`
(
    `Sche_ID`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `Sche_Date`        datetime                  DEFAULT NULL,
    `Sche_Tabl_ID`     int(10) unsigned NOT NULL,
    `Sche_Matc_ID`     int(10) unsigned          DEFAULT NULL,
    `Sche_Grou_ID`     int(10) unsigned          DEFAULT NULL,
    `Sche_MatchNr`     int(10) unsigned          DEFAULT NULL,
    `Sche_Timestamp`   timestamp        NOT NULL DEFAULT current_timestamp(),
    `Sche_RoundNumber` int(10) unsigned          DEFAULT NULL,
    `Sche_type_ID`     int(10) unsigned          DEFAULT NULL,
    `Sche_Tabl_Name`   varchar(20)      NOT NULL,
    PRIMARY KEY (`Sche_ID`),
    KEY `Sche_Matc_ID` (`Sche_Matc_ID`),
    KEY `Sche_Tabl_ID` (`Sche_Tabl_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.schedule: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.settings
CREATE TABLE IF NOT EXISTS `settings`
(
    `ID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `Name`      varchar(45)      NOT NULL,
    `Timestamp` timestamp        NOT NULL DEFAULT current_timestamp(),
    `Value`     varchar(150)     NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.settings: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.tables
CREATE TABLE IF NOT EXISTS `tables`
(
    `Tabl_ID`        int(10)   NOT NULL AUTO_INCREMENT,
    `Tabl_Name`      varchar(255)       DEFAULT NULL,
    `Tabl_Group`     varchar(255)       DEFAULT NULL,
    `Tabl_Left`      int(10)            DEFAULT NULL,
    `Tabl_Timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
    `Tabl_Top`       int(10)            DEFAULT NULL,
    `Tabl_Matc_ID`   int(10)            DEFAULT NULL,
    `Tabl_Tour_ID`   int(10)            DEFAULT NULL,
    PRIMARY KEY (`Tabl_ID`),
    KEY `Tabl_Matc_ID` (`Tabl_Matc_ID`),
    KEY `Tabl_Tour_ID` (`Tabl_Tour_ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 82
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.tables: ~81 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.team
CREATE TABLE IF NOT EXISTS `team`
(
    `team_ID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `team_Name`      varchar(100)     NOT NULL,
    `team_club_id`   int(10) unsigned NOT NULL,
    `team_type_id`   int(10) unsigned NOT NULL,
    `team_paid`      int(10) unsigned NOT NULL,
    `team_seed`      int(10) unsigned NOT NULL,
    `team_timestamp` timestamp        NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`team_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.team: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.teamsystem
CREATE TABLE IF NOT EXISTS `teamsystem`
(
    `TeSy_ID`           int(10) unsigned NOT NULL AUTO_INCREMENT,
    `TeSy_Name`         varchar(100)     NOT NULL,
    `TeSy_PlayerCount`  int(10)                   DEFAULT NULL,
    `TeSy_PlayerCountB` int(10)                   DEFAULT NULL,
    `TeSy_DoubleCount`  int(10)                   DEFAULT NULL,
    `TeSy_timestamp`    timestamp        NOT NULL DEFAULT current_timestamp(),
    `TeSy_WinPoint`     int(10)                   DEFAULT NULL,
    PRIMARY KEY (`TeSy_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.teamsystem: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.teamsystemmatches
CREATE TABLE IF NOT EXISTS `teamsystemmatches`
(
    `TeSM_ID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `TeSM_TeSy_ID`   int(10) unsigned NOT NULL,
    `TeSM_Round`     int(10)                   DEFAULT NULL,
    `TeSM_Play1`     varchar(10)      NOT NULL,
    `TeSM_Play2`     varchar(10)      NOT NULL,
    `TeSM_Kind`      int(10)                   DEFAULT NULL,
    `TeSM_timestamp` timestamp        NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`TeSM_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.teamsystemmatches: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.tournament
CREATE TABLE IF NOT EXISTS `tournament`
(
    `Tour_ID`                int(10)   NOT NULL AUTO_INCREMENT,
    `Tour_Name`              varchar(255)       DEFAULT NULL,
    `Tour_Date`              datetime           DEFAULT NULL,
    `Tour_TableCount`        int(10)            DEFAULT NULL,
    `Tour_PrintSchiri`       int(10)            DEFAULT NULL,
    `Tour_UseThirdPlace`     int(10)            DEFAULT NULL,
    `Tour_DisplayTables`     int(10)            DEFAULT NULL,
    `Tour_DisplayPos`        varchar(45)        DEFAULT NULL,
    `Tour_PrintSchiriFormat` int(10)            DEFAULT NULL,
    `Tour_Timestamp`         timestamp NOT NULL DEFAULT current_timestamp(),
    `Tour_Verband`           int(10) unsigned   DEFAULT NULL,
    PRIMARY KEY (`Tour_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.tournament: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.type
CREATE TABLE IF NOT EXISTS `type`
(
    `Type_ID`                 int(10)   NOT NULL AUTO_INCREMENT,
    `Type_Name`               varchar(255)       DEFAULT NULL,
    `Type_Kind`               int(10)            DEFAULT NULL,
    `Type_Syst_ID`            int(10)            DEFAULT NULL,
    `Type_Clas_id`            int(10)            DEFAULT NULL,
    `Type_KOSize`             int(10) unsigned   DEFAULT NULL,
    `Type_TrostrundeSize`     int(10) unsigned   DEFAULT 0,
    `Type_UrkundePrinted`     int(10) unsigned   DEFAULT NULL,
    `Type_StartGebuehr`       double             DEFAULT 0,
    `Type_NachmeldeGebuehr`   double             DEFAULT 0,
    `Type_PrintName`          varchar(255)       DEFAULT NULL,
    `Type_clickTTCompetition` blob               DEFAULT NULL,
    `Type_Parenttype_ID`      int(10)            DEFAULT NULL,
    `Type_Active`             int(10)            DEFAULT 1,
    `Type_Blocked`            int(10)            DEFAULT 0,
    `Type_TeSy_ID`            int(10)            DEFAULT NULL,
    `Type_groups`             int(10)            DEFAULT 0,
    `Type_System`             int(10)            DEFAULT 0,
    `Type_nextmatches`        int(10)            DEFAULT 0,
    `Type_Sex`                varchar(10)        DEFAULT NULL,
    `Type_AgeFrom`            int(10)            DEFAULT 0,
    `Type_AgeTo`              int(10)            DEFAULT 0,
    `Type_YearFrom`           int(10)            DEFAULT 0,
    `Type_YearTo`             int(10)            DEFAULT 0,
    `Type_TTRFrom`            int(10)            DEFAULT 0,
    `Type_TTRTo`              int(10)            DEFAULT 0,
    `Type_TTRRemarks`         varchar(200)       DEFAULT NULL,
    `Type_StartTime`          datetime           DEFAULT NULL,
    `Type_Trostrunde`         int(10)            DEFAULT 0,
    `Type_DritterPlatz`       int(10)            DEFAULT 0,
    `Type_KomplettKO`         int(10)            DEFAULT 0,
    `type_Aufstellung`        int(10)            DEFAULT 0,
    `type_GewinnSaetze`       int(10)            DEFAULT 0,
    `type_Vorgabe`            int(10)            DEFAULT 0,
    `type_WinPoints`          int(10)            DEFAULT 0,
    `Type_timestamp`          timestamp NOT NULL DEFAULT current_timestamp(),
    `type_alleUrkunden`       int(10)            DEFAULT 0,
    `type_KORounds`           int(10)            DEFAULT 0,
    PRIMARY KEY (`Type_ID`),
    KEY `Type_Clas_id` (`Type_Clas_id`),
    KEY `Type_System_ID` (`Type_Syst_ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.type: ~10 rows (ungefähr)

-- Exportiere Struktur von Tabelle test.typeperplayer
CREATE TABLE IF NOT EXISTS `typeperplayer`
(
    `typl_id`         int(10) unsigned NOT NULL AUTO_INCREMENT,
    `typl_play_id`    int(10) unsigned NOT NULL,
    `typl_type_id`    int(10) unsigned NOT NULL,
    `typl_seed`       int(10) unsigned NOT NULL,
    `typl_WaitList`   int(10)                   DEFAULT 0,
    `typl_paid`       int(10) unsigned NOT NULL,
    `typl_timestamp`  timestamp        NOT NULL DEFAULT current_timestamp(),
    `typl_Cash_ID`    int(10) unsigned NOT NULL DEFAULT 0,
    `Typl_ExternalID` varchar(50)               DEFAULT NULL,
    PRIMARY KEY (`typl_id`),
    KEY `index_typl_paid` (`typl_paid`, `typl_play_id`, `typl_Cash_ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 399
  DEFAULT CHARSET = utf8mb3;

-- Exportiere Daten aus Tabelle test.typeperplayer: ~398 rows (ungefähr)



