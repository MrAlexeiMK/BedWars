rd /s /q "logs"
rd /s /q "world"
xcopy map world /D /E /C /R /H /I /K /Y
java -jar -Xms600m -Xmx600m spigot-1.17.1.jar
