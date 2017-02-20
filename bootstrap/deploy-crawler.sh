#!/bin/sh
project=dabllo-crawler


# Update and build.
echo 'Update and build it...'
cd /home/source/dabllo/webapp
git pull
mvn clean install -Dmaven.test.skip

cd /home/source/dabllo/webapp/dabllo-projects/$project
mvn clean package -Dmaven.test.skip -Ponline


# Deploy and restart the server.
echo 'Kill the existing application...'
pid=`ps -ef | grep /home/logs/dabllo/crawler-gc.log | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]; then
  kill -9 $pid
fi
sleep 3

echo 'Deploy and start the application...'
rm -rf /home/deploy/$project/*
cp /home/source/dabllo/webapp/dabllo-projects/$project/target/$project-standalone.tar.gz /home/deploy/$project/
cd /home/deploy/$project
tar zxf $project-standalone.tar.gz
sh /home/deploy/$project/startup.sh

echo 'Deploy completed!'
sleep 5

tailf /home/logs/dabllo/crawler.log
