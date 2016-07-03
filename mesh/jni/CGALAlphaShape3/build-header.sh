#! /bin/bash 

JAVA_SRC=CGALAlphaShape3

cd $(pwd)

javac de/hfkbremen/mesh/$JAVA_SRC.java
javah -jni de.hfkbremen.mesh.$JAVA_SRC
