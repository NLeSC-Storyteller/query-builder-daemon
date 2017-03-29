#!/bin/bash

DATA_DIR="/data"

rm $DATA_DIR/storyteller.db

cd /src/query-builder-preprocessing
./run_docker_stack.sh $DATA_DIR
