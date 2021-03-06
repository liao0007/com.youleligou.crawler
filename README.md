# com.youleligou.crawler
Stacks:
* ES
* SPARK
* CASSANDRA
* AKKA


UI Managers
* Kibana http://192.168.1.31:5601
* Spark http://192.168.1.31:8080

high volume crawler based on akka


## deploy
```
sbt assembly && scp ./target/scala-2.11/com.youleligou.crawler-assembly-1.0-SNAPSHOT.jar root@192.168.1.31:/root/crawler
```

### dependent services
```
docker run --restart=always -p 6379:6379 --name redis -d redis
docker run --restart=always -p 3128:3128 --name squid -v /etc/squid/squid_combined.conf:/etc/squid3/squid.conf -d sameersbn/squid
firewall-cmd --zone=public --add-port=6379/tcp --permanent
firewall-cmd --zone=public --add-port=3128/tcp --permanent
firewall-cmd --reload
```

```
//create database dir 
mkdir /var/data && mkdir /var/data/cassandra
chcon -Rt svirt_sandbox_file_t /var/data/cassandra

//first node
docker run --restart=always -p 9042:9042 -p 9160:9160 -p 7000:7000 --name cassandra -v /var/data/cassandra:/var/lib/cassandra -d -e CASSANDRA_BROADCAST_ADDRESS=192.168.1.32 -e CASSANDRA_CLUSTER_NAME="YOLO CASSANDRA CLUSTER" cassandra

//subsequent nodes
docker run --restart=always -p 9042:9042 -p 9160:9160 -p 7000:7000 --name cassandra -v /var/data/cassandra:/var/lib/cassandra -d -e CASSANDRA_SEEDS=192.168.1.32 -e CASSANDRA_BROADCAST_ADDRESS=192.168.1.33 -e CASSANDRA_CLUSTER_NAME="YOLO CASSANDRA CLUSTER" cassandra

//open ports for each node
firewall-cmd --zone=public --add-port=9042/tcp --permanent
firewall-cmd --zone=public --add-port=9160/tcp --permanent
firewall-cmd --zone=public --add-port=7000/tcp --permanent
firewall-cmd --reload
```

# config elastic search
```
echo '
vm.max_map_count=262144' >> /etc/sysctl.conf
sysctl -w vm.max_map_count=262144
mkdir /var/data/elasticsearch && chmod 777 /var/data/elasticsearch
docker run --restart=always -p 9200:9200 -p 9300:9300 --name elas -v /var/data/elasticsearch:/usr/share/elasticsearch/data -e network.publish_host=192.168.1.34 -e discovery.zen.ping.unicast.hosts=192.168.1.32,192.168.1.34,192.168.1.34 -e cluster.name=yolo-es-cluster -d docker.elastic.co/elasticsearch/elasticsearch:5.4.0
docker run --restart=always -p 5601:5601 --name kibana -e ELASTICSEARCH_URL=http://192.168.1.32:9200 -d docker.elastic.co/kibana/kibana:5.4.0
firewall-cmd --zone=public --add-port=9200/tcp --permanent
firewall-cmd --zone=public --add-port=9300/tcp --permanent
firewall-cmd --zone=public --add-port=5601/tcp --permanent
firewall-cmd --reload
```

# config spark
* master
```
firewall-cmd --zone=public --add-port=6066/tcp --permanent
firewall-cmd --zone=public --add-port=7077/tcp --permanent
firewall-cmd --zone=public --add-port=4040/tcp --permanent
firewall-cmd --zone=public --add-port=8080/tcp --permanent
firewall-cmd --reload
./sbin/start-master.sh -h hp01.youleligou.com
```
* slave
```
firewall-cmd --zone=public --add-port=7077/tcp --permanent
firewall-cmd --zone=public --add-port=8081/tcp --permanent
firewall-cmd --reload
./sbin/start-slave.sh -h hp04.youleligou.com spark://hp01.youleligou.com:7077
```