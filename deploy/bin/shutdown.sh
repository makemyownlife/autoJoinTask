#!/bin/sh

#set JAVA_HOME
#JAVA_HOME=/usr/local/java

#check JAVA_HOME & java
noJavaHome=false
if [ -z "$JAVA_HOME" ] ; then
    noJavaHome=true
fi
if [ ! -e "$JAVA_HOME/bin/java" ] ; then
    noJavaHome=true
fi
if $noJavaHome ; then
    echo
    echo "Error: JAVA_HOME environment variable is not set."
    echo
    exit 1
fi
#==============================================================================

#set JAVA_OPTS
JAVA_OPTS="-Xss128k"
#==============================================================================

#stop Server
$JAVA_HOME/bin/jps |grep StartUp|awk -F ' ' '{print $1}'|while read line
do
  eval "kill -9 $line"
done
#==============================================================================

#set HOME
CURR_DIR=`pwd`
cd `dirname "$0"`/..
SEARCH_HOME=`pwd`
cd $CURR_DIR
if [ -z "SEARCH_HOME" ] ; then
    echo
    echo "Error: SEARCH_HOME environment variable is not defined correctly."
    echo
    exit 1
fi
#==============================================================================

#set CLASSPATH
SEARCH_CLASSPATH="$SEARCH_HOME/conf:$SEARCH_HOME/lib/classes"
for i in "$SEARCH_HOME"/lib/*.jar
do
    SEARCH_CLASSPATH="$SEARCH_CLASSPATH:$i"
done
#==============================================================================

#shutdown Server
RUN_CMD="\"$JAVA_HOME/bin/java\""
RUN_CMD="$RUN_CMD -DSEARCH.home=\"$SEARCH_HOME\""
RUN_CMD="$RUN_CMD -classpath \"$SEARCH_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.diyicai.server.ShutDown $@"
RUN_CMD="$RUN_CMD >> \"$SEARCH_HOME/logs/console.log\" 2>&1 &"
echo $RUN_CMD
eval $RUN_CMD
#==============================================================================