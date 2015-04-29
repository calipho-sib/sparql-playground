# Sparql Playground

SPARQL playground has been developed at the <a target="_blank" href="https://www.isb-sib.ch/">Swiss Institute of Bioinformatics</a> for learning SPARQL.

##Technology
* Sparql playground requires Java 1.7 or higher
* The SPARQL engine in use is Sesame 2.7.9
* The java web services are implemented using Spring Boot 1.2.3
* The web interface is build in angularJS 1.3 and bootstrap 3

##Installation
* Download the latest release:
* Run it locally on your machine: 			
```
java -jar sparql-playgroud.war
```

##Predefined RDF datasets

There are some datasets predefined:

* java -jar sparql-playgroud.war Uses default dataset: cats and dogs
* java -jar sparql-playgroud.war nextprot Uses a sample of neXtProt dataset
* java -jar sparql-playgroud.war uniprot Uses a sample of UniProt dataset


##Create your own RDF dataset

You can create your own dataset by giving a directory as argument and calling: java -jar sparql-playgroud.war "your directory name"

Your directory should follow this convention:

* prefixes.ttl: a file containing the default prefixes
* ttl-data: a folder containing turtle file(s)
* queries: a folder containing the queries
* pages: pages with markdown files


#License

The project is opensource and free under the GNU GPL v2 License. The sources are available on GitHub.
This project was forked from the <a target="_blank" href="http://snorql.nextprot.org/">neXtProt snorql interface.</a>


#Contact

For any related questions do not hesitate to <a href="mailto:support@nextprot.org">contact us</a>.
