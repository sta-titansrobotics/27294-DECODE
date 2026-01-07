read -p "Please ensure that you do NOT have the driver station connected to your computer via tcp/ip or usb, the only external device that should be connected to your computer is the control hub (basically dont connect anything that is running android unless it is a control hub). Press enter to continue if you understand this"

if [ ! -d "./temp" ]; then
    echo "setting up compile workspace pls wait..."
    source get.sh
else
    echo "./temp directory detected, if compile fails delete the directory and rerun this script"
fi

if [ ! -f temp/androidsdk/cmdline-tools/latest/bin/sdkmanager.bat ]; then
    echo "sdkmanager not found (you may need to run this again as this thing likes to halucinate)"
    exit 1
else
    echo "Please accept the licenses outlined below (type y then enter)"
    JAVA_HOME="temp/jdk21/" PATH="$PATH:$JAVA_HOME/bin" temp/androidsdk/cmdline-tools/latest/bin/sdkmanager.bat --licenses
fi

result=$?
echo "end of accepting licenses"

if [ "$result" -eq 0 ]; then
    JAVA_HOME="temp/jdk21/" PATH="$PATH:$JAVA_HOME/bin" ./gradlew :TeamCode:installRelease
else
    echo "you do not accept the licenses that were mentioned above, go do it"
    exit 1
fi

result=$?
if [ "$result" -ne "0" ]; then
    echo "something went wrong with compiling/uploading to the control hub, check the error message above for more details"
    exit 1
fi
