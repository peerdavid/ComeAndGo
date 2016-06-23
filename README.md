<img src="https://github.com/peerdavid/ComeAndGo/blob/master/documentation/logo.jpg" width="120"/>

# Come & Go

## Introduction:

Come & Go is a timemanagementsystem for small and middle sized companies (100 users) with different user roles, such as boss, admins, personell manager or employees. The idea of this project was, to create a small software from scratch, to learn the process, how to create a clean software architecture. The process we used is developed by <a href="http://www.iese.fraunhofer.de/de/competencies/architecture/architekturkonstruktion.html">Fraunhofer-Institut für Experimentelles Software Engineering IESE</a>.
<br><br>
The requirements of Come & Go are written by the University of Innsbruck during the SE&PM lecture.

## Run Come & Go:
The container is available on dockerhub. So the only thing you need is docker installed and an internet connection.
To run and test Come & Go, you can simply execute the following command on your linux machine:
```
docker run -i -t -p 9000:9000 comeandgo/comeandgo:1.0
```

## Software Architecture
The architecture is available in /architecture/architecture.vpp. To open this file you can install <a href="https://www.visual-paradigm.com/">Visual Paradigm</a> (Free Community Edition).

There you can find the specifications and our scenarios. The scenarios also contains informations of decisions we made and decisions we discarded. You can also find how we increased our level of confidence to ensure, that the design is implementable.
Last but not least we ensured, that the implementation is exactly what we designed. For that we created a small cmd tool which creats a class diagram of our code (using google guice GraphvizGrapher). So we can compare the real architecture and the designed architecture.


## Web
The web implementation can be found in /web/*.

## Android
The android client is a prototype to show, how an external system could access and interact with Come&Go.

## Documentation
The developers of come & go used the following documentation during the development: <a href=https://github.com/peerdavid/ComeAndGo/wiki>Documentation</a>.


## Team
### Frontend Development
Brunner Martin (Frontend Developer)<br>
Haas Leonhard (Frontend Developer)<br>
Waldhart Sebastian (Frontend Developer)<br>

### Backend Development
Haberl Stefan (Backend Developer)<br>
Summerer Patrik (Backend Developer)<br>

### Software Architecture
Peer David (Software Architekt)<br>

