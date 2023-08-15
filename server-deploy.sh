#!/bin/bash

# sudo required

# this script should be run in the production server

root_password=$1

# docker hub login credential
dockerhub_username="mfahg4j9q8hzt"

dockerhub_repo_name="balance"
image_name=${dockerhub_repo_name}
dockerhub_tag="latest"
# this is the docker image full name, dockerhub_username/dockerhub_repo_name:dockerhub_tag
image_full_name=${dockerhub_username}/${dockerhub_repo_name}:${dockerhub_tag}

# this is the docker container name
container_name="balance-expense-server"

echo 1

echo ${root_password}
# we pull the application down as a docker image
echo ${root_password} | sudo docker pull ${image_full_name}

echo test
# remote the old one

echo ${root_password} | sudo docker rm -f ${container_name}

# we run the application
echo ${root_password} | sudo docker run -d -p 8095:8095 --name ${container_name} ${image_full_name}