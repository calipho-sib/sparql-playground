rm -rf target
mvn package
cp target/sparql-playground-1.5.0.war sparql-playground.war
rm -rf target
