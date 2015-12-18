#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)
DST=$ROOT/../processing-library/$LIB_NAME
SRC=../src

if [ -d "$DST/src" ]; then
	rm -rf "$DST/src"
fi
mkdir -p "$DST"

cp -r "$SRC" "$DST"
