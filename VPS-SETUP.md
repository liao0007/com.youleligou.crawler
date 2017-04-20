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
acl SSL_ports port 443
acl Safe_ports port 80          # http
acl Safe_ports port 21          # ftp
acl Safe_ports port 443         # https
acl Safe_ports port 70          # gopher
acl Safe_ports port 210         # wais
acl Safe_ports port 1025-65535  # unregistered ports
acl Safe_ports port 280         # http-mgmt
acl Safe_ports port 488         # gss-http
acl Safe_ports port 591         # filemaker
acl Safe_ports port 777         # multiling http
acl CONNECT method CONNECT

auth_param basic program /usr/lib64/squid/basic_ncsa_auth /etc/squid/passwords
auth_param basic realm proxy
acl authenticated proxy_auth REQUIRED
http_access allow authenticated
http_port *PROXY_PORT*

coredump_dir /var/spool/squid
cache deny all
never_direct allow all
cache_peer vps001.youleligou.com parent *PROXY_PORT* 0 round-robin no-query no-digest connect-fail-limit=999999 login=PASSTHRU
############ SQUID CONFIG ############ " > /etc/squid/squid.conf
```
* start squid
```
systemctl enable squid && 
systemctl restart squid &&
firewall-cmd --zone=public --add-port=*PROXY_PORT*/tcp --permanent &&
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
acl SSL_ports port 443
acl Safe_ports port 80          # http
acl Safe_ports port 21          # ftp
acl Safe_ports port 443         # https
acl Safe_ports port 70          # gopher
acl Safe_ports port 210         # wais
acl Safe_ports port 1025-65535  # unregistered ports
acl Safe_ports port 280         # http-mgmt
acl Safe_ports port 488         # gss-http
acl Safe_ports port 591         # filemaker
acl Safe_ports port 777         # multiling http
acl CONNECT method CONNECT

http_access deny !Safe_ports
http_access deny CONNECT !SSL_ports

auth_param basic program /usr/lib64/squid/basic_ncsa_auth /etc/squid/passwords
auth_param basic realm proxy
acl authenticated proxy_auth REQUIRED
http_access allow authenticated
http_port 433

coredump_dir /var/spool/squid
cache deny all
via off
forwarded_for off
request_header_access X-Forwarded-For deny all
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
curl -x 127.0.0.1:433 -U vps:PASSWORD www.baidu.com
./change_ip.sh
```

#### check ip
```
curl cip.cc
```