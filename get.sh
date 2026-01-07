echo setting up temp dir

# setup
mkdir temp
cd temp

# download
echo downloading...
curl https://dl.google.com/android/repository/commandlinetools-win-13114758_latest.zip --output android.zip
curl https://download.oracle.com/java/21/archive/jdk-21.0.3_windows-x64_bin.zip --output jdk21.zip

echo unziping
unzip android.zip
unzip jdk21.zip

# androidsdk dir setup
echo setting up androidsdk directory
mkdir androidsdk/
mkdir androidsdk/cmdline-tools/
mkdir androidsdk/cmdline-tools/latest/
mv cmdline-tools/* androidsdk/cmdline-tools/latest/
rm -r cmdline-tools/

echo setting up jdk directory
mv "jdk-21.0.3/" "jdk21"

echo "echo \"set \`ANDROID_HOME\` to: \$(cygpath -w \$(realpath androidsdk/))\"" >> get_paths.sh
echo "echo \"set \`JAVA_HOME\` to: \$(cygpath -w \$(realpath jdk21/))\"" >> get_paths.sh
echo "echo \"Add the following value to Path: %JAVA_HOME%/bin\"" >> get_paths.sh
echo "echo \"\"" >> get_paths.sh
echo "echo \"Expect a new window to pop up and add the following values as addressed above\"" >> get_paths.sh
echo "rundll32 sysdm.cpl,EditEnvironmentVariables" >> get_paths.sh
echo "read -p \"Press Enter to Exit\"" >> get_paths.sh
echo "androidsdk/cmdline-tools/latest/bin/sdkmanager.bat --licenses" >> final_setup.sh

read -p "Press Enter to Exit"