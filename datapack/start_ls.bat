@echo off
set PATH=E:\java\jre1.6.0_45\bin
java -server -Dfile.encoding=UTF-8 -Xms1024m -Xmx1024m -cp config;./*;./server.jar l2p.loginserver.L2LoginServer
pause
