# Expects CentOS 7 and Tomcat 7.
deploy:
	LANG=C mvn clean install
	cp target/transfer.war /var/lib/tomcat/webapps/ROOT.war

lazy:
	LANG=C mvn -Dmaven.test.skip=true clean install
	cp target/transfer.war /var/lib/tomcat/webapps/ROOT.war
