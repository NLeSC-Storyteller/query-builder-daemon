JAVA_HOME="C:/ProgramData/Oracle/Java/javapath"
PYTHON_HOME="C:/Users/Maarten/AppData/Local/Programs/Python/Python36-32"

STORYTELLER_PATH="c:/Users/Maarten/workspace/StoryTeller"
LIB="$STORYTELLER_PATH"/target
DB="$STORYTELLER_PATH"/../query-builder-server/data/storyteller4.db
RESOURCES="$( cd $STORYTELLER_PATH && cd .. && pwd)"/vua-resources

SERVER="http://145.100.58.139:50053"
LIMIT="100"

ID=$1
QUERY=$2

$JAVA_HOME/java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData  "$QUERY" --ks-limit $LIMIT --ks-service $SERVER --log --token-index "$STORYTELLER_PATH"/scripts/token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz > $ID.json 2> error.log

echo $DB
$PYTHON_HOME/python update_queries.py $DB $ID $ID.json
