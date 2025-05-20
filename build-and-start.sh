#!/bin/bash


if [ "$EUID" -eq 0 ]; then
  echo "Doesn't run this script like root."
  echo "When we build project we cannot be root."
  echo "In the docker execution we will need your root powers"
  exit 1
fi

echo "Building projects"

cd vote-challenge/ && ./gradlew clean build
cd ..
cd agenda-processor/ && ./gradlew clean build
cd ..
cd vote-visualizer/ && ./gradlew clean build
cd ..

echo "Projects already builded"
echo "I need to sudo permission to execute docker images"

sudo docker-compose up --build
