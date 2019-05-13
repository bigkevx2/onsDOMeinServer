/*Voor creëren database eerst de master database aanroepen, hier staat alle systeem info
in en worden de databases in gezet die worden aangemaakt door ontwikkelaars*/
USE master;
GO

/*in de master database worde de OnsDomein database verwijdert, mits deze bestaat*/
DROP DATABASE IF EXISTS OnsDomein;
GO

/*de OnsDomein database wordt nu gemaakt in de master database*/
CREATE DATABASE OnsDomein;
GO

/*de OnsDomein database wordt na het creëren geselecteerd om de verdere database op te bouwen.
Als het script zonder het selecteren van de juiste database wordt gerund dan worden de tabellen
en alle andere objecten in de verkeerde database aangemaakt.*/
USE OnsDomein;
GO

/*TABELLEN*/
CREATE TABLE ServerLog(
	log_ID				int	IDENTITY(1,1)		NOT NULL,
	inServerTimeStamp	datetime				NOT NULL,
	outServerTimeStamp	datetime					NULL,
	sendingClient		varchar(50)				NOT NULL,
	receivingClient		varchar(50)					NULL,
	command				varchar(10)						NULL,
	cmdMessage			varchar(max)					NULL
);
GO

select * from ServerLog order by InServerTimeStamp