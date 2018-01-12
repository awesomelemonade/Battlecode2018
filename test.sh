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
bash ./battlecode.sh -p1 Bot -p2 examplefuncsplayer-python -m testmap

cp -r replays $dir/
