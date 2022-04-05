#!/bin/bash

grep "frankDocVersion" frank-doc/target/test-classes/build.properties | cut -d= -f2 > frankDocVersion.txt
echo $(cat frankDocVersion.txt)