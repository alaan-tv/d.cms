#!/usr/bin/env bash

IMAGE=$1
CONTAINER=$2
FELIX_VOLUME=$3
eval 'docker stop ${CONTAINER}'
eval 'docker rm ${CONTAINER}'
eval 'docker build --no-cache . -t ${IMAGE}'
eval 'docker run -d -t --name ${CONTAINER} -p 1099:1099 -p 8080:8181 -p 44444:44444 -v ${FELIX_VOLUME}:/deploy ${IMAGE}'
eval 'docker exec -it ${CONTAINER} bash'