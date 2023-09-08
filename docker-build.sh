#!/bin/bash

image=balance:latest-alpine
hubname=mfahg4j9q8hzt
hubpwd=KrZ%=9897=ca%]2

docker build -t ${image} .

# tag image
docker tag ${image} ${hubname}/${image}

# login to hub
docker login -u ${hubname} -p ${hubpwd}

# push image
docker push ${hubname}/${image}
