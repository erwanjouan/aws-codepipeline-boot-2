include sam-lambda-jar/bootstrap.mk

install_jupyter:
	pip3 install jupyterlab matplotlib diagrams

profile:
	java -jar \
		-XX:StartFlightRecording=disk=true,maxage=3m,dumponexit=true,filename=./logs/myApp.jfr  \
		-XX:FlightRecorderOptions=maxchunksize=1M,memorysize=1M,repository=./logs \
		-jar target/*.jar
