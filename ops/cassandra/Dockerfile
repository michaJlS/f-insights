FROM cassandra:3.11
ENV CASSANDRA_CLUSTER_NAME=FlickrAssistant
RUN sed -i 's/^authenticator.\+$/authenticator: PasswordAuthenticator/g' /etc/cassandra/cassandra.yaml