rm -rf target
mvn package
cp target/sparql-playground-1.4.1.war sparql-playground.war
rm -rf target
