
## sven：是一个提交spark任务的客户端工具，且可以管理hdfs文件数据，具体用法如下：
### 0、请求转发逻辑参考：https://github.com/cloudera/livy
### 1、sven --file xxxx ： 表示提交spark 任务，请求中间会经过livy服务器 

``` 
sven 
 arguments
 --file(required) 				Local File containing the application to execute
 Options:
 --class 					Application Java/Spark main class.
 --args 					Command line arguments for the application
 --proxyUser 					User to impersonate when running the job
 --jars 					jars to be used in this session
 --pyFiles 					Python files to be used in this session
 --driverMemory 				Amount of memory to use for the driver process
 --driverCores 					Number of cores to use for the driver process
 --executorMemory 				Amount of memory to use per executor process
 --executorCores 				Number of cores to use for each executor
 --numExecutors 				Number of executors to launch for this session
 --archives 					Archives to be used in this session
 --queue 					The name of the YARN queue to which submitted
 --name 					The name of this session
 --conf 					PROP=VALUE Arbitrary Spark configuration property 
 --logs 					Logs of the application output in the yarn cluster
 --files                                         增加配置文件参数 ,多个文件用空格分割， 使用绝对路径 
 --version 					Print the version of current spark-client.

example:
例子一：
sven --file /www/harbinger-spark/examples/jars/spark-examples_2.11-2.1.0.jar --className org.apache.spark.examples.SparkPi \
--conf spark.master=yarn spark.submit.deployMode=cluster --queue rec-test --logs /home/lhp/kunkka-sven/logs/k.log --files /home/hadoopuser/hello.test /home/hadoopuser/hello.test&

例子二： 
sven --file /www/meitu-hot-recall/tools/spark-cf.jar \
  --class com.meitu.rec.sparkcf.drivers.SimilarityDriver \
  --conf \
  spark.master=yarn\
  spark.executor.instances=45\
  spark.executor.cores=2 \
  spark.executor.memory=6g \
  spark.default.parallelism=20 \
  spark.submit.deployMode=cluster \
  --args \
  --algorithm "itemcf" \
  --similarity "Cosine" \
  --threshold 0.001 \
  --maxUserItems 5000 \
  --maxItemUsers 5000 \
  --inputFile "hdfs://192.168.199.102/data/rec/meitu/meitu-hot-recall/user-item-score/20171102" \
  --outputDir "hdfs://192.168.199.102/data/rec/meitu/meitu-hot-recall/item-cf/simi/20171102/"
 
 
/www/kunkka-sven/bin/sven.sh --conf \
    spark.master=yarn \
    spark.default.parallelism=471 \
    spark.app.name="topicmodel-preprocess/baiy" \
    spark.driver.maxResultSize=8g \
    spark.submit.deployMode=cluster \
    --executorCores 4 \
    --executorMemory 16g \
    --numExecutors 32 \
    --driverMemory 8g \
    --queue rec-meipai \
    --file $scripts_dir/preprocess_spark.py \
    --files $conf_dir/topic_model.conf \
    --pyFiles $scripts_dir/preprocess_spark.py \
    --args --batch_date $batch_date
 ```

### 2、sven fs xxx   表示管理hdfs数据
```
usage: sven [arguments] [options]
 arguments
 -fs(required) 					identification	run manange the hdfs 
 Options:
 -ls 						eg: sven fs -ls  hdfsDir		
 -cat 						eg: sven fs -cat  hdfsFile		
 -getmerge					eg: sven fs -getmerge  hdfsDir     localFile
 -put 						eg: sven fs -put      localPath hdfsDir
 -rm 						eg: sven fs -rm   hdfsFile     
 -rmr 					    	eg: sven fs -rmr  hdfsDir 
 -mkdir 					eg: sven fs -mkdir  hdfsDir 
 -get 						eg: sven fs -get  hdfsPath     localFile
  
 
 
example:  sven fs -get /package/server.log /home/lhp/a.log



 说明:

*	sven fs [options] 表示管理的是hdfs文件
*	sven --file(required) [options] 表示提交spark 作业
```
### 3、sven 自从3.4.0版本后，新增俩个命令 sven-hadoop 和sven-spark
    
     
       1、sven-hadoop fs xxxx : 表示管理hdfs数据文件，语法和 hadoop fs xxx一样；
       2、sven-spark ： 表示提交spark 任务，语法和spark-submit一样，但是要求使用hadoopuser用户使用该命令
      










 


