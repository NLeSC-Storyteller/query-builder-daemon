#!/bin/bash

DATA_DIR="/data"

echo "starting clear_all_queries"
cd DATA_DIR
rm *.json

python /src/query-builder-daemon/scripts/clear_all_queries.py $DATA_DIR/storyteller.db 1>> $DATA_DIR/log.out 2>> $DATA_DIR/log.err
