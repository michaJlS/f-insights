Flickr Assistant
=================================
My Scala+Play Framework+Spark playground.

Configuration
==============
dev.conf.sample

https://www.flickr.com/services/apps/create/
http://www.playframework.com/documentation/latest/ApplicationSecret

Running
========
sbt "compile -Dconfig.resource=dev.conf"
sbt "run -Dconfig.resource=dev.conf"

Readings
=========
Flickr  API Docs: https://www.flickr.com/services/api/
Flickr OAuth:: https://www.flickr.com/services/api/auth.oauth.html
OAuth: https://www.playframework.com/documentation/2.4.x/ScalaOAuth
WS: https://www.playframework.com/documentation/2.4.x/ScalaWS
