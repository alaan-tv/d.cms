#!/usr/bin/env bash

#place output build into the volume mapped folder

docker stop dcms
docker rm dcms
docker build --no-cache . -t dee/cms
docker run -d -t --name dcms -p 1099:1099 -p 8181:8181 -p 44444:44444 -v /work/env/docker.deploy/:/deploy dee/cms
docker exec -it dcms bash