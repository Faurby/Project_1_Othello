#!/usr/bin/env bash

HERE=$(dirname $(realpath $0))

cd $HERE

javac *.java
java Othello RandomAI OthelloAIDonathello
