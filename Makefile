
deploy:
	mvn clean install
	cp target/transfer.war /var/lib/tomcat/webapps/ROOT.war
