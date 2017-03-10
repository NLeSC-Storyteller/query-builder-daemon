#!/bin/bash

LIB="/src/StoryTeller/target"
DATA_DIR="/data"
RESOURCES_DIR="/src/vua-resources"

SERVER="http://130.37.53.35:50053"
LIMIT="100"

ID=$1
QUERY=$2

echo "starting query to KnowledgeStore"
java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData "$QUERY" --ks-limit $LIMIT --ks-service $SERVER --log --token-index "$DATA_DIR/token.index.gz" --eurovoc "$RESOURCE_DIR/mapping_eurovoc_skos.label.concept.gz" 1> "$DATA_DIR/$ID.json" 2> "$DATA_DIR/log.err"

echo "starting update_queries"
python update_queries.py "$DATA_DIR/storyteller.db" $ID "$DATA_DIR/$ID.json" 1>> "$DATA_DIR/log.out" 2>> "$DATA_DIR/log.err"
