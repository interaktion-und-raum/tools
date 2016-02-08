#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

SRC=$ROOT/../lib/$LIB_NAME.jar
DST=$ROOT/../processing-library/$LIB_NAME/library/

if [ -d "$DST" ]; then
	rm -rf "$DST"
fi
mkdir -p "$DST"

cp "$SRC" "$DST"

SRC_LIBS=$ROOT/../lib/
cp "$SRC_LIBS/controlP5.jar" "$DST"
cp "$SRC_LIBS/jsyn-beta-16.6.4.jar" "$DST"
cp "$SRC_LIBS/rwmidi.jar" "$DST"
