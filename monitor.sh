#!/bin/bash

while [[ $(free -m | head -2 | tail -1 | rev | cut -d' ' -f1 | rev) > 100 ]]; do
    sleep 1
done
