#!/bin/bash

APP_LOG_NAME=app.log
APP_JAR_NAME=server.jar
APP_WD=/app

nohup java -jar ${APP_WD}/${APP_JAR_NAME} > ${APP_LOG_NAME} 2>&1 &

while true; do sleep 3600; done