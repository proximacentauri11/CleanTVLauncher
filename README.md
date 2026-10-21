# CleanTVLauncher

Лёгкий Android TV лаунчер для Xiaomi TV Stick 4K (Android 14, SDK 34, armeabi-v7a, 2 ГБ RAM).

Стиль Apple TV 4K: чёрный фон, док внизу, сетка приложений, крупные часы и приветствие. Без blur, без видео, без сетевого поллинга.

## Что сделано

### Дебloat (pm disable-user --user 0)
- Телеметрия Xiaomi: com.miui.tv.analytics, com.xiaomi.statistic
- PatchWall: com.mitv.tvhome.atv, com.mitv.tvhome.mitvplus, com.mitv.tvhome.michannel
- Miracast/каст: com.xiaomi.mitv.smartshare
- Автообновления прошивки: com.xiaomi.mitv.updateservice
- Стриминг: com.vk.tv, com.tcl.browser
- Рекомендации Google: com.google.android.tvrecommendations (если отключился)
- Google Assistant: com.google.android.katniss (если отключился)
- Стоковый лаунчер: com.google.android.tvlauncher
- Старый тестовый лаунчер: com.example.appletvlauncher

### Лаунчер
- Домашний: com.example.cleantvlauncher
- Док: happ, SmartTube, YouTube, Lampa
- Сетка: 4 колонки, остальные приложения
- Навигация: Вниз с верхнего ряда сетки -> док; внутри сетки Вниз листает ряды
- Часы, дата, приветствие
- Подпись над доком при фокусе на доке; подпись под плиткой при фокусе на плитке

## Сборка и установка
Нужны: JDK 17, Android SDK в C:\android-sdk (platforms;android-34, build-tools;34.0.0), adb.

Запусти build_and_install.bat

## Вернуть всё как было
Запусти restore.bat, затем перезагрузи стик. Скрипт включит отключённые пакеты и вернёт стоковый лаунчер домашним.

## Как поменять док
Открой app\src\main\java\com\example\cleantvlauncher\MainActivity.java, найди массив DOCK_PACKAGES и замени пакеты в нужном порядке. Пересобери через build_and_install.bat.

Имена пакетов: adb shell pm list packages

## Как поменять обои
Сейчас фон чёрный (Color.BLACK в buildLayout()).
1. Положи картинку в app\src\main\res\drawable\wallpaper.png
2. В MainActivity.java в buildLayout() замени root.setBackgroundColor(Color.BLACK); на root.setBackgroundResource(R.drawable.wallpaper);
3. Пересобери через build_and_install.bat

## AdGuard DNS (не включён)
adb shell settings put global private_dns_specifier dns.adguard-dns.com
adb shell settings put global private_dns_mode hostname

Откат: adb shell settings put global private_dns_mode opportunistic
YouTube-рекламу не блокирует.

## Безопасность: закрыть сетевой ADB
1. На стике: Настройки -> Аккаунты и безопасность -> Отладка по ADB -> выключить
2. Там же: Revoke USB debugging authorizations
3. Перезагрузить стик
4. На ПК: Test-NetConnection "ВАШ ЛОКАЛЬНЫЙ IP" -Port 5555 -> TcpTestSucceeded = False

## Известные ограничения
- Сборка под armeabi-v7a (32-бит), как у стика
- В сетке только приложения с LEANBACK_LAUNCHER (TV-приложения)
- После обновления прошивки Xiaomi может вернуть PatchWall/стоковый лаунчер — повтори дебloat и build_and_install.bat

## Структура проекта
CleanTVLauncher/
  app/
    build.gradle
    src/main/
      AndroidManifest.xml
      java/com/example/cleantvlauncher/
        MainActivity.java
        AppAdapter.java
        AppInfo.java
      res/
        drawable/ (banner, ic_launcher, tile_focus, dock_band)
        values/ (strings, colors, themes)
  build.gradle
  settings.gradle
  gradle.properties
  restore.bat
  build_and_install.bat
  README.md