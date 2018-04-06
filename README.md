# MB2mp3tag

MB2mp3tag is a program for downloading music data from musicbrainz.org and saving it in a format that can be used by mp3tag.

Musicbrainz.org is a huge database (https://musicbrainz.org/statistics) containing detailed information on music releases: titles of songs, compositions dates, recording dates, composers, performers, recording locations, etc. This information is stored in a relational database (https://musicbrainz.org/doc/MusicBrainz_Database/Schema).

The mp3tag program (https://www.mp3tag.de/en/) is my favorite program for tagging music files. It has many functions, it gives the possibility of manual and automatic editing of music tags.

The mp3tag program allows you to set tags for a song by loading them from a text file. In this file, all information about the song must be saved in a single line. Musicbrainz.org makes the data available in a dispersed form. Releases' data may be downloaded separately, data for each recording separately and separately for each artist (composer or performer). My program (MB2mp3tag) is therefore the link between musicbrainz.org and mp3tag. It downloads data from the musicbrainz.org website in the form in which it is possible, processes them and saves them in a text file in such a format that it can be used by the mp3tag program.
The source data are xml files. The release is downloaded as an xml file, each recording presented in this release is downloaded as an xml file, each artist (artist, composer) is downloaded as a separate xml file. For only one medium (disk) containing several recordings, we may already have to download almost 100 xml files from musicbrainz.org. Musicbrainz.org "dislikes" when it needs to share a lot of data within a short period of time (https://musicbrainz.org/doc/XML_Web_Service/Rate_Limiting). It is recommended that there be at most one reading per second. That's why my application has a local cache. Each xml file that you download from the server is stored in the local directory. This makes the creation of tags for subsequent releases faster and faster, as more and more data is available locally over time.

## How to use the program?

Let's say you have music files from the "Mozart Piano Concertos (Andras Schiff)" release. You want to tag them in the mp3tag program. First you are looking for this release on the musicbrainz.org website. It is at https://musicbrainz.org/release/b0837172-673c-4416-80d6-8a5801e6f102:

[[https://github.com/PawelTrela/My-projects/blob/master/AudioTaggerWithMusicBrainz/images/03.png|alt="Piano concertos" release on musicbrainz.org]]

So you copy this address to the clipboard and run my program, giving the copied address as an argument:
MB2mp3tag.exe https://musicbrainz.org/release/b0837172-673c-4416-80d6-8a5801e6f102

[[https://github.com/PawelTrela/My-projects/blob/master/AudioTaggerWithMusicBrainz/images/01.png|alt=MB2mp3tag - run program]]

The program starts working and after a few tens of seconds in the file "Piano Concertos.txt" you have stored data for the tags:

[[https://github.com/PawelTrela/My-projects/blob/master/AudioTaggerWithMusicBrainz/images/02.png|alt=MB2mp3tag - program finished creating output file]]

Next you open the mp3tag program, load a directory with music files into it, and launch the "Convert > Text file - Tag" action:

[[https://github.com/PawelTrela/My-projects/blob/master/AudioTaggerWithMusicBrainz/images/05.png|alt=mp3tag - configure action "tag from text file"]]

In the dialog that opens, in the "File name" field you give the path to the file that was created by my program, and in the "String format" field you specify the template that my program prepared the data from (by default: %discnumber%|%disctotal%|%album%|%track%|%tracknumber%|%tracktotal%|%title%|%composer%|%artist%|%year%|%organization%|%comment%|%url%). You confirm "OK" and after a while your music files are tagged:

[[https://github.com/PawelTrela/My-projects/blob/master/AudioTaggerWithMusicBrainz/images/06.png|alt=mp3tag - tag's updated]]

And here's how it looks in music player:

[[https://github.com/PawelTrela/My-projects/blob/master/AudioTaggerWithMusicBrainz/images/07.png|alt=aimp3 (music player)]]