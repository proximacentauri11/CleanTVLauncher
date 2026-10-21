@echo off
setlocal
set IP=10.48.219.58:5555
echo === CleanTVLauncher: restore ===
for %%P in (
  com.miui.tv.analytics
  com.xiaomi.statistic
  com.mitv.tvhome.atv
  com.mitv.tvhome.mitvplus
  com.mitv.tvhome.michannel
  com.xiaomi.mitv.smartshare
  com.google.android.tvrecommendations
  com.google.android.katniss
  com.vk.tv
  com.tcl.browser
  com.xiaomi.mitv.updateservice
  com.google.android.tvlauncher
  com.example.appletvlauncher
) do (
  echo Enabling %%P
  adb -s %IP% shell pm enable %%P
)
echo Setting stock launcher as HOME...
adb -s %IP% shell cmd package set-home-activity com.google.android.tvlauncher/.MainActivity
echo.
echo Restore done. ??????????? ????.
endlocal
