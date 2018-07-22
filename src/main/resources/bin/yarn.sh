#!/usr/bin/env bash



java -classpath /www/kunkka-sven/conf/:/www/kunkka-sven/lib/*  -Dhadoop.root.logger=INFO,console -Dyarn.root.logger=INFO,console -Djava.library.path=/www/harbinger-hadoop/lib/native org.apache.hadoop.yarn.client.cli.LogsCLI -applicationId application_1524731821779_1415
