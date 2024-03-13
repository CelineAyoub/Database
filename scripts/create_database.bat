rem create_database.bat

@echo off

set SERVER_NAME=LocalInstence\MAJID
set DATABASE_USER=root
set DATABASE_PASSWORD=''
set SCRIPT_FILE="C:\coffe shop\scripts"

sqlcmd -S %SERVER_NAME% -U %DATABASE_USER% -P %DATABASE_PASSWORD% -d master -i %SCRIPT_FILE%

echo Database creation completed.