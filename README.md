# com.youleligou.crawler

high volumn crawler based on akka


## deploy
```
sbt assembly; scp ./target/scala-2.12/com.youleligou.crawler-assembly-1.0-SNAPSHOT.jar root@192.168.1.31:/root
```

### dependent services
```
docker run --restart=always -p 6379:6379 --name redis -d redis
docker run --restart=always -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=*passwd* -d mysql --character-set-server=utf8 --collation-server=utf8_unicode_ci
docker run --restart=always -p 3128:3128 --name squid -v /etc/squid/squid_combined.conf:/etc/squid3/squid.conf -d sameersbn/squid
```

```
docker run --restart=always -p 9042:9042 -p 9160:9160 --name cassandra -v /var/data/cassandra:/var/lib/cassandra -d cassandra
firewall-cmd --zone=public --add-port=9042/tcp --permanent
firewall-cmd --zone=public --add-port=9160/tcp --permanent
firewall-cmd --reload
```

### crontab
```
30,0 * * * * cat squid.conf cache_peer.conf > squid_combined.conf; docker restart *docker_id*
```