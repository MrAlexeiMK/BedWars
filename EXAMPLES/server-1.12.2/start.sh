rm -R logs
rm -R world
cp -avr map world
screen -S bw4 -X quit
screen -S bw4 -m -d java -Xincgc -server -jar -Xms600m -Xmx600m spigot-1.16.2.jar
