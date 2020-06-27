FROM debian:jessie
MAINTAINER Livo Mobile

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -qqy \
    supervisor \
    openjdk-7-jdk \
    tomcat8 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* /var/lib/tomcat8/webapps/*

ADD supervisord.conf /etc/supervisor/
ADD distribution-1.0.0-bin.tar.gz /srv/livod/
ADD web-ui-1.0.0.war /var/lib/tomcat8/webapps/ROOT.war

RUN chmod +x /srv/livod/start.sh

ENV AEON_HOME /var/opt/livod/

EXPOSE 8080 2366

VOLUME /var/opt/livod/
VOLUME /var/lib/tomcat8/logs/

ENTRYPOINT /usr/bin/supervisord -c /etc/supervisor/supervisord.conf