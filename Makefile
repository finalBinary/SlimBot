JCC = javac

JFLAGS = -g -Xlint:unchecked

CF = /home/giovanni/Java/SlimBot
CF_T = /home/giovanni/Java/SlimBot
JF = /home/giovanni/Java/Jars
IJM = /InstaJsonManager
IJM_T = $(CF)/InstaJsonManager
IJM_S = $(CF)/InstaJsonManager


MySQLJar = $(JF)/mysql-connector-java-5.0.8-bin.jar
JsoupJar = $(JF)/jsoup.jar
GsonJar = $(JF)/gson-2.6.2.jar

$(CF)/testBot.class : $(CF)/testBot.java $(IJM_T)/JsonHandler.class $(IJM_T)/PicJson.class $(IJM_T)/ScrollJson.class $(IJM_T)/TagJson.class
	$(JCC) $(JFLAGS) -cp .:$(JsoupJar):$(GsonJar):$(IJM) testBot.java

$(IJM_T)/JsonHandler.class : $(IJM_T)/SimpleNode.class $(IJM_T)/SimpleJson.class $(IJM_S)/JsonHandler.java
	$(JCC) $(JFLAGS)  $(IJM_S)/JsonHandler.java

$(IJM_T)/PicJson.class : $(IJM_T)/SimpleNode.class $(IJM_T)/SimpleJson.class $(IJM_S)/PicJson.java
	$(JCC) $(JFLAGS)  $(IJM_S)/PicJson.java

$(IJM_T)/TagJson.class : $(IJM_T)/LikeNode.class $(IJM_T)/CommentNode.class $(IJM_S)/TagJson.java
	$(JCC) $(JFLAGS)  $(IJM_S)/TagJson.java

$(IJM_T)/ScrollJson.class : $(IJM_T)/SimpleNode.class $(IJM_T)/SimpleJson.class $(IJM_S)/ScrollJson.java
	$(JCC) $(JFLAGS)  $(IJM_S)/ScrollJson.java

$(IJM_T)/LikeNode.class : $(IJM_S)/LikeNode.java
	$(JCC) $(JFLAGS)  $(IJM_S)/LikeNode.java

$(IJM_T)/CommentNode.class : $(IJM_S)/CommentNode.java
	$(JCC) $(JFLAGS)  $(IJM_S)/CommentNode.java

$(IJM_T)/SimpleNode.class : $(IJM_S)/SimpleNode.java
	$(JCC) $(JFLAGS)  $(IJM_S)/SimpleNode.java

$(IJM_T)/SimpleJson.class : $(IJM_S)/SimpleJson.java
	$(JCC) $(JFLAGS)  $(IJM_S)/SimpleJson.java

#PageHandler.class : .java
#	$(JCC) $(JFLAGS) -cp .:$(CF)/PageParser:$(JsoupJar) PageParser/PageHandler.java

clean:
	rm $(CF)/testBot.class $(IJM_T)/JsonHandler.class $(IJM_T)/PicJason.class $(IJM_T)/SimpleNode.class $(IJM_T)/SimpleJson.class $(IJM_T)/ScrollJson.class $(IJM_T)/*.class
