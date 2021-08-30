rd /s /q "logs"
rd /s /q "world"
xcopy map world /D /E /C /R /H /I /K /Y
java -Dfile.encoding=UTF-8 -jar -Xms600m -Xmx600m spigot-1.8.8.jar
