# Expects CentOS 7 and Tomcat 7.
deploy:
	mvn clean install
	cp target/transfer.war /var/lib/tomcat/webapps/ROOT.war

lazy:
	mvn -Dmaven.test.skip=true clean install
	cp target/transfer.war /var/lib/tomcat/webapps/ROOT.war
