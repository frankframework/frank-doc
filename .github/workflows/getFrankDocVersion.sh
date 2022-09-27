#!/bin/bash

grep "frankDocVersion" ../../frank-doc-doclet/target/test-classes/build.properties | cut -d= -f2 > frankDocVersion.txt
echo $(cat frankDocVersion.txt)
