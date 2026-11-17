@echo off
set PATH=E:\java\jre1.6.0_45\bin
java -server -Dfile.encoding=UTF-8 -Xms8192m -Xmx8192m -cp config;./*;./server.jar l2p.gameserver.GameServer
pause
