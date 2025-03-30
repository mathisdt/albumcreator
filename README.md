![license](https://img.shields.io/github/license/mathisdt/albumcreator.svg?style=flat) [![Build](https://github.com/mathisdt/albumcreator/actions/workflows/build.yaml/badge.svg)](https://github.com/mathisdt/albumcreator/actions) [![last released](https://img.shields.io/github/release-date/mathisdt/albumcreator.svg?label=last%20released&style=flat)](https://github.com/mathisdt/albumcreator/releases)

# Album Creator

## What's the purpose?

This utility allows to create compilations of music tracks.
Maybe you used to put all of your music files into one single
directory, and now you want to have individual album directories -
but the ID3 information was lost? You'll have to do it manually!

Using AlbumCreator, it's easy - just select tracks one after the other,
and the program will create a script for you or move the files at the
click of a button.

Each track will get a prefix with the track number, but retain the file
name it had originally. If a file already exists, it will not be overwritten
but the program will stop right there.

## How to do it?

There's not much to do wrong - first select source and target directory using
the buttons at the top. Then double-click on tracks on the left (source)
list to add them to the right (target) list. When you're done,
choose if you want to create a script (which will appear inside the
logging area below the buttons so you can copy it) or move the files
directly and click the appropriate button at the bottom.

If you want to remove a track from the right list, double-click on it, and
the other tracks will be renumbered accordingly.

## Gettings started

* you'll need some music files, obviously - currently MP3 and FLAC are supported
* get the [Java Runtime Environment (JRE)](http://java.com/) in version 8 or newer
* download the [lastest release](https://github.com/mathisdt/albumcreator/releases/latest)
  and unpack it
* start it using the script contained in the "bin" directory or with `java -jar albumcreator.jar`

## Build using Earthly

The CI build of this project uses [Earthly](https://docs.earthly.dev/), which in turn uses
container virtualization (e.g. Docker or Podman). You can also run the build locally (if you
have Earthly as well as an OCI compatible container engine installed) by executing
`earthly +build`. This will create a container with everything needed for the build,
create the package inside it and then copy the results to the directory `target` for you.
