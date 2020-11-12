#
# Anti-Monitor
#

server/target: web/dist
	@echo "package Fat Jar"
	rm -Rf src/main/resources/META-INF/resources
	mv web/dist/spa src/main/resources/META-INF/resources
	mvn clean install

clean:
		@echo "clean All distributions"
		@rm -Rf web/dist
		@rm -Rf server/target

web/dist: clean
	@echo "package web"
	cd web && yarn install && yarn build


