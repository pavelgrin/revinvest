FROM tomcat:jre21-temurin-noble
RUN rm -rf /usr/local/tomcat/webapps/*
EXPOSE 8080
ARG WAR_FILE
COPY ${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war
CMD ["catalina.sh", "run"]
