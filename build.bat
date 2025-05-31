@echo off

set PATH_TO_JAVA="C:\Program Files\Eclipse Adoptium\jdk-17.0.11.9-hotspot\bin"
set PATH_TO_FX="D:\Programming\Java\Tools\javafx-sdk-21.0.4\lib"
set JAVA_MODS="C:\Program Files\Eclipse Adoptium\jdk-17.0.11.9-hotspot\jmods"
set FX_MODS="D:\Programming\Java\Tools\javafx-sdk-21.0.4\jmods"

set OUT_BYTECODE="bin"
set MAINCLASS="App"
set APPNAME="Video"
set MODULES_TO_ADD="javafx.controls,javafx.fxml,javafx.graphics,javafx.media,java.desktop,javafx.base"

@REM goto jar

mkdir build
dir /s /b src\*.java > sources.txt

:javac
echo Ejecutando javac...

%PATH_TO_JAVA%\javac --module-path %PATH_TO_FX% --add-modules=javafx.controls,javafx.fxml,javafx.graphics,java.desktop,javafx.base -d %OUT_BYTECODE% @sources.txt

:recursos
echo Copiando recursos no-java

echo .java > excl.txt
xcopy src\*.* %OUT_BYTECODE% /s /i /e /y /EXCLUDE:excl.txt > nul
@REM goto exit

:jar
echo Creando Jar

%PATH_TO_JAVA%\jar --create --file=build/jar/%APPNAME%.jar --main-class=%MAINCLASS% -C %OUT_BYTECODE% .
goto exit

:jlink
echo Ejecutando JLINK

@REM Esto funcionó correctamente si se ejecuta bien con:
@REM fxjre\bin\java -jar app\javafx.jar

%PATH_TO_JAVA%\jlink --module-path %JAVA_MODS%;%FX_MODS% --add-modules=%MODULES_TO_ADD% --output build\runtime

:jpackage
echo Ejecutando JPACKAGE

@REM La version es con --app-version porque lo que va a tirar es la version del jpackage
@REM --type
@REM exe y msi hacen un instalador con exe o con msi (que hasta ahora no me han funcado)
@REM app-image hace una imagen portable que se abre como .exe
@REM Lo que haya en input él lo copia completo para su carpeta de destino, entonces ojo con poner la carpeta de destino dentro de input

mkdir build\package
%PATH_TO_JAVA%\jpackage --type app-image --dest build/package --input build/jar --name %APPNAME% --main-jar %APPNAME%.jar --runtime-image "build\runtime" --verbose

:exit