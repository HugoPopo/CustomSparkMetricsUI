#!/bin/sh

if [ "$1" = 1 ]
then
    echo "launching sbt package...\n"
    sbt package
fi
spark-shell --conf spark.logConf=true --conf spark.extraListeners=org.apache.spark.CustomSparkListener --driver-class-path target/scala-2.11/custom-spark-metrics-ui_2.11-1.0.jar
