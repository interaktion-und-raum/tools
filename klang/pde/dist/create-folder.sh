#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)
PROCESSING_LIB=$ROOT/../processing-library
DST=$PROCESSING_LIB/$LIB_NAME

if [ -d "$DST" ]; then
	rm -rf "$DST"
fi

if [ -d "$PROCESSING_LIB" ]; then
	rm -rf "$PROCESSING_LIB"
fi

mkdir -p "$DST"
