#!/bin/bash
source=$1 # source directory
target=$2 # target directory
input=$3 # paired filelist
outputDir=$4 # output directory
tempDir=$outputDir/temp
SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

echo $(date)
mkdir -p $tempDir
while IFS= read -r line
do
	sFile=$source/$(echo "$line" | cut -f1)
	tFile=$target/$(echo "$line" | cut -f2)
	echo "$sFile"
	# pull out the English from the parallel corpus
	cut -f1 $sFile > $tempDir/en1
	cut -f1 $tFile > $tempDir/en2
	echo "Extracted source"

	# dedup
	sort -T . -S 4G $tempDir/en1 | uniq > $tempDir/en1.dedup
	sort -T . -S 4G $tempDir/en2 | uniq > $tempDir/en2.dedup
	echo "Removed duplicates"

	# join the two files and check if they contain shared sentences
	cat $tempDir/en1.dedup $tempDir/en2.dedup | sort  -T . -S 4G | uniq -c > $tempDir/out
	cat $tempDir/out | awk '{$1=$1;print}' | grep -E '^2' | sed -E 's/^2 //g' | grep -P '[\p{Latin}]' > $tempDir/keys.txt
	echo "Extracted shared sentences"

	# Match source and target, pivoting from EN (10G memory should be enough)
	java -Xmx10G -Dfile.encoding=UTF8 -jar $SCRIPTPATH/extract-corpus-pivot.jar $sFile $tFile $tempDir/keys.txt > $outputDir/$(echo "$line" | cut -f1)
	echo "Extracted $(echo "$line" | cut -f1)"
done < "$input"
#clean
rm -rf $tempDir
echo "DONE"
echo $(date)