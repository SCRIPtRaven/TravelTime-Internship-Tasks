# TravelTime-Internship-Tasks

To run this program from a terminal:
1. Navigate to the root directory of the project (located in the folder location-region)
2. Run the following command to build a jar file and clean any old remnants: 'sbt clean assembly'
   This generates a fat jar file per the instructions provided in build.sbt on its generation.
2. Run the following command: 'java -jar location-region-1.0.jar --regions=regions.json --locations=locations.json --output=results.json'
3. Parameters --regions and --locations can be given any other JSON file present in the root directory of the project

To compile and run the program:
1. Navigate to the root directory of the project (located in the folder location-region)
2. Run "sbt compile"
3. Run "sbt run"