#!/bin/bash

VERSION=
PKGNAME=Maize

STAGING=$(mktemp -d)
BOTS=$(find . -type f -printf "%p\n" | grep "^./bots/.*\.java")

# Make the project
echo "Making project..."
make clean
make

# Create staging dir
mkdir "$STAGING/$PKGNAME"


cp -v Maize.jar maize.cfg "$STAGING/$PKGNAME"
cp -rv lib imgres "$STAGING/$PKGNAME"
for file in $BOTS; do
    cp --parents -v "$file" "$STAGING/$PKGNAME"
done

# Move, zip, move back (better way of doing this, fo' sho)
echo Zipping $STAGING/$PKGNAME from $STAGING...
(cd "$STAGING" && zip -r - "$PKGNAME") > "$PKGNAME.zip"
echo Package left in $PKGNAME.zip

