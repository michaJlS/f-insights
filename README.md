Disclaimer
===========
This is my playground for learning Scala, Play framework, Akka & Cassandra or whatever else I will decide to include
in this project in future.


Flickr Assistant
=================================
Set of tools which aims to help you to gain back the control over loads of photos which you miss on flickr but you 
should see them and stuff like that. 

Configuration
==============
dev.conf.sample

https://www.flickr.com/services/apps/create/
http://www.playframework.com/documentation/latest/ApplicationSecret

Running
========
```
sbt "compile -Dconfig.resource=dev.conf"
sbt "run -Dconfig.resource=dev.conf"
```


Frontend
=========
```
npm install
grunt
grunt watch
```


Readings
=========
Flickr  API Docs: https://www.flickr.com/services/api/
