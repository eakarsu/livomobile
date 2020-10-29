Simdilik instructions lar

docker exec -it <container name> /bin/bash

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
export AEON_HOME=/home/mobilelivo/aeon_home

docker run -a stdin -a stdout -i -t ubuntu /bin/bash



docker run --mount type=bind,source=/tmp,target=/myfiles -a stdin -a stdout -i -t ubuntu /bin/bash



Shared /tmp with container



docker run --mount type=bind,source=/tmp,target=/myfiles -a stdin -a stdout -i -t ubuntu:18.04 /bin/bash



Compile live



 docker build  -t livoserver
 
 sudo docker run -d -p 2366:2366 -p 6667:6667  livoserver liv
oserver


sudo docker tag f11dae90b827 eakarsun3/livowebui
 sudo docker tag livoserver           eakarsun3/livoserver

1 - git clone https://github.com/eakarsu/livomobile



2- cd livomobile/livo-server



3- Change libthread versions in thrift related packages (pom.xml files)

sed -i 's/0.9.1/0.13.0/g' `find thrift-* -name pom.xml -print`

5-  
We can install thrift directly
sudo apt-get  install thrift-compiler
apt-get update
apt-get install maven build-essential  openjdk-8-jdk gradle


4- Install thrift package on ubuntu

  
curl -o a.gz https://apache.osuosl.org/thrift/0.13.0/thrift-0.13.0.tar.gz


Tar xvf a.gz

Cd thrift-0.13.0

./configure

Make

Make install

Thrift -version

0.13.0



6- change login method. add appName argument

/root/livomobile/livo-server/authentication-ldap/src/main/java/tr/com/eno/livo/server/authc/ldap/LDAPUserAuthenticationService.java:

mvn -DskipTests package



7- All packaged under distribution/target/distribution-1.0.0-bin/ .I think we will dump it into apache tomcat server



Run server first

export AEON_HOME=/Users/erakarsu/livo/livomobile/aeon_home

Goto /Users/erakarsu/livo/livomobile/livo-server/distribution/target/distribution-1.0.0-bin

./start.sh



Run web ui

export AEON_HOME=/Users/erakarsu/livo/livomobile/aeon_home

Delete whole content of apache tomcat web apps folder

Cp livo-server/web-ui/target/web-ui-1.0.0.war /atacahe-tomcat/webapps/ROOT.war

Bin/startup.sh for tomcat

--
sel33man
Erol Akarsu
AbaptotheFuture18*
 
 google cloud for livo 
livomobile@gmali.com
gikweV-keqhe7-vubqeb


Livo
