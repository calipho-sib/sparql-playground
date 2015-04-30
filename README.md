# Sparql Playground

SPARQL playground has been developed at the <a target="_blank" href="https://www.isb-sib.ch/">Swiss Institute of Bioinformatics</a> for learning SPARQL.

##Installation
* Download the [latest release](https://github.com/calipho-sib/sparql-playground/tarball/master)
* Unzip and enter the folder, then run: `java -jar sparql-playgroud.war` (requires Java 1.7 or higher)
* Once the application started you should open your browser on: `http://localhost:8080`

##Technology
* Sparql playground requires Java 1.7 or higher
* The SPARQL engine in use is Sesame 2.7.9
* The java web services are implemented using Spring Boot 1.2.3
* The web interface is build in angularJS 1.3 and bootstrap 3

##Predefined RDF datasets

There are some datasets predefined:

* Uses default dataset (cats and dogs): `java -jar sparql-playgroud.war`

* Uses a sample of neXtProt dataset: `java -jar sparql-playgroud.war nextprot`

* Uses a sample of UniProt dataset `java -jar sparql-playgroud.war uniprot `


##Create your own RDF dataset

* You can create your own dataset by giving a directory as argument: `java -jar sparql-playgroud.war your-directory-name`

Your directory should follow this convention:

* ttl-data: a folder containing turtle file(s)
* queries: a folder containing the queries showed in the first page
* prefixes.ttl: a file containing the default prefixes (optional)
* pages: pages with markdown files for the Documentation
* config.properties - optionally you can include this property file with: repository.type=native to create a native repository (instead of memory)

#License

The project is opensource and free under the GNU GPL v2 License. The sources are available on <a target="_blank" href="https://github.com/calipho-sib/sparql-playground">GitHub</a>.

This project was forked from the <a target="_blank" href="http://snorql.nextprot.org/">neXtProt snorql interface.</a>


#Contact

For any related questions do not hesitate to <a href="mailto:support@nextprot.org">contact us</a>.
