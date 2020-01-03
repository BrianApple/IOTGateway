#!/bin/sh
#守护进程脚本，当网关异常关闭时主动重启网关
#建议直接通过该脚本启动网关！
#author:yangcheng
#网关编号
NUM=1
#基础目录，网关配置文件以及网关日志文件等都在当前路径下
BASE_DIR=/opt
#网关日志名称
LOGNAME=iotgate
#JVM优化参数
JVM="-Xms2g -Xmx2g -XX:NewRatio=2 -XX:+UseG1GC  -XX:MaxGCPauseMillis=20 -Dio.netty.leakDetectionLevel=advanced -Xloggc:$BASE_DIR/gc.log -XX:MaxDirectMemorySize=1G -Dio.netty.allocator.pageSize=8192 -Dio.netty.allocator.maxOrder=10 -Dio.netty.recycler.maxCapacity=0 -Dio.netty.recycler.maxCapacity.default=0"
PAT=`dirname $0`
function start {
        
		   command=`nohup java $JVM -jar $BASE_DIR/Gate4HYKJ-1.0.1.jar -n $NUM -f $BASE_DIR/iotGate.conf  > $BASE_DIR/$LOGNAME.log &`
		   

        tail -n 30  $BASE_DIR/$LOGNAME.log
}


for((i=0 ; ; i++))
do
  sleep 1
  server=`ps aux | grep Gate4HYKJ-1.0.1.jar | grep -v grep`
        if [ ! "$server" ]; then
            #如果不存在就启动
            start
        fi
        sleep 1
done
