INSERT INTO `club` (`Club_ID`, `Club_Name`, `Club_Verband`, `Club_ShortName`, `Club_AdresseName`, `Club_AdresseStrasse`,
                    `Club_AdresseOrt`, `Club_Email`, `Club_ClickTTID`, `Club_Nr`, `Club_Timestamp`, `Club_Bezirk`,
                    `Club_Kreis`, `Club_Region`)
VALUES (1, 'TTV Erdmannhausen', 'TTVWH', NULL, NULL, NULL, NULL, NULL, NULL, '08013', '2019-01-03 18:21:42',
        'Ludwigsburg', NULL, '5');


INSERT INTO `player` (`Play_ID`, `Play_FirstName`, `Play_LastName`, `Play_Type_ID`, `Play_BirthDate`, `Play_Club_ID`,
                      `Play_Paid`, `Play_seed`, `Play_FirstNameShort`, `Play_ExternalID`, `Play_Quote`,
                      `Play_Foreigner`, `Play_Nationality`, `Play_TTR`, `Play_TTRPos`, `Play_TTRStr`, `Play_ClickTTID`,
                      `Play_StartNr`, `Play_Sex`, `Play_Email`, `Play_PLZ`, `Play_Location`, `Play_Street`,
                      `Play_TelNr`, `Play_Timestamp`, `Play_LicenseNr`)
VALUES (54, 'David', 'Adkins', NULL, '2002-01-01 00:00:00', 1, 0, NULL, 'Da', 0, NULL, 0, 'DE', 1289, NULL, NULL,
        'NU1523586', NULL, 'M', NULL, NULL, NULL, NULL, NULL, '2019-01-03 18:21:45', '08013229|'),


INSERT INTO `type` (`Type_ID`, `Type_Name`, `Type_Kind`, `Type_Syst_ID`, `Type_Clas_id`, `Type_KOSize`,
                    `Type_TrostrundeSize`, `Type_UrkundePrinted`, `Type_StartGebuehr`, `Type_NachmeldeGebuehr`,
                    `Type_PrintName`, `Type_clickTTCompetition`, `Type_Parenttype_ID`, `Type_Active`, `Type_Blocked`,
                    `Type_TeSy_ID`, `Type_groups`, `Type_System`, `Type_nextmatches`, `Type_Sex`, `Type_AgeFrom`,
                    `Type_AgeTo`, `Type_YearFrom`, `Type_YearTo`, `Type_TTRFrom`, `Type_TTRTo`, `Type_TTRRemarks`,
                    `Type_StartTime`, `Type_Trostrunde`, `Type_DritterPlatz`, `Type_KomplettKO`, `type_Aufstellung`,
                    `type_GewinnSaetze`, `type_Vorgabe`, `type_WinPoints`, `Type_timestamp`, `type_alleUrkunden`,
                    `type_KORounds`)
VALUES (1, 'Herren D', 1, 0, NULL, NULL, 0, NULL, 9, 0, 'Herren D 0-1500 Einzel',
        '',
        NULL, 1, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 0, 1500, NULL, '2019-01-06 08:30:00', 0, 0, 0, 0, 3, 0, 11,
        '2019-01-03 18:21:44', 0, 0),
       (2, 'Herren D', 2, 3, NULL, NULL, 0, NULL, 0, 0, 'Herren D 0-1500 Doppel',
        '',
        NULL, 1, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 0, 1500, NULL, '2019-01-06 08:30:00', 0, 0, 0, 0, 3, 0, 11,
        '2019-01-03 18:21:44', 0, 0),
       (3, 'Herren B', 1, 0, NULL, 0, 0, 0, 9, 0, 'Herren B 1701-1900 Einzel',
        '',
        NULL, 1, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 1701, 1900, NULL, '2019-01-06 09:30:00', 0, 0, 0, 0, 3, 0,
        11, '2019-01-03 18:21:44', 0, 0),
       (4, 'Herren B', 2, 3, NULL, NULL, 0, NULL, 0, 0, 'Herren B 1701-1900 Doppel',
        '',
        NULL, 1, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 1701, 1900, NULL, '2019-01-06 09:30:00', 0, 0, 0, 0, 3, 0,
        11, '2019-01-03 18:21:44', 0, 0),
       (5, 'Damen A', 1, 0, NULL, 0, 0, 0, 9, 0, 'Damen A Einzel',
        '',
        NULL, 1, 0, NULL, 0, 0, 0, 'W', 121, 11, 1898, 2008, 0, 3000, NULL, '2019-01-06 12:30:00', 0, 0, 0, 0, 3, 0, 11,
        '2019-01-03 18:21:44', 0, 0),
       (6, 'Damen A', 2, 3, NULL, NULL, 0, NULL, 0, 0, 'Damen A Doppel',
        '',
        NULL, 0, 0, NULL, 0, 0, 0, 'W', 121, 11, 1898, 2008, 0, 3000, NULL, '2019-01-06 12:30:00', 0, 0, 0, 0, 3, 0, 11,
        '2019-01-03 18:21:44', 0, 0),
       (7, 'Herren C', 1, 0, NULL, 0, 0, 0, 9, 0, 'Herren C 1501-1700 Einzel',
        '',
        NULL, 1, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 1501, 1700, NULL, '2019-01-06 14:00:00', 0, 0, 0, 0, 3, 0,
        11, '2019-01-03 18:21:44', 0, 0),
       (8, 'Herren C', 2, 3, NULL, 64, 0, 0, 0, 0, 'Herren C 1501-1700 Doppel',
        '',
        NULL, 1, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 1501, 1700, NULL, '2019-01-06 14:00:00', 0, 0, 0, 0, 3, 0,
        11, '2019-01-03 18:21:44', 0, 0),
       (9, 'Herren A', 1, 0, NULL, NULL, 0, NULL, 9, 0, 'Herren A Einzel',
        '',
        NULL, 0, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 1901, 3000, NULL, '2019-01-06 16:30:00', 0, 0, 0, 0, 3, 0,
        11, '2019-01-03 18:21:44', 0, 0),
       (10, 'Herren A', 2, 3, NULL, NULL, 0, NULL, 0, 0, 'Herren A Doppel',
        '',
        NULL, 0, 0, NULL, 0, 0, 0, 'G', 121, 11, 1898, 2008, 1901, 3000, NULL, '2019-01-06 16:30:00', 0, 0, 0, 0, 3, 0,
        11, '2019-01-03 18:21:44', 0, 0);

INSERT INTO `typeperplayer` (`typl_id`, `typl_play_id`, `typl_type_id`, `typl_seed`, `typl_WaitList`, `typl_paid`,
                             `typl_timestamp`, `typl_Cash_ID`, `Typl_ExternalID`)
VALUES (1, 54, 1, 0, 0, 0, '2019-01-03 18:21:45', 0, 'PLAYER1');


-- Insert test user (username: admin, password: password)
INSERT INTO `users` (`username`, `password`, `enabled`)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1);

INSERT INTO `authorities` (`username`, `authority`)
VALUES ('admin', 'ROLE_USER'),
       ('admin', 'ROLE_ADMIN');
