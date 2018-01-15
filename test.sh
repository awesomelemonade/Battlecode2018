#!/bin/bash

KILLPID=0

killexisting() {
    # Kill previous instances
    pkill -x "/home/ubuntu/.pyenv/versions/general/bin/python"
    pkill -fx "python3 run.py"
    pkill -fx "java -classpath .:../battlecode/java Player"
}

killmonitor() {
    bash "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/monitor.sh"
    killexisting
    killmonitorid
}

killmonitorid() {
    killmonitor &
    KILLPID=$BASHPID
}

#killmonitorid
killexisting

BUILD_NUMBER="${1}"
GIT_BRANCH=$(echo ${2} | cut -f2 -d'/')
GIT_PREVIOUS_SUCCESSFUL_COMMIT="${3}"

NUMWINS=0
NUMGAMES=0

MAPS=("socket" "bananas" "julia")
BOTS=("examplefuncsplayer-python")f

P_ENEMIES=()
P_RESULTS=()
P_MAPS=()

DIR=$PWD
SCAFFOLD_DIR=~ubuntu/bc18-scaffold

rungame() {
    GAME_ID=${NUMGAMES}
    NUMGAMES=$(( NUMGAMES + 1 ))
    CMD="./battlecode.sh -p1 Bot -p2 $1 -m $2"
    LOGFILE="${DIR}/logs/log_${GAME_ID}"

    echo ">>>> Run CMD: ${CMD}"
    #ulimit -v 256000
    ${CMD} | tee ${LOGFILE}
    WINNER=$(tail -1 ${LOGFILE} | cut -f4 -d' ')
    sed -i "1 i\ ${CMD}" ${LOGFILE}
    if [[ ${WINNER} == "2" ]]; then
        NUMWINS=$(( NUMWINS + 1 ))
        P_RESULTS[GAME_ID]="Win"
    else
        P_RESULTS[GAME_ID]="Loss"
    fi
    P_ENEMIES[GAME_ID]=$1
    P_MAPS[GAME_ID]=$2
}

urlencode() {
    # urlencode <string>
    old_lc_collate=${LC_COLLATE}
    LC_COLLATE=C

    local length="${#1}"
    for (( i = 0; i < length; i++ )); do
        local c="${1:i:1}"
        case ${c} in
            [a-zA-Z0-9.~_-]) printf "${c}" ;;
            *) printf '%%%02X' "'${c}" ;;
        esac
    done

    LC_COLLATE=${old_lc_collate}
}

cd "${SCAFFOLD_DIR}"

git reset --hard
git clean -fdx
git pull

RUN_SCRIPT=$(cat examplefuncsplayer-java/run.sh)
mkdir -p replays

cd "${DIR}"

git reset --hard
git clean -fdx
git checkout dev-main
git pull --all

echo ">>>> Copying previous successful bot"
mkdir -p "${SCAFFOLD_DIR}/Bot_prev"

git checkout ${GIT_PREVIOUS_SUCCESSFUL_COMMIT}
cp -r Bot/src/* "${SCAFFOLD_DIR}/Bot_prev/"
echo "${RUN_SCRIPT}" > "${SCAFFOLD_DIR}/Bot_prev/run.sh"
BOTS+=("Bot_prev")

git checkout ${GIT_BRANCH}

if [[ ${GIT_BRANCH} != "master" ]]; then
    echo ">>>> Not on master branch: copying master bot"
    mkdir -p "${SCAFFOLD_DIR}/Bot_master"

    git checkout master
    cp -r Bot/src/* "${SCAFFOLD_DIR}/Bot_master/"
    echo "${RUN_SCRIPT}" > "${SCAFFOLD_DIR}/Bot_master/run.sh"
    BOTS+=("Bot_master")

    git checkout ${GIT_BRANCH}
fi

echo ">>>> Copying current bot"
mkdir -p "${SCAFFOLD_DIR}/Bot"
cp -r Bot/src/* "${SCAFFOLD_DIR}/Bot/"
echo "${RUN_SCRIPT}" > "${SCAFFOLD_DIR}/Bot/run.sh"

cd "${SCAFFOLD_DIR}"

sed -i '2 i\python3() {\n    ~ubuntu/.pyenv/versions/general/bin/python $@\n}\npip3() {\n    echo $@\n}' battlecode.sh
mkdir -p "${DIR}/logs"

for bot in ${BOTS[@]}; do
    for map in ${MAPS[@]}; do
        rungame ${bot} ${map}
    done
done
#kill ${KILLPID}

cp -r replays "${DIR}/"

cd "${DIR}/replays"
echo "<!DOCTYPE html>
<head>
<meta charset=\"UTF-8\">
<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">
<body>
<h3>Build ${BUILD_NUMBER}: Won ${NUMWINS} of ${NUMGAMES} games</h3>
<ul>" > ../links.html
for i in $(seq 0 $(( NUMGAMES - 1 ))); do
    scp "replay_${i}.bc18" "ubuntu@ssh.pantherman594.com:/var/www/pantherman594/replays/${BUILD_NUMBER}_${i}"
    echo "<li>Bot vs. ${P_ENEMIES[i]} on map ${P_MAPS[i]} (${P_RESULTS[i]}): <a href=\"https://pantherman594.com/tinyview/?fname=$(urlencode /replays/${BUILD_NUMBER}_${i})\">replay</a> <a href=\"https://ci.pantherman594.com/job/CitricSky-Battlecode2018/${BUILD_NUMBER}/artifact/logs/log_${i}.txt\">log</a></li>" >> ../links.html
done
echo "</ul>
</body>" >> ../links.html

ssh ubuntu@ssh.pantherman594.com "cd /var/www/pantherman594/tinyview; git pull"

touch ../"Won ${NUMWINS} of ${NUMGAMES} games"

if [[ ! ${NUMWINS} > $(( NUMGAMES / 2 )) ]]; then
    exit 1
fi
exit 0
