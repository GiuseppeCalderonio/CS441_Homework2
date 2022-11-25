# Install Java and set the JAVA_HOME variable
FROM ubuntu

RUN apt-get update


RUN apt install openjdk-18-jdk -y


ENV SBT_VERSION 1.7.2

# Install curl and vim
RUN \
  apt-get -y install curl && \
  apt-get -y install vim

# Install both scala and sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://repo.scala-sbt.org/scalasbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get -y install sbt

RUN mkdir -p /root/build/project
ADD build.sbt /root/build/
ADD ./project/plugins.sbt /root/build/project
ADD src /root/build/src

WORKDIR /root/build

CMD sbt clean compile test