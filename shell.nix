{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell {
  name = "android-dev-shell";

  buildInputs = with pkgs; [
    openjdk17
    gradle
    git
    curl
    wget
    tree
    unzip
  ];

  ANDROID_HOME = "${builtins.getEnv "HOME"}/android-sdk";
  JAVA_HOME = pkgs.openjdk17;

  shellHook = ''
    export ANDROID_HOME=$HOME/android-sdk
    export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
    echo "✅ Android SDK and tools ready!"
  '';
}