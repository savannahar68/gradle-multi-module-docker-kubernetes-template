FROM openjdk:11

ARG project
ENV PROJECT $project
ARG version
WORKDIR /app

ADD ./distributions/$project-$version.tar /app/
WORKDIR /app/$project-$version
CMD bin/$PROJECT
