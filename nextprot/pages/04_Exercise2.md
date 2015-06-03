#Exercise 2

Build a SPARQL query to find:

* **Proteins with 10 or more gold interactions with SH2 domain-containing proteins**

## Procedure

* Select query sample 138 (NXQ_00138)

* Use the term finder to get the accession code of *SH2* domain
* In the SPARQL query, change the line setting the constraint on the domain term

* Run the query => you should get 3 rows as a result

* Check the result you get by clicking one of the *(neXtProt link)* in the result:

  * see *Binary interactions* section of *Interactions* TAB: gold interactions are those without the silver tag: you should see at least 10 such interactions.
  
  * click on the gene name of one of the **interacting protein**, then navigate to its *Sequence* TAB and check for the presence of a SH2 domain

**Terms**

* SH3 [cv:DO-00615], SH2 [cv:DO-00614]

