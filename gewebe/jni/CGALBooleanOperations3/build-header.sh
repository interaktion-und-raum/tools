#! /bin/bash 

JAVA_SRC=CGALBooleanOperations3

cd $(pwd)

javac de/hfkbremen/gewebe/$JAVA_SRC.java
javah -jni de.hfkbremen.gewebe.$JAVA_SRC