@echo off && cls

rem MySQL DB connection properties:
set dbhost=localhost
set dbport=33
set dbuser=root
set dbpass=BPL2sql
set dbname=login

mysql -h %dbhost% -P %dbport% -u %dbuser% --password=%dbpass% --execute="DROP DATABASE IF EXISTS %dbname%"
mysql -h %dbhost% -P %dbport% -u %dbuser% --password=%dbpass% --execute="CREATE DATABASE IF NOT EXISTS %dbname%"
echo =======================================[Installing LoginServer Tables]
for /r login %%f in (*.sql) do echo.   - File: %%f && mysql -h %dbhost% -P %dbport% -u %dbuser% --password=%dbpass% -D %dbname% < %%f
echo. && echo ================================================================[Done] && echo.
pause