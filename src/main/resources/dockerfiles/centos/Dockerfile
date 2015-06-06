FROM centos:centos7

MAINTAINER Sergii Kozlov "derzaicheg@gmail.com"

#Install dependencies and download java8
RUN yum -y install wget && \
    yum -y install tar && \
    wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.tar.gz"

#Install Jetty
ENV JETTY_VERSION 9.2.10.v20150310
RUN wget http://download.eclipse.org/jetty/${JETTY_VERSION}/dist/jetty-distribution-${JETTY_VERSION}.tar.gz && \
    tar -zxf jetty-distribution-${JETTY_VERSION}.tar.gz  && \
    mv jetty-distribution-${JETTY_VERSION}/ /opt/jetty

#Install Java
RUN tar -zxf jdk-8u25-linux-x64.tar.gz && \
    mv jdk1.8.0_25/ /opt && \
    alternatives --install /usr/bin/java java /opt/jdk1.8.0_25/bin/java 2 && \
    alternatives --auto java

#Set Env Variables
#RUN export JAVA_HOME=/opt/jdk1.8.0_25
#RUN export JRE_HOME=/opt/jdk1.8.0_25/jre
#RUN export PATH=$PATH:/opt/jdk1.8.0_25/bin:/opt/jdk1.8.0_25/jre/bin

ENV JAVA_HOME /opt/jdk1.8.0_25
ENV JRE_HOME /opt/jdk1.8.0_25/jre
ENV PATH $PATH:/opt/jdk1.8.0_25/bin:/opt/jdk1.8.0_25/jre/bin
ENV JETTY_HOME /opt/jetty
ENV JETTY_USER root
ENV JETTY_HOST 0.0.0.0
ENV JETTY_LOGS /opt/jetty/logs

ADD http://tomcat.apache.org/tomcat-6.0-doc/appdev/sample/sample.war /opt/jetty/webapps/

WORKDIR /opt/jetty
EXPOSE 8080
CMD ["java", "-jar", "/opt/jetty/start.jar"]