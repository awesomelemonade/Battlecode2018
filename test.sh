#!/bin/bash

BUILD_NUMBER="$1"
BRANCH_NAME="$2"
GIT_PREVIOUS_SUCCESSFUL_COMMIT="$3"

NUMWINS=0
NUMGAMES=0

MAPS=("socket" "bananas")
BOTS=("examplefuncsplayer-python")

rungame() {
    CMD="./battlecode.sh -p1 Bot $@"
    LOGFILE="log_${NUMGAMES}"

    echo ${CMD}
    #ulimit -v 256000
    cpulimit -l 40 -z -i ${CMD} | tee ${LOGFILE}
    WINNER=$(tail -1 ${LOGFILE} | cut -f4 -d' ' )
    sed -i "1 i\$CMD"
    if [[ ${WINNER} == "2" ]]; then
        NUMWINS=$(( NUMWINS + 1 ))
        mv ${LOGFILE} log_${NUMGAMES}_W.txt
        mv replays/replay_${NUMGAMES}.bc18 replays/replay_${NUMGAMES}_W.bc18
    else
        mv ${LOGFILE} log_${NUMGAMES}_L.txt
        mv replays/replay_${NUMGAMES}.bc18 replays/replay_${NUMGAMES}_L.bc18
    fi
    NUMGAMES=$(( NUMGAMES + 1 ))
}

urlencode() {
    # urlencode <string>
    old_lc_collate=$LC_COLLATE
    LC_COLLATE=C

    local length="${#1}"
    for (( i = 0; i < length; i++ )); do
        local c="${1:i:1}"
        case ${c} in
            [a-zA-Z0-9.~_-]) printf "$c" ;;
            *) printf '%%%02X' "'$c" ;;
        esac
    done

    LC_COLLATE=${old_lc_collate}
}

DIR=$PWD
SCAFFOLD_DIR="~ubuntu/bc18-scaffold"

cd "${SCAFFOLD_DIR}"

git reset --hard
git clean -fdx
git pull

RUN_SCRIPT="$(cat examplefuncsplayer-java/run.sh)"
mkdir -p replays

cd ${DIR}

git reset --hard
git clean -fdx

# Previous successful bot
mkdir -p "${SCAFFOLD_DIR}/Bot_prev"

git checkout ${GIT_PREVIOUS_SUCCESSFUL_COMMIT}
cp -r Bot/src/* "${SCAFFOLD_DIR}/Bot_prev/"
echo RUN_SCRIPT > "${SCAFFOLD_DIR}/Bot_prev/run.sh"
BOTS+=("Bot_prev")

git checkout ${BRANCH_NAME}

# Master branch bot, if not on master branch
if [[ ${BRANCH_NAME} != "master" ]]; then
    mkdir -p "${SCAFFOLD_DIR}/Bot_master"

    git checkout master
    cp -r Bot/src/* "${SCAFFOLD_DIR}/Bot_master/"
    echo RUN_SCRIPT > "${SCAFFOLD_DIR}/Bot_master/run.sh"
    BOTS+=("Bot_master")

    git checkout ${BRANCH_NAME}
fi

# Copy the current bot
mkdir -p "${SCAFFOLD_DIR}/Bot"
cp -r Bot/src/* "${SCAFFOLD_DIR}/Bot/"
echo RUN_SCRIPT > "${SCAFFOLD_DIR}/Bot/run.sh"

sed -i '2 i\python3() {\n    ~ubuntu/.pyenv/versions/general/bin/python $@\n}\npip3() {\n    ~ubuntu/.pyenv/versions/general/bin/pip $@\n}' battlecode.sh

for bot in ${BOTS}; do
    for map in ${MAPS}; do
        rungame -p2 ${bot} -m ${map}
    done
done

mv log_* replays/
cp -r replays ${DIR}/

cd ${DIR}/replays
echo "<ul>" > links.html
for i in *.bc18; do
    NAME="${i/replay_/${BUILD_NUMBER}_}"
    scp "$i" "ubuntu@ssh.pantherman594.com:/var/www/pantherman594/replays/${NAME}"
    echo "<li><a href=\"https://pantherman594.com/tinyview/?fname=$(urlencode /replays/${NAME})\">Replay ${NAME}</a></li>" >> links.html
done
echo "</ul>" >> links.html

ssh ubuntu@ssh.pantherman594.com "cd /var/www/pantherman594/tinyview; git pull"

touch "$NUMWINS of $NUMGAMES games won"

if [[ ! ${NUMWINS} > $(( NUMGAMES / 2 )) ]]; then
    exit 1
fi
exit 0
