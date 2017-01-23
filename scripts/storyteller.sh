JAVA_HOME="C:/ProgramData/Oracle/Java/javapath"
PYTHON_HOME="C:/Users/Maarten/AppData/Local/Programs/Python/Python36-32"
STORYTELLER_PATH="c:/Users/Maarten/workspace/StoryTeller"

LIB="$STORYTELLER_PATH"/target
DB="$STORYTELLER_PATH"/../query-builder-server/data/storyteller5.db
RESOURCES="$( cd $STORYTELLER_PATH && cd .. && pwd)"/vua-resources

SERVER="http://145.100.58.139:50053"
LIMIT="100"

ID=$1
QUERY=$2

echo "starting query to KnowledgeStore"
$JAVA_HOME/java -Xmx2000m -cp "$LIB/StoryTeller-v1.0-jar-with-dependencies.jar" vu.cltl.storyteller.json.JsonMakeStoryFromTripleData "$QUERY" --ks-limit $LIMIT --ks-service $SERVER --log --token-index "$STORYTELLER_PATH"/scripts/token.index.gz --eurovoc $RESOURCES/mapping_eurovoc_skos.label.concept.gz 1> $ID.json 2> log.err

echo "starting update_queries"
$PYTHON_HOME/python update_queries.py $DB $ID $ID.json 1>> log.out 2>> log.err
