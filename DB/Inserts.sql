Use HobbyHandwerker;

INSERT INTO User (ID_User, Email, Username, Password)
VALUES (1, 'linus.zech@zkb.ch', 'BigDaddyYoda', '123456789'),
       (2, 'pit.huwiler@zkb.ch', 'KCP-IanMcK', 'qwertzuiop'),
       (3, 'sarina.kittelmann@zkb.ch', 'Sadaki99', 'asdfghjklö'),
       (4, 'dominic.rueegger@zkb.ch', 'Miaumura', 'yxcvbnm');

INSERT INTO Tool (ID_Tool, Name)
VALUES (1, 'Hammer'),
       (2, 'Kreuzschraubenzieher'),
       (3, 'Schlitzschraubenzieher'),
       (4, 'Aale'),
       (5, 'Säge'),
       (6, 'Motorsäge'),
       (7, 'Kreissäge'),
       (8, 'Stichsäge'),
       (9, 'Flex'),
       (10, 'Winkelschleifer');

INSERT INTO User_has_tool (FS_User, FS_Tool, Brand, Model, Description, Picture)
VALUES (2, 1, 'Hammermarke', 'Hammermodell', 'Kann hämmern, guter Zustand', null),
       (2, 2, 'Superzieher', 'Kreuz', 'Wie neu', null),
       (2, 3, 'Superzieher', 'Schlitz', 'Mit Trauma versehen', null),
       (2, 4, 'AAAAAAA', 'Penetrator', 'Kann alles löchern :)', null),
       (2, 5, 'Saugh', 'UIIAI', 'Des Magiers treuester Begleiter', null),
       (2, 6, 'Saugh', 'Brrrrrrr', 'Leatherface Cosplay Prop', null),
       (2, 7, 'FidgetTools', 'Spinner', 'Zum Gebrauch im Kreissaal oder im Garten', null),
       (2, 8, 'Bosch', 'RRRRRAAAAA', 'Sticht wie Gangster in London', null),
       (2, 9, 'IcedOut', 'Elastigirl', 'Zum Posen und zum Arbeiten geeignet', null),
       (2, 10, 'Smooth', 'EdgePetter', 'Macht selbst die bösesten Kanten zart', null),
       (1, 2, 'Twister', 'Criss-Cross Master 3000', 'Ist da, wenn mal eine Schraube locker ist', null),
       (4, 9, 'Flexinator', 'XY Premium', 'Made By Dr.Doofenschmirtz', null),
       (3, 1, 'Quack', 'Whack', 'Anders als der Name sagt, nicht whack ;)', null);
