FROM openjdk:11

ARG project
ENV PROJECT $project
ARG version
WORKDIR /app

# Download kafka cli
RUN apt-get update && \
 apt-get install -y wget && \
 wget https://archive.apache.org/dist/kafka/2.2.1/kafka_2.12-2.2.1.tgz && \
 tar xzf kafka_2.12-2.2.1.tgz

ADD ./distributions/$project-$version.tar /app/
WORKDIR /app/$project-$version
CMD bin/$PROJECT
