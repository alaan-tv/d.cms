#!/usr/bin/env bash
docker stop dcms
docker rm dcms
docker build --no-cache . -t dee/cms
docker run -d -t --name dcms -p 1099:1099 -p 8080:8181 -p 44444:44444 dee/cms
docker exec -it dcms bash