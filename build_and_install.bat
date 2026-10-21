@echo off
setlocal
set ANDROID_HOME=C:\android-sdk
set IP=10.48.219.58:5555
cd /d %~dp0

echo === CleanTVLauncher: build ===
call gradlew.bat assembleDebug
if errorlevel 1 (
  echo BUILD FAILED
  exit /b 1
)

echo === Push & install ===
adb -s %IP% push app\build\outputs\apk\debug\app-debug.apk /data/local/tmp/cleantv.apk
adb -s %IP% shell pm install -r /data/local/tmp/cleantv.apk

echo === Set as HOME ===
adb -s %IP% shell cmd package set-home-activity com.example.cleantvlauncher/.MainActivity

echo === Press Home ===
adb -s %IP% shell input keyevent KEYCODE_HOME

echo.
echo Build & install done.
endlocal
