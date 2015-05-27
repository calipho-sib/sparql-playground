#Exercise 4

Build a SPARQL query to find:

* **Proteins with a lipidation site having a variant on it**

## Procedure

* Select query sample 99 (NXQ_00099)

The query shows a single constraint on the PTM annotation: its start position. 
We need to add a second constraint on it to select lipidation sites only. 
So we first have to make the PTM annotation an explicit variable (i.e. ?ptm):

* Remove the line:
```
?iso :ptm /:start ?varpos.
```

* Add the lines:
```
?iso :ptm  ?ptm .
?ptm  :start ?varpos .
```

The modified version is equivalent to the original one, except that now we have a hook (the ?ptm variable) to set the PTM type

* In the Help page, search for *Isoform* (CTRL-F)

* Click on the Isoform link in the list of classes (left panel), then search *:ptm* in the page. You’ll see the list of PTM types in the right panel

* Copy the :LipidationSite and add the following line to the query:
```
?ptm a :LipidationSite .
```
* Run the query

* Check the result you get by clicking one of the (neXtProt link) in the result:
  * see feature viewer in of Sequence TAB
  * check that you find at least one variant at the position of a lipidation site

**Optionally**
To get non transmembrane proteins with a variant on a lipidation site:
add the line:		 
```
filter not exists {?iso :transmembraneRegion ?_}
```
