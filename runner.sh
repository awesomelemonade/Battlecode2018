#!/bin/bash

SCRIPT=$(realpath ${0})
PRE="00000000"
SCAFF_DIR=~/bc18-scaffold
SCAFF_COM=""
BOT_DIR=~/citricsky-battlecode2018
BOT_COM=""
NUMGAMES=0
NUMWINS=0

resetScaffold() {
    echo "<<<< Resetting Scaffold"
    cd ${SCAFF_DIR}
    git reset --hard
    git clean -fdx
    git pull
    cp -r ../SuperCowPowers .
    mkdir replays
    sed -i '2 i\python3() {\n    ~ubuntu/.pyenv/versions/general/bin/python $@\n}\npip3() {\n    echo $@\n}' battlecode.sh
    echo ">>>>"
}

resetBot() {
    echo "<<<< Resetting Bot"
    cd ${BOT_DIR}
    git reset --hard
    git clean -fdx
    git pull
    echo ">>>>"
}

copyBot() {
    echo "<<<< Copying Bot"
    mkdir -p "${SCAFF_DIR}/Bot"
    cp "${BOT_DIR}/Bot/src/Player.java" "${SCAFF_DIR}/Bot/"
    cp -r "${BOT_DIR}/Bot/src/citricsky" "${SCAFF_DIR}/Bot/"
    cp "${SCAFF_DIR}/examplefuncsplayer-java/run.sh" "${SCAFF_DIR}/Bot/run.sh"
    echo ">>>>"
}

getScaffCommit() {
    cd ${SCAFF_DIR}
    echo $(git log | head -n 1 | cut -d' ' -f2)
}

getBotCommit() {
    cd ${BOT_DIR}
    echo $(git log | head -n 1 | cut -d' ' -f2)
}

runGame() {
    cd ${SCAFF_DIR}

    GAME_ID=$(date +%F_%H-%M-%S)
    OPPONENT=${1}
    MAP=${2}
    CMD="./battlecode.sh -p1 ${PRE}Bot -p2 ${PRE}${OPPONENT} -m ${MAP}"
    LOGFILE="${GAME_ID}.txt"
    echo "<<<< Running game against ${OPPONENT} on map ${MAP}"

    mv Bot ${PRE}Bot
    mv "${1}" "${PRE}${1}"

    ${CMD} | tee ${LOGFILE}
    echo ">>>>"

    echo "<<<< Recording results"
    WINNER=$(tail -1 ${LOGFILE} | cut -f4 -d' ')
    cd replays
    if [[ ${WINNER} == "1" ]]; then
        RESULT="Win"
    elif [[ ${WINNER} == "2" ]]; then
        RESULT="Loss"
    else
        RESULT="Inconclusive"
        touch replay_0.bc18
    fi
    gzip replay_0.bc18
    mv replay_0.bc18.gz ../${GAME_ID}.bc18z
    cd ..

    scp "${GAME_ID}.bc18z" "ubuntu@ssh.pantherman594.com:/var/www/pantherman594/battlecode/games/"
    scp "${LOGFILE}" "ubuntu@ssh.pantherman594.com:/var/www/pantherman594/battlecode/games/"
    ssh ubuntu@ssh.pantherman594.com "sed -i '1 i${BOT_COM} ${GAME_ID} ${OPPONENT} ${MAP} ${RESULT}' /var/www/pantherman594/battlecode/games/gamedata"
    echo ">>>>"
    echo "<<<< Cleaning up"
    rm "${LOGFILE}" "${GAME_ID}.bc18z"
    mv "${PRE}Bot" "Bot"
    mv "${PRE}${OPPONENT}" "${OPPONENT}"
    echo ">>>>"
}

resetScaffold
SCAFF_COM=$(getScaffCommit)

resetBot
BOT_COM=$(getBotCommit)
copyBot

ssh ubuntu@ssh.pantherman594.com "cd /var/www/pantherman594/tinyview; git pull"

while true; do
    cd ${SCAFF_DIR}
    for map in $(ls battlecode-maps | sort -R); do
        if [[ $(getBotCommit) != ${BOT_COM} ]]; then
            echo "<<<< Bot updated. Restarting script... >>>>"
            chmod +x "${SCRIPT}"
            exec "${SCRIPT}"
            break
        fi
        runGame SuperCowPowers $(echo "${map}" | cut -d'.' -f1)
    done
done
