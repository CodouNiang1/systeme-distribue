#!/bin/bash
ROOT=/mnt/c/Users/Codou/OneDrive/systeme-distribue

java \
  -Dorg.omg.CORBA.ORBClass=com.sun.corba.ee.impl.orb.ORBImpl \
  -Dorg.omg.CORBA.ORBSingletonClass=com.sun.corba.ee.impl.orb.ORBSingleton \
  -Dorg.omg.CORBA.ORBInitialHost=localhost \
  -Dorg.omg.CORBA.ORBInitialPort=1050 \
  -jar $ROOT/pdf-server/target/pdf-server-1.0.0-jar-with-dependencies.jar
