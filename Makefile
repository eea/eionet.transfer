
deploy:
	mvn -Dmaven.test.skip=true clean install
	cp target/transfer.war /var/lib/tomcat/webapps/ROOT.war
