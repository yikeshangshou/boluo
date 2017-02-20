#!/bin/sh


# Update and build.
echo 'Update and build it...'
cd /home/source/dabllo/webapp
git pull
mvn clean install -Dmaven.test.skip

cd /home/source/dabllo/webapp/dabllo-projects/dabllo-web
mvn clean package -Dmaven.test.skip -Ponline


# Deploy and restart the server.
echo 'Kill the existing application...'
pid=`ps -ef | grep /home/server/dabllo/ | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]; then
  kill -9 $pid
fi
sleep 3

echo 'Deploy and start the application...'
rm -rf /home/server/dabllo/webapps/*
cp /home/source/dabllo/webapp/dabllo-projects/dabllo-web/target/ROOT.war /home/server/dabllo/webapps/
setsid sh /home/server/dabllo/bin/startup.sh

echo 'Deploy completed!'
sleep 5

tailf /home/server/dabllo/logs/catalina.out