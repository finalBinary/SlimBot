#!/bin/bash

JF=/home/giovanni/Java/Jars

MySQL=$JF/mysql-connector-java-5.0.8-bin.jar
Jsoup=$JF/jsoup.jar
Gson=$JF/gson-2.6.2.jar
PictureJason=/home/giovanni/Java/SlimBot/PictureJason;
#MySQLHandling=/home/giovanni/Java/StreamManager/MySQLHandling
#ErrorHandling=MySQLHandling/ErrorHandling/ErrorHandler
#PageParser=/home/giovanni/Java/StreamManager/PageParser

java -cp .:$Jsoup:$Gson:$PictureJason testBot
