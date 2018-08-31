#!/bin/bash
set -e

cp tmplt.Dockerfile Dockerfile

VERSION=`cat cdrkeeper/VERSION`
SV="cdrkeeper-${VERSION}"

sed -i -e 's/{VERSION}/'"${VERSION}"'/g' Dockerfile

docker build -t cdrkeeper:latest .

rm ./Dockerfile

docker tag cdrkeeper:latest readytalk/cdrkeeper:${VERSION}
docker tag cdrkeeper:latest readytalk/cdrkeeper:latest

if [[ ${TRAVIS} && "${TRAVIS_BRANCH}" == "master" && -n $DOCKER_USERNAME && -n $DOCKER_PASSWORD ]]; then
  docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
  docker push readytalk/cdrkeeper:${VERSION}
  docker push readytalk/cdrkeeper:latest
fi



