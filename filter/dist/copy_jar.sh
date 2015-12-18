#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)
SRC=/Users/dennisppaul/dev/tools/netbeans/tools/dist/tools.jar
DST=$ROOT/../processing-library/$LIB_NAME/library/

if [ -d "$DST" ]; then
	rm -rf "$DST"
fi
mkdir -p "$DST"

cp "$SRC" "$DST"
mv $DST/tools.jar $DST/$LIB_NAME.jar