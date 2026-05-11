#!/bin/bash
JAVAFX_PATH=~/.m2/repository/org/openjfx
ROOT=/mnt/c/Users/Codou/OneDrive/systeme-distribue

java \
  --module-path \
  $JAVAFX_PATH/javafx-controls/17.0.6/javafx-controls-17.0.6-linux.jar:\
$JAVAFX_PATH/javafx-graphics/17.0.6/javafx-graphics-17.0.6-linux.jar:\
$JAVAFX_PATH/javafx-base/17.0.6/javafx-base-17.0.6-linux.jar:\
$JAVAFX_PATH/javafx-fxml/17.0.6/javafx-fxml-17.0.6-linux.jar \
  --add-modules javafx.controls,javafx.fxml \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  -Dorg.omg.CORBA.ORBClass=com.sun.corba.ee.impl.orb.ORBImpl \
  -Dorg.omg.CORBA.ORBSingletonClass=com.sun.corba.ee.impl.orb.ORBSingleton \
  -Dorg.omg.CORBA.ORBInitialHost=localhost \
  -Dorg.omg.CORBA.ORBInitialPort=1050 \
  -cp $ROOT/pdf-client-gui/target/pdf-client-gui-1.0.0-jar-with-dependencies.jar \
  gui.MainApp
