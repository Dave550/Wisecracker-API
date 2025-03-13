# Weather/Hurricane Forecast CSCI-602
![img.png](img.png)

## Objectives
* To find the temperature in the area of Professor Raven's Weather Station
* To find the forecast of any city in the United States
* To find out if there are hurricane winds in the area
* To find out if the temperatures are dangerous in the area

## Overview
This Weather API was created to provide a one-stop shop for weather information.
This API has 4 endpoints. The 1st endpoint can be used to figure out the current conditions of Dr. Raven's personal weather station.
The 2nd endpoint can be used to find any city's forecast in the United States.
The endpoint will produce a 7-day forecast. The 3rd endpoint can be used to determine if there is a hurricane in the area and if so, what category hurricane.
The 4th endpoint can be used to find out current temperature information in the inputted location.

## Environment Setup

1.) Install Java JDK 11+. JDK located [here](https://openjdk.java.net/install/). If you have a Mac you can use `brew`.

```bash
brew install java
```
2.) Clone down the repository from GitHub

```bash
git clone https://github.com/CitadelCS/whirling-wisecrackers.git
```
3.) Build the project

> Disclaimer: If running on a Windows machine replace `./mvnw` with `mvnw.cmd`

```bash
./mvnw clean install
```

You should see a success if everything is set up correctly.

4.) Run the API

```bash
./mvnw spring-boot:run
```

Access the API by visiting [http://localhost:5001/weather/](http://localhost:5001/weather/). From there you can hit the endpoints directly.

6.) Success!

## Sample Output

http://localhost:5001/weather/NC/Charlotte/
```
[{"temperature":39,"forecast":"A slight chance of rain showers before 1am. 
Mostly cloudy, with a low around 39. West southwest wind around 8 mph. 
Chance of precipitation is 20%. New rainfall amounts less than a tenth of an inch possible.",
"windDirection":"WSW","windSpeed":"8 mph","timeFrame":"Tonight"},...]
```

http://localhost:5001/weather/NC/Charlotte/hurricane
```
{"category":0,"status":"No Hurricane Force Winds"}
```

http://localhost:5001/weather/station
```
{"temperature":"44.1"}
```

http://localhost:5001/weather/NC/Charlotte/temperature
```
{"temperature":"30F","warning":"Freezing Cold Warning"}
```
