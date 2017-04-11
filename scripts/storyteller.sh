#!/bin/bash

LIB="/src/StoryTeller/target"
DATA_DIR="/data"
RESOURCES_DIR="/src/vua-resources"

SERVER="http://130.37.53.35:50053"

ID=$1
QUERY=$2
LIMIT=$3

echo "starting query to KnowledgeStore"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" \
vu.cltl.storyteller.json.JsonMakeStoryFromTripleData $QUERY \
--ks-limit $LIMIT \
--ks-service $SERVER \
--log \
--token-index $DATA_DIR/token.index.gz \
--resource-dir $RESOURCE_DIR \
1> $DATA_DIR/$ID.json \
2> $DATA_DIR/log.err

echo "starting update_queries"
python /src/query-builder-daemon/scripts/update_queries.py $DATA_DIR/storyteller.db $ID $DATA_DIR/$ID.json 1>> $DATA_DIR/log.out 2>> $DATA_DIR/log.err
