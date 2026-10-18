#!/usr/bin/env bash
# Build the debug APK and install it on the TV as the home app, checking each step before the next.
# Needs the stock adb in PATH (and JDK 17 + Android SDK 34 for the build).
#
#   TV_HOST=192.168.1.50 ./build-and-install.sh
#
# The stock launcher is NOT disabled automatically: verify the new home across a reboot first,
# then disable it yourself (see README). That keeps a way back in if anything looks wrong.
set -euo pipefail
: "${TV_HOST:?set TV_HOST to the TV IP address, e.g. TV_HOST=192.168.1.50 $0}"
STOCK_LAUNCHER="${STOCK_LAUNCHER:-com.google.android.tvlauncher}"
ADB="${ADB:-adb}"
DEV="$TV_HOST:5555"
cd "$(dirname "$0")"

./gradlew assembleDebug --console=plain
APK=app/build/outputs/apk/debug/app-debug.apk

$ADB connect "$DEV" >/dev/null 2>&1 || true
$ADB -s "$DEV" wait-for-device

echo "== install =="
INSTALL_OUT=$($ADB -s "$DEV" install -r "$APK")
echo "$INSTALL_OUT"
case "$INSTALL_OUT" in
  *Success*) ;;
  *) echo "install failed — nothing else was touched"; exit 1 ;;
esac

echo "== permission for the Wi-Fi name pill =="
$ADB -s "$DEV" shell pm grant com.gathin.tvhome android.permission.ACCESS_FINE_LOCATION 2>/dev/null || true

echo "== set as home activity =="
$ADB -s "$DEV" shell cmd package set-home-activity --user 0 com.gathin.tvhome/.MainActivity

cat <<EOF

Installed. Press Home on the remote and check every key, the dock and the clock.
Only after that, disable the stock launcher (this is the irreversible-feeling step):

  $ADB -s $DEV shell pm disable-user --user 0 $STOCK_LAUNCHER

Way back:  $ADB -s $DEV shell pm enable $STOCK_LAUNCHER
EOF
