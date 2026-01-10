## NOTICE

This repository contains the public FTC SDK for the DECODE (2025-2026) competition season.

# COMPILATION INSTRUCTIONS (for those who dont know what they are doing)

i ask that you first install git first. if you do not want to go through the hassle of selecting the
configuration from running the setup executable, you can use `winget` utility on windows.

this can be done by first pressing `win + r` and typing `cmd` in the text box and pressing open.
this should open up a command prompt

in the command prompt type `winget install Git.Git` and follow the instructions outlined by the terminal (this can include accepting the terms of usage for using winget)

once done, navigate to a folder where your project will reside in. right click as if you were trying to create a new folder

> [!NOTE]
> if you are on windows 11, click more options after right clicking.

select `open git bash`

clone this repository by running `git clone link` 
> [!IMPORTANT]
> replace `link` with the link to this repository

once the repository has been cloned, navigate into the repository by using `cd folder` 
> [!IMPORTANT]
> replace `folder` with the name of the folder where your project resides, this should be the name of your repository, please note that if your folder contains spaces or special characters then you might need to first type out the letters & numbers of the folder and hit the `tab` button for bash to autocomplete the foldername for you

then type `./compile_now.sh` and follow the instruction outlined in the terminal (this should be just accepting the android sdk licenses if you haven't already done that)

> [!NOTE]
> you may need to run `./compile_now.sh` a few times for the inital setup

> [!NOTE]
> before running `./compile_now.sh`, you may want to first connect the control hub to this device
> for the purpose of this you should just use usb to connect the control hub to this device by
> using [this guide](https://docs.revrobotics.com/rev-hardware-client/duo/control-hub#connecting-via-usb)

> [!NOTE]
> for those wondering where the android sdk and jdk is when doing this setup, it is located in the `temp`
> folder in this project