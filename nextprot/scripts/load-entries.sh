
entries=$(grep -v "#" ./entries-to-add.txt | grep -v "^$" | sort -u )
for entry in $entries; do
  echo "building ttl for ${entry}"
  wget http://localhost:8080/nextprot-api-web//entry/${entry}.ttl -O ../ttl-data/${entry}.ttl
done

for entry in $entries; do
	echo "adding prefixes to file ${entry}.ttl"
	cat header.txt ../ttl-data/${entry}.ttl > ../ttl-data/${entry}.ttl.prefixed
	rm ../ttl-data/${entry}.ttl
	mv ../ttl-data/${entry}.ttl.prefixed ../ttl-data/${entry}.ttl
done

# error with NX_P59103