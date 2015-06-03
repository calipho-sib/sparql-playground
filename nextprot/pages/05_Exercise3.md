#Exercise 3

Build a SPARQL query to find:

* **Proteins with a variant in a homeobox region**

## Procedure

* Select query sample 99 (NXQ_00099)

* Replace the line about ptm with 
```
?iso :region ?region .
```

* Use the term finder to get the accession code of *homeobox* domain

* Aadd a line setting the constraint on homeobox: 
```
?region :term <accession for homeobox> .

```
* Add the following lines to ensure the variant position is within the homeobox region: 
```
?region :start ?start .
?region :end ?end .
filter (?varpos > ?start && ?varpos <= ?end )
```

* Run the query => you should get 10 rows as a result

* Check the result you get by clicking one of the *(neXtProt link)* in the result:
  * see feature viewer of *Sequence* TAB



**Terms**

* Homeobox[cv:DO-00312]
