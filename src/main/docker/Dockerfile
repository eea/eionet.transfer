FROM centos:7
MAINTAINER "Søren Roug <soren.roug@eea.europa.eu>"

RUN yum -y install epel-release
RUN yum -y install tomcat tomcat-native psmisc

ADD transfer.war /usr/share/tomcat/webapps/ROOT.war

EXPOSE 8080
# Needed for tomcat start
ENV CATALINA_BASE=/usr/share/tomcat NAME=tomcat
# Start tomcat as a daemon and wait for it to crash
CMD /usr/sbin/tomcat start && sleep 60 && while fuser -s -n tcp 8080; do sleep 15; done
