
Album Creator
=============

What's the purpose?
~~~~~~~~~~~~~~~~~~~

This utility allows to create compilations of music tracks.
Maybe you used to put all of your music files into one single
directory, and now you want to have individual album directories -
but the ID3 information was lost? You'll have to do it manually!

Using AlbumCreator, it's easy - just select tracks one after the other,
and the program will create a scrit for you or move the files at the
click of a button.

Each track will get a prefix with the track number, but retain the file
name it had originally. If a file already exists, it will not be overwritten
but the program will stop right there.

How to do it?
~~~~~~~~~~~~~

There's not much to do wrong - first select source and target directory using
the buttons at the top. Then double-click on tracks on the left (source)
list to add them to the right (target) list. When you're done,
choose if you want to create a script (which will appear inside the
logging area below the buttons so you can copy it) or move the files
directly and click the appropriate button at the bottom.

If you want to remove a track from the right list, double-click on it, and
the other tracks will be renumbered accordingly.

What do I have to provide?
~~~~~~~~~~~~~~~~~~~~~~~~~~

* some music files, obviously - currently only MP3 and FLAC are supported
  but this is easy to change in MusicFilePredicate
* the Java Runtime Environment (JRE) in version 8 or newer -
  get it here: http://java.com/
