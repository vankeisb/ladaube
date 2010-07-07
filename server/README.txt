LaDaube server module
---------------------

The web front-end is implemented in Java/Groovy/Stripes and relies on a couchdb database.

to compile and build the war :
  mvn clean install

The couchdb folder contains a couchapp that can be used to push the initial DB into a couchdb server.

The details for couchdb connection are :
host : localhost
port : 5984
db : ladaube-couch

See ladaube-couch and push.sh.