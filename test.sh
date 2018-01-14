#!/bin/bash

NUMWINS=0
NUMGAMES=0

function rungame() {
    CMD="./battlecode.sh -p1 Bot $@"
    LOGFILE="log_${NUMGAMES}"

    echo $CMD
    #ulimit -v 256000
    cpulimit -l 40 -z -i $CMD | tee $LOGFILE
    WINNER=$(tail -1 $LOGFILE | cut -f4 -d' ' )
    sed -i "1 i\$CMD"
    if [[ $WINNER == "2" ]]; then
        NUMWINS=$(( NUMWINS + 1 ))
        mv $LOGFILE log_${NUMGAMES}_W.txt
        mv replays/replay_${NUMGAMES}.bc18 replays/replay_${NUMGAMES}_W.bc18
    else
        mv $LOGFILE log_${NUMGAMES}_L.txt
        mv replays/replay_${NUMGAMES}.bc18 replays/replay_${NUMGAMES}_L.bc18
    fi
    NUMGAMES=$(( NUMGAMES + 1 ))
}

dir=$PWD

git reset --hard
git clean -fdx

cd ~ubuntu/bc18-scaffold

git reset --hard
git clean -fdx
git pull

mkdir -p Bot
mkdir -p replays

cp -r $dir/Bot/src/* ./Bot/
cp examplefuncsplayer-java/run.sh ./Bot/

sed -i '2 i\python3() {\n    ~ubuntu/.pyenv/versions/general/bin/python $@\n}\npip3() {\n    ~ubuntu/.pyenv/versions/general/bin/pip $@\n}' battlecode.sh

rungame -p2 examplefuncsplayer-python -m socket
rungame -p2 examplefuncsplayer-python -m bananas

mv log_* replays/
cp -r replays $dir/

cd $dir/replays
mkdir toCopy
for i in *bc18; do
    NAME="${i/replay_/${1}_/}"
    scp "$i" "ubuntu@ssh.pantherman594.com:/var/www/replays/${NAME}"
    echo "<a href=\"${NAME}\">Replay ${NAME}</a>" >> links.html
done

touch "$NUMWINS of $NUMGAMES games won"

if [[ ! $NUMWINS > $(( NUMGAMES / 2 )) ]]; then
    exit 1
fi
exit 0
