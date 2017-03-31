FROM nlescstoryteller/storyteller

VOLUME /data

COPY . /src/query-builder-daemon

WORKDIR /src/query-builder-daemon/scripts/
RUN chmod +x *.sh
RUN chmod +x *.py

WORKDIR /src/query-builder-daemon
RUN chmod +x gradlew
RUN sync
RUN ./gradlew installDist

WORKDIR /src/query-builder-daemon/build/install/query-builder-daemon/bin

EXPOSE 4567
CMD ["./query-builder-daemon"]
