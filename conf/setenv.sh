# Logging configuration

# Print status messages (especially logging configuration) to the standard output
# See also: <http://logback.qos.ch/manual/configuration.html#statusListener>
#CATALINA_OPTS="$CATALINA_OPTS -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener"

# Logback configuration
CATALINA_OPTS="$CATALINA_OPTS -Dlogback.configurationFile=file:$CATALINA_HOME/conf/logback.xml"
