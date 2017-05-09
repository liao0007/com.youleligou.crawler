#VPS
* deploy __hostwriter__ app
* install squid server
```
yum install -y httpd-tools &&
yum install -y squid &&
yum install -y vim
```
* config squid
```
echo "
############ SQUID CONFIG ############
auth_param basic program /usr/lib64/squid/basic_ncsa_auth /etc/squid/passwords
auth_param basic realm proxy
acl authenticated proxy_auth REQUIRED
http_access allow authenticated
http_port *PROXY_PORT*

coredump_dir /var/spool/squid
cache deny all
via off
forwarded_for transparent
never_direct allow all
always_direct deny all
cache_peer vps001.youleligou.com parent *PROXY_PORT* 0 round-robin no-query no-digest connect-fail-limit=999999 login=PASSTHRU
############ SQUID CONFIG ############ " > /etc/squid/squid.conf
```
* start squid
```
systemctl enable squid && 
systemctl restart squid &&
firewall-cmd --zone=public --add-port=433/tcp --permanent &&
firewall-cmd --reload
```
* schedule squid restart
```
* */1 * * * systemctl restart squid
```
#VPS PEERS
* setup pppoe
```
systemctl stop NetworkManager.service && 
pppoe-setup
```

* install all dependencies
```
yum install -y httpd-tools &&
yum install -y squid &&
yum install -y vim
```

* setup squid
```
echo "
############ SQUID CONFIG ############
auth_param basic program /usr/lib64/squid/basic_ncsa_auth /etc/squid/passwords
auth_param basic realm proxy
acl authenticated proxy_auth REQUIRED
http_access allow authenticated
http_port 433

coredump_dir /var/spool/squid
cache deny all
via off
forwarded_for off

request_header_access From deny all
request_header_access Server deny all
request_header_access WWW-Authenticate deny all
request_header_access Link deny all
request_header_access Cache-Control deny all
request_header_access Proxy-Connection deny all
request_header_access X-Cache deny all
request_header_access X-Cache-Lookup deny all
request_header_access Via deny all
request_header_access X-Forwarded-For deny all
request_header_access Pragma deny all
request_header_access Keep-Alive deny all
############ SQUID CONFIG ############
" > /etc/squid/squid.conf && 
htpasswd -c /etc/squid/passwords vps
```

* setup pppoe script with HOSTNAME
```
echo "
#!/bin/sh
HOSTNAME=vps001.youleligou.com
/sbin/ifdown ppp0
sleep 1
/sbin/ifup ppp0
sleep 1
echo "curl -s vps.youleligou.com:9000/${HOSTNAME}"
curl -s vps.youleligou.com:9000/${HOSTNAME}
" > /root/change_ip.sh && chmod +x /root/change_ip.sh
```

* start squid and setup firewall
```
systemctl enable squid && 
systemctl restart squid &&
firewall-cmd --zone=public --add-port=433/tcp --permanent &&
firewall-cmd --reload
```
* schedule change ip script and squid restart
```
*/2 * * * * /root/change_ip.sh
* */1 * * * systemctl restart squid
```
* test and trigger
```
curl -x 127.0.0.1:433 -U vps:vps www.baidu.com
./change_ip.sh
```

#### check ip
```
curl cip.cc
```