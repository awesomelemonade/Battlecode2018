#!/bin/bash

SCRIPT=$(realpath ${0})
PRE="00000000"
SCAFF_DIR=~/bc18-scaffold
BOT_DIR=~/citricsky-battlecode2018
PREV_BOT_COMMIT=""
NUMGAMES=0
NUMWINS=0
MY_BOTS=("Bot")
BOTS=("SuperCowPowers" "QualifyingBot")

resetScaffold() {
    echo "<<<< Resetting Scaffold"
    cd ${SCAFF_DIR}
    git reset --hard
    git clean -fdx
    git pull
    for bot in ${BOTS[@]}; do
        echo "<<< Copying ${bot}"
        cp -rv ../$bot .
        echo ">>>"
    done
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
    for bot in ${MY_BOTS[@]}; do
        echo "<<<< Copying $bot"
        mkdir -p "${SCAFF_DIR}/${bot}"
        cp "${BOT_DIR}/${bot}/src/Player.java" "${SCAFF_DIR}/${bot}/"
        cp -r "${BOT_DIR}/${bot}/src/citricsky" "${SCAFF_DIR}/${bot}/"
        cd "${SCAFF_DIR}/${bot}"
        javac $(find . -name '*.java') -classpath ../battlecode/java
        echo "#!/bin/sh" > "${SCAFF_DIR}/${bot}/run.sh"
        echo "java -Xmx40m -classpath .:../battlecode/java Player" >> "${SCAFF_DIR}/${bot}/run.sh"
        echo ">>>>"
    done
}

getBotCommit() {
    cd ${BOT_DIR}
    git fetch &> /dev/null
    echo $(git log FETCH_HEAD --pretty=format:"%H" | head -n 1)
}

runGame() {
    cd ${SCAFF_DIR}

    GAME_ID=$(date +%F_%H-%M-%S)
    BOT=${1}
    OPPONENT=${2}
    MAP=${3}
    CMD="./battlecode.sh -p1 ${PRE}${BOT} -p2 ${PRE}${OPPONENT} -m ${MAP}"
    LOGFILE="${GAME_ID}.txt"
    echo "<<<< Running game against ${OPPONENT} on map ${MAP}"

    mv "${BOT}" "${PRE}${BOT}"
    mv "${OPPONENT}" "${PRE}${OPPONENT}"

    ssh ubuntu@ssh.pantherman594.com "sed -i '1i${PREV_BOT_COMMIT} ${GAME_ID} ${OPPONENT} ${MAP} Running' /var/www/pantherman594/battlecode/games/gamedata"
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
    ssh ubuntu@ssh.pantherman594.com "sed -i '1s/ Running/ ${RESULT}/' /var/www/pantherman594/battlecode/games/gamedata"
    echo ">>>>"

    echo "<<<< Cleaning up"
    rm "${LOGFILE}" "${GAME_ID}.bc18z"
    mv "${PRE}${BOT}" "${BOT}"
    mv "${PRE}${OPPONENT}" "${OPPONENT}"
    ssh ubuntu@ssh.pantherman594.com "cd /var/www/pantherman594/battlecode/games; ls -t | tail -n +101 | xargs rm --"
    echo ">>>>"
}

update() {
    if [[ $(getBotCommit) != ${PREV_BOT_COMMIT} ]]; then
        echo "<<<< Bot updated. Restarting script... >>>>"
        resetBot
        chmod +x "${SCRIPT}"
        exec "${SCRIPT}"
        exit 0
    fi
}

resetScaffold

resetBot
PREV_BOT_COMMIT=$(getBotCommit)
copyBot

ssh ubuntu@ssh.pantherman594.com "cd /var/www/pantherman594/tinyview; git pull"

while true; do
    cd ${SCAFF_DIR}
    for map in $(ls battlecode-maps | sort -R); do
        for bot in ${MY_BOTS[@]}; do
            for opponent in ${BOTS[@]}; do
                update
                runGame $bot $opponent $(echo "${map}" | cut -d'.' -f1)
                sleep 10s
            done
        done
    done
    for i in {1..360}; do
        update
        sleep 10s
    done
done
