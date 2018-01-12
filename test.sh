#!/bin/bash

dir=$PWD

cd ~ubuntu/bc-scaffold

git reset --hard
git pull

mkdir Bot
mkdir replays

cp -r $PWD/Bot/src/* ./Bot/

alias python3='~ubuntu/.pyenv/versions/general/bin/python'
alias pip3='~ubuntu/.pyenv/versions/general/bin/pip'
./battlecode.sh -p1 examplefuncsplayer-java -p2 examplefuncsplayer-python -m testmap

cp -r replays $PWD/