---
title: "Downloading Sequence"
description: "The Lifecycle of files and when they are downloaded"
---

# Downloading Sequence

When the user attempts to etch a record in the player, they must wait for the file to be verified.
Hopefully, this alleviates issues and frustrations with etching disks that then do not work, and then trying to play them.

When a file is requested to be downloaded with a URL, the File Downloader will attempt to download it, and if successful
return the checksum of the downloaded file. (TODO: See if there's a standard header for the checksum). If there is not
already a file in the cache named by that checksum, it will move the file to the cache and return the checksum.

After the checksum is returned to the etcher, the record is considered valid and will be etched with the name, url, and
checksum of the file.

When the record is attempted to be played, the same process takes place again, however now that we already have the
checksum written to the item, we only need to search the cache for the checksum. If it doesn't exist, such as another player listening,
the file downloader will attempt to download the file, and return the checksum to the player to be played.

With this, the file downloader is able to behave the same way regardless of if it's being used to etch or play a record.
Additionally, etched records will be cached already when you attempt to play them for the first time.



```mermaid
sequenceDiagram
    actor User
    participant Record Etcher
    participant File Downloader
    participant Local Cache
    participant Music Player
    participant Record Player

    User->>Record Etcher: Writes a record with a url

    Record Etcher->>File Downloader: Requests download
    alt valid
        opt Checksum not present
          File Downloader->>Local Cache: Moves file to cache
        end
        File Downloader-->>Record Etcher: The checksum of the file
    else invalid
        File Downloader-->>User: Error Message
    end

    
    Record Etcher-->>User: A record written with the url and checksum
    User->>Record Player: Places Record in
    Record Player-)Music Player: Requests play of record
    Music Player-)Local Cache: Checks Cache

    alt in cache
        Music Player--)User: Streams Music to
    else not in cache
      Music Player-)File Downloader: Request Download
      alt valid
        opt Checksum not present
          File Downloader->>Local Cache: Moves file to cache
        end
        File Downloader-->>Music Player: The checksum of the file
        Music Player--)User: Streams Music to
      else invalid
        File Downloader-->>User: Error Message
      end
    end
```

## Future Considerations
- Preemptively downloading songs when the user picks up a record
- Syncing record play time between users
- Configurable limits for cache filesystem usage and expiring

## MusicCache

The music cache is designed such that it may support different providers and strategies
in the future, although it likely never will if we're being honest with ourselves.

```mermaid
flowchart TD
    request_url(Request file by url)
    request_chk(Request file by url and checksum)
    
    request_url-->download_temp
    
    request_chk--Checksum in cache?-->request_chk_if{ }
    request_chk_if--Yes-->return
    request_chk_if--Noo-->download_temp
    
    
    download_temp(Download to temp directory)-->download_if
    download_if{ }
    download_if--Success-->move(Move to cache)-->return
    download_if--Failure-->return_err
    
    return((Return Checksum))
    return_err((Return Error))
```