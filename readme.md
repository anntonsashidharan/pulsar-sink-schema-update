


bin/pulsar-admin --admin-url http://localhost:8080   sinks localrun --archive /home/anton/projects/Verizon/sink1/target/sink1-1.0-SNAPSHOT-jar-with-dependencies.jar   --tenant public --namespace default    --topics-pattern '.*'   --parallelism 1 --auto-ack true --name customSinkConnectorTest7
