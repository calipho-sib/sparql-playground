FROM openjdk:11-jre-slim
COPY sparql-playground.war /app/sparql-playground.war
COPY start.sh /app/start.sh
WORKDIR /app
EXPOSE 8080
CMD ["sh", "start.sh"]
