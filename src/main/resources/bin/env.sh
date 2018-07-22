#!/usr/bin/env bash



export SPARK_HOME=/www/kunkka-sven


#export PATH="/data1/anaconda/bin:$PATH"
export PATH=/usr/local/anaconda2/bin:$PATH
export PYTHONPATH=$SPARK_HOME/python/:$PYTHONPATH
export PYTHONPATH=/usr/local/anaconda2/bin:$PYTHONPATH
export PYTHONPATH=$SPARK_HOME/python/lib/py4j-0.10.7-src.zip:$PYTHONPATH
export SPARK_SCALA_VERSION=2.11
