#!/bin/bash

dir=$PWD

cd ~ubuntu/bc18-scaffold

git reset --hard
git clean -fdx
git pull

mkdir -p Bot
mkdir -p replays

cp -r $dir/Bot/src/* ./Bot/
cp examplefuncsplayer-java/run.sh ./Bot/

sed -i '2 i\python3() {\n    ~ubuntu/.pyenv/versions/general/bin/python $@\n}\npip3() {\n    ~ubuntu/.pyenv/versions/general/bin/pip $@\n}' battlecode.sh
bash ./battlecode.sh -p1 Bot -p2 examplefuncsplayer-python -m testmap

cp -r replays $dir/
