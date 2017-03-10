FROM nlescstoryteller/storyteller

VOLUME /data

WORKDIR /src/query-builder-daemon/build/install/query-builder-daemon/
COPY scripts/storyteller.sh .
RUN chmod +x storyteller.sh
RUN chmod +x update_queries.py

WORKDIR /src/query-builder-daemon/build/install/query-builder-daemon/bin
CMD ["./query-builder-daemon"]
