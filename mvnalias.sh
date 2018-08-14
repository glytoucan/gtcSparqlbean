#!/bin/sh
alias mvn='sudo docker run -it --rm -h maven.bluetree.jp -v /opt/maven:/root/.m2 -v ${PWD}:/maven -w /maven maven:3.3.3-jdk-8 mvn'
