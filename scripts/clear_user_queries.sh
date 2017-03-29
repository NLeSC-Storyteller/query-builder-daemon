#!/bin/bash

DATA_DIR="/data"

USERNAME=$1

echo "starting clear_user_queries"

python /src/query-builder-daemon/scripts/clear_user_queries.py $DATA_DIR/storyteller.db $USERNAME 1>> $DATA_DIR/log.out 2>> $DATA_DIR/log.err
