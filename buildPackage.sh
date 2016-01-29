#!/bin/bash

PKGNAME=MaizePkg.zip

FILES=$(find . -type f -printf "%p\n" | grep -v "\\.java" | grep -v ".git*\|.idea\|Presentation\|Makefile\|.sh\|Maize.mf" )

rm -f maize.zip

for file in $FILES; do
	zip -g "$PKGNAME" "$file"
done

for file in $(find ./bots -type f -printf "%p\n"); do
	zip -g "$PKGNAME" "$file"
done

for file in $(find ./imgres -type f -printf "%p\n"); do
	zip -g "$PKGNAME" "$file"
done

zip -g "$PKGNAME" "runUI.sh"
zip -g "$PKGNAME" "runTest.sh"
