FROM tomcat:jre21-temurin-noble
RUN rm -rf /usr/local/tomcat/webapps/*

# Debug port
EXPOSE 5005
# Application port
EXPOSE 8080

COPY conf/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY conf/logback.xml /usr/local/tomcat/conf/logback.xml

# Don't forget to define the WAR_FILE arg during build
ARG WAR_FILE
COPY ${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war

ENTRYPOINT ["catalina.sh"]
CMD ["run"]
