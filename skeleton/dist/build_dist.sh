#!/bin/sh

source config.build

ROOT=$(pwd)
C0=$(tput sgr0)
C1=$(tput setaf $(expr $BASE_COLOR + 72))
C2=$(tput setaf $BASE_COLOR)

printJob()
{
	echo ""
	echo $C2"#########################################"
	echo $C2"# "$C1$1
	echo $C2"#########################################"
	echo $C0
}

printJob "create folder"
sh $ROOT/create-folder.sh $LIB_NAME
printJob "copying jar"
sh $ROOT/copy_jar.sh $LIB_NAME
printJob "copying extra libs"
for i in ${EXTRA_LIBS[@]}; do
	sh $ROOT/copy_extra_libs.sh $LIB_NAME $i
done
printJob "copying src"
sh $ROOT/copy_src.sh $LIB_NAME
printJob "copying README"
sh $ROOT/copy_readme.sh $LIB_NAME
printJob "creating processing sketches"
for i in ${IO_EXAMPLE_PATHS[@]}; do
	sh $ROOT/create-processing-sketches.sh $LIB_NAME $i
done
printJob "packing zip"
sh $ROOT/pack-zip.sh $LIB_NAME
printJob "done"
