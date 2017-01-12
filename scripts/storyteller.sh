ROOT=/home/jason/Workspace/StoryTeller
LIB="$ROOT"/target
RESOURCES="$( cd $ROOT && cd .. && pwd)"/vua-resources

SERVER="http://145.100.58.139:50053"
LIMIT="100"

ID=$1
QUERY=$2

java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  $QUERY --ks-limit $LIMIT --ks-service $SERVER --log --token-index token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > $ID.json
