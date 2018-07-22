#!/usr/bin/env bash

MAIN_CLASS="com.local.hupper.kunkka.spark.client.RunSparkClient"

source /etc/profile
export LANG=en_US.UTF-8


PROJ_HOME=/www/kunkka-sven
PROJ_LIB=".:${PROJ_HOME}/jars/*"

BIN_DIR="${PROJ_HOME}/bin/"
CONF_DIR="${PROJ_HOME}/conf:${PROJ_HOME}/conf/harbinger2"
LOG_DIR="${PROJ_HOME}/logs"

CUR_TIME=$(date +'%Y%m%d-%H%M')

GC_LOG=${LOG_DIR}/gc.log.${CUR_TIME}
BOOTSTRAP_LOG=${LOG_DIR}/bootstrap.log.${CUR_TIME}

#Memory
export JAVA_OPTS="$JAVA_OPTS -server -Xms1G -Xmx1G -XX:PermSize=256M -XX:MaxPermSize=1G -Xss256k -XX:SurvivorRatio=8"
#gc
export JAVA_OPTS="$JAVA_OPTS -XX:MaxTenuringThreshold=6 -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 \
                  -XX:+UseCMSCompactAtFullCollection -XX:+ExplicitGCInvokesConcurrent -XX:+CMSScavengeBeforeRemark \
                  -XX:-UseBiasedLocking -XX:+AlwaysPreTouch"
#gc
export JAVA_OPTS="$JAVA_OPTS -Xloggc:${GC_LOG} -XX:+UseGCLogFileRotation -XX:GCLogFileSize=20M -XX:NumberOfGCLogFiles=10 \
                 -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -XX:+PrintTenuringDistribution -XX:+PrintGCDetails \
                 -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:PrintFLSStatistics=1"

# sven logo...



if [ ! -d ${LOG_DIR} ]; then
    mkdir -p ${LOG_DIR}
fi


#echo "JRE_HOME:        $JAVA_HOME"
#echo "CLASSPATH:       $PROJ_LIB"
#echo "PROJECT_HOME:    $PROJ_HOME"
#echo "CONF_PATH:       $CONF_DIR"
#echo "LOG_PATH:        $LOG_DIR"
date +"[%F %T] start server " >>${BOOTSTRAP_LOG}

COMMAND=$1
args=$@
if [ "$COMMAND" = "fs" ] ; then
    MAIN_CLASS="com.local.hupper.kunkka.hdfs.client.RunHDFSClient"
    if [ $# == 4 ]; then
            file_real_path=$(readlink -f $4)
            args=$1" "$2" "$3" "$file_real_path
    fi
    if [ $2 == '-put' ]; then
            file_real_path=$(readlink -f $3)
            args=$1" "$2" "$file_real_path" "$4
    fi
    echo -ne "\033[0;32m"
    cat ${PROJ_HOME}/conf/logo
    echo -ne "\033[m";

    java -classpath "${PROJ_LIB}" ${JAVA_OPTS} \
         -Dproject.home=${PROJ_HOME} -Dsven.path=${PROJ_HOME}/conf/harbinger2/sven.conf -Dlogback.configurationFile=${CONF_DIR}/logback.xml -Dfile.encoding=UTF-8 \
         ${MAIN_CLASS}  $args
else
    java -classpath "${PROJ_LIB}" ${JAVA_OPTS} \
         -Dproject.home=${PROJ_HOME} -Dsven.path=${PROJ_HOME}/conf/harbinger2/sven.conf -Dlogback.configurationFile=${CONF_DIR}/logback.xml -Dfile.encoding=UTF-8 \
         ${MAIN_CLASS} "$@"
fi
