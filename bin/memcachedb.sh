#!/bin/bash

root=$(
  cd $(dirname $(readlink -e $0 || echo $0))/..
  /bin/pwd
)

if [ -z "$JAVA_HOME" ]
then
    echo "Please set environment JAVA_HOME";
    exit 1
fi

DATE=$(date +%Y%m%d%H%M%S)
JAVA=$JAVA_HOME/bin/java
HEAP="-Xms1G -Xmx1G -XX:MaxPermSize=512M"
HEAP="$HEAP -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$root/jvm-$DATE.hprof"

GC="-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80"
GC="$GC -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark"

GC_LOG="-XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -Xloggc:$root/log/gc.log"
GC_LOG="$GC_LOG -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"

#MORNITOR="-Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.port=5555"
#MORNITOR="$MORNITOR -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

OPT="-server"

CONF=$root/conf/application.conf
LOGBACK=$root/conf/logback.xml
MAINCLASS=com.yiguang.mcdb.McdbServer

PIDFILE=$root/memcachedb.pid

function set_classpath()
{
    CLASSPATH=""
    jars=$(find $root/lib -name *.jar)
	for i in $jars
	do
		tmpclasspath=$i:$tmpclasspath
	done
	CLASSPATH=$CLASSPATH$tmpclasspath
}

function do_start()
{
    set_classpath

    echo "java=$JAVA"
    echo "JVMOPT=$OPT"
    echo "JVMHEAP_OPT=$HEAP"
    echo "JVMGC_OPT=$GC"
    echo "JVMGC_LOG=$GC_LOG"
    echo "CLASSPATH=$CLASSPATH"

    $JAVA $OPT $HEAP $GC $GC_LOG $MORNITOR -cp $CLASSPATH \
    -Dconfig.file=$CONF \
    -Dlogback.configurationFile=$LOGBACK \
    $MAINCLASS > $root/log/run.log 2>&1 &
    PID=$!
    echo $PID > $PIDFILE
}

function do_stop()
{
    PID=$(cat $PIDFILE)
    echo "kill process pid=$PID"
    kill $PID

    if [ "$?"="0" ]
    then
        rm $PIDFILE
    fi
}

case "$1" in
    start)
        if [ -f $PIDFILE ]
        then
            echo "$PIDFILE exists, process is already running or crashed"
        else
            echo "Starting McdbServer..."
            do_start
        fi
        ;;
    stop)
        if [ ! -f $PIDFILE ]
        then
            echo "$PIDFILE does not exist, process is not running"
        else
            while [ -f ${PIDFILE} ]
            do
               do_stop
               echo "Waiting for McdbServer to shutdown ..."
               sleep 1
            done
            echo "McdbServer stopped"
        fi
        ;;
    restart)
        ${0} stop
        ${0} start
        ;;
    *)
        echo "Usage: memcachedb {start|stop|restart}" >&2
        exit 1
esac
