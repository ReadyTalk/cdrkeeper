#!/bin/bash

if [ -z $VERSION ]; then
  VERSION="unknown"

  if [ ! -z $GIT_TAG ]; then
    echo "GET_TAG set: $GET_TAG"
    VERSION=$GET_TAG
  fi
  if [ ! -z $GIT_BRANCH ]; then
    echo "GET_BRANCH set: ${GIT_BRANCH}"
    VERSION=${GIT_BRANCH}
  fi
  if ! git diff-index --quiet HEAD -- 2>/dev/null; then
    echo "found local only changes.. setting version to \"local\""
    VERSION="local"
  fi

  if [[ $VERSION == "unknown" ]]; then
    echo "No Version found, discovering..."
    GIT_FOUND_TAG=$(git describe --exact-match --tags HEAD 2>/dev/null)
    if [ ! -z $GIT_FOUND_TAG ]; then
      VERSION=$GIT_FOUND_TAG
    fi
    GIT_FOUND_BRANCH=$(git branch 2>/dev/null | grep \* | cut -d ' ' -f2)
    if [ ! -z $GIT_FOUND_BRANCH ]; then
      VERSION=$GIT_FOUND_BRANCH
    fi
  fi
else
  echo "Version manually set to ${VERSION}"
fi

if [[ "${VERSION}" == "master" ]]; then
  VERSION="latest"
fi

set -e

docker run --rm -u $(id -u ${USER}):$(id -g ${USER}) -v "$PWD":/home/gradle/cdrkeeper -w /home/gradle/cdrkeeper -e ORG_GRADLE_PROJECT_version=${VERSION} gradle:4.10-jdk8-alpine gradle clean build

docker build --build-arg VERSION=$VERSION -t cdrkeeper:${VERSION} .

if [[ ${TRAVIS} && -n $DOCKER_USERNAME && -n $DOCKER_PASSWORD ]]; then
  docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
  docker push readytalk/cdrkeeper:${VERSION}
fi

