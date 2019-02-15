#!/bin/bash
java -Djava.ext.dirs=. -jar JavaAgentServer.jar > server.log
echo pause
read -n 1
