### Prerequisites:

[Git](https://git-scm.com/downloads) + [JDK 8](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html) / [OpenJDK 8](http://openjdk.java.net/install/) + [Apache Maven](https://maven.apache.org/download.cgi)

#### 1. Cloning and building the main project ([anti-spam-weka-cli](https://github.com/marcelovca90/anti-spam-weka-cli)):

```
git clone https://github.com/marcelovca90/anti-spam-weka-cli ~/anti-spam-weka-cli/
cd ~/anti-spam-weka-cli/AntiSpamWekaCli/ && mvn clean install
```

#### 2. Cloning and extracting the data sets ([anti-spam-weka-data](https://github.com/marcelovca90/anti-spam-weka-data)):

```
git clone https://github.com/marcelovca90/anti-spam-weka-data ~/anti-spam-weka-data/
cd ~/anti-spam-weka-data/2017_BASE2/ && cat 2017_BASE2.zip.part-* > 2017_BASE2.zip && unzip 2017_BASE2.zip
cd ~/anti-spam-weka-data/2017_MULT10/ && cat 2017_MULT10.zip.part-* > 2017_MULT10.zip && unzip 2017_MULT10.zip
```

#### 4. [LINUX] Installing the high-performance linear algebra library ([netlib-java](https://github.com/fommil/netlib-java)):

```
sudo apt-get install libatlas3-base libopenblasbase
sudo update-alternatives --config libblas.so
sudo update-alternatives --config libblas.so.3
sudo update-alternatives --config liblapack.so
sudo update-alternatives --config liblapack.so.3
```

#### 5. Configuring and performing training and testing

```
cd ~/anti-spam-weka-cli/AntiSpamWekaCli/ && vi run.properties && ./run.sh
```
