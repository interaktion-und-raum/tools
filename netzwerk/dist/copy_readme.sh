#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

SRC=$ROOT/../README.md
DST=$ROOT/../processing-library/$LIB_NAME

cp "$SRC" "$DST"

SRC=$ROOT/../graphics/*.jpg
DST=$ROOT/../processing-library/$LIB_NAME/graphics

mkdir "$DST"
cp $SRC "$DST"
