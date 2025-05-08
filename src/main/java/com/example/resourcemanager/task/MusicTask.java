package com.example.resourcemanager.task;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.adult.Adult;
import com.example.resourcemanager.entity.adult.AdultAuthor;
import com.example.resourcemanager.entity.comic.Comic;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.entity.music.*;
import com.example.resourcemanager.entity.picture.Picture;
import com.example.resourcemanager.mapper.music.MusicAlbumMapper;
import com.example.resourcemanager.mapper.music.MusicAuthorMapper;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.service.comic.ComicSetService;
import com.example.resourcemanager.service.music.*;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Async
@Component
public class MusicTask extends AsyncTask {
    @Resource
    ComicSetService comicSetService;

    @Resource
    MusicService musicService;

    @Resource
    MusicAuthorService musicAuthorService;

    @Resource
    MusicAlbumService musicAlbumService;

    @Resource
    MusicBindService musicBindService;

    @Resource
    MusicAlbumBindService musicAlbumBindService;

    @Resource
    MusicAuthorBindService musicAuthorBindService;

    public static Map<Integer, ComicFolder> folderMap = new HashMap<>();
    public List<Files> musicList = new ArrayList<>();
    public List<Music> musics = new ArrayList<>();
    public List<MusicAuthor> musicAuthorList = new ArrayList<>();
    public List<MusicAlbum> musicAlbumList = new ArrayList<>();
    public static File cover;

    Map<String, Integer> musicNameMap = new HashMap<>();

    Map<String, Integer> musicAuthorNameMap = new HashMap<>();
    List<MusicAuthor> addAuthors = new ArrayList<>();
    HashSet<String> hasAuthor = new HashSet<>();

    Map<String, Integer> musicAlbumNameMap = new HashMap<>();
    List<MusicAlbum> addAlbums = new ArrayList<>();
    HashSet<String> hasAlbum = new HashSet<>();

    Map<String, List<String>> albumBindAuthorsMap = new HashMap<>();
    Map<String, List<String>> musicBindAuthorsMap = new HashMap<>();
    Map<String, String> musicBindAlbumMap = new HashMap<>();

    public MusicTask() {
        basePath = "music";
        contentType = 4;
        skipFolder.add("cover");
    }

    @Override
    public void create() {
        musics.clear();
        folderMap.clear();
        musicList.clear();

        cover = new File(filePath + File.separator + basePath + File.separator + "cover");
        if (!cover.exists()) cover.mkdirs();

        createFiles.forEach(value -> {
            if (value.getIsFolder() == 1 && value.getChild() != null) {
                value.setCover(value.getChild().get(0).getHash());
            }
        });
        System.out.println(createFiles.get(0).getFileName());
        System.out.println(createFiles.get(0).getId());
        deepCreate(createFiles, 1);

        musicAuthorList = musicAuthorService.getList();
        musicAlbumList = musicAlbumService.getList();

        musicAuthorNameMap = musicAuthorList.stream().collect(Collectors.toMap(MusicAuthor::getName, MusicAuthor::getId));//Map<艺术家名称，艺术家ID>
        musicAlbumNameMap = musicAlbumList.stream().collect(Collectors.toMap(MusicAlbum::getName, MusicAlbum::getId));//Map<专辑名称，专辑ID>

        List<Future<Music>> futureList = new ArrayList<>();
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(20000));
        for (Files files : musicList) {
            Future<Music> future = executor.submit(new GetMusicTask(files, filesUtils));
            futureList.add(future);
        }
//

        int index = 0;
        for (Future<Music> future : futureList) {
            try {
                Music music = future.get();
                musics.add(music);
                checkAuthor(music.getAuthors(), music.getTitle() + index, music.getAlbum());
                checkAlbum(music.getAlbum(), music.getCover(),music.getYear());
                musicBindAlbumMap.put(music.getTitle() + index, music.getAlbum());
                index++;
            } catch (Exception e) {
                e.printStackTrace();
                executor.shutdownNow();
                future.cancel(true);
                throw new BizException("4000", e.getMessage());
            }
        }
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }

        musics = musicService.createData(musics);
        addAlbums = musicAlbumService.createData(addAlbums);
        addAuthors = musicAuthorService.createData(addAuthors);

        for (int i = 0; i < musics.size(); i++) {
            Music value = musics.get(i);
            musicNameMap.put(value.getTitle() + i, value.getId());
        }
        addAlbums.forEach(value -> musicAlbumNameMap.put(value.getName(), value.getId()));
        addAuthors.forEach(value -> musicAuthorNameMap.put(value.getName(), value.getId()));

        List<MusicAuthorBind> musicAuthorBindList = new ArrayList<>();//音乐绑定艺术家
        musicBindAuthorsMap.forEach((key, value) -> {
            Integer musicId = musicNameMap.get(key);
            System.out.println(key);
            System.out.println(musicNameMap);
            if (musicId != null) {
                value.forEach(author -> {
                    Integer authorId = musicAuthorNameMap.get(author);
                    if (authorId != null) {
                        MusicAuthorBind albumAuthorBind = new MusicAuthorBind();
                        albumAuthorBind.setMusicId(musicId);
                        albumAuthorBind.setAuthorId(authorId);
                        musicAuthorBindList.add(albumAuthorBind);
                    }
                });
            }
        });
        musicAuthorBindService.createData(musicAuthorBindList);


        List<MusicAlbumBind> musicAlbumBindList = new ArrayList<>();//专辑绑定艺术家
        albumBindAuthorsMap.forEach((key, value) -> {
            Integer albumId = musicAlbumNameMap.get(key);
            if (albumId != null) {
                value.forEach(author -> {
                    Integer authorId = musicAuthorNameMap.get(author);
                    if (authorId != null) {
                        MusicAlbumBind musicAlbumBind = new MusicAlbumBind();
                        musicAlbumBind.setAlbumId(albumId);
                        musicAlbumBind.setAuthorId(authorId);
                        musicAlbumBindList.add(musicAlbumBind);
                    }
                });
            }
        });
        musicAlbumBindService.createData(musicAlbumBindList);
    //        musicBindAuthorMap.forEach((value,key) -> {
//            if(musicNameMap.containsKey(key) || musicAuthorNameMap.containsKey(value)) return;
//            MusicAuthorBind musicAuthorBind = new MusicAuthorBind();
//            musicAuthorBind.setMusicId(musicNameMap.get(key));
//            musicAuthorBind.setAuthorId(musicAuthorNameMap.get(value));
//            musicAuthorBindList.add(musicAuthorBind);
//        });
//
//        List<MusicAlbumBind> musicAlbumBindList = new ArrayList<>();
//        albumBindAuthorMap.forEach((value,key) -> {
//            if(musicAlbumNameMap.containsKey(key) || musicAuthorNameMap.containsKey(value)) return;
//            MusicAlbumBind musicAlbumBind = new MusicAlbumBind();
//            musicAlbumBind.setAlbumId(musicAlbumNameMap.get(key));
//            musicAlbumBind.setAuthorId(musicAuthorNameMap.get(value));
//            musicAlbumBindList.add(musicAlbumBind);
//        });
//
//
        List<MusicBind> musicBindList = new ArrayList<>();
        musicBindAlbumMap.forEach((key, value) -> {
            if (!musicAlbumNameMap.containsKey(value) || !musicNameMap.containsKey(key)) return;
            MusicBind musicBind = new MusicBind();
            musicBind.setAlbumId(musicAlbumNameMap.get(value));
            musicBind.setMusicId(musicNameMap.get(key));
            musicBindList.add(musicBind);
        });
        System.out.println(musicBindList);

        musicBindService.createData(musicBindList);
//        musicAlbumBindService.createData(musicAlbumBindList);
//        musicAuthorBindService.createData(musicAuthorBindList);
    }

    public void deepCreate(List<Files> list, Integer index) {
        list = filesService.createFiles(list);
        for (Files files : list) {
            if (files.getIsFolder() == 2) {
                files.setCover(files.getHash());
                musicList.add(files);
            }

            if (files.getChild() == null) continue;
            deepCreate(files.getChild(), index += 1);
        }
    }


    public void checkAuthor(List<String> authors, String musicName, String albumName) {
        List<String> albumAuthors = albumBindAuthorsMap.computeIfAbsent(albumName, k -> new ArrayList<>());
        List<String> musicAuthors = musicBindAuthorsMap.computeIfAbsent(musicName, k -> new ArrayList<>());
        for (String author : authors) {
            author = author.trim();
            if (!albumAuthors.contains(author)) {
                albumAuthors.add(author);
            }
            if (!musicAuthors.contains(author)) {
                musicAuthors.add(author);
            }
            if (musicAuthorNameMap.containsKey(author)) continue;
            if (hasAuthor.contains(author)) continue;
            MusicAuthor musicAuthor = new MusicAuthor();
            musicAuthor.setName(author);
            addAuthors.add(musicAuthor);
            hasAuthor.add(author); // 及时添加到 hasAuthor 避免重复添加 addAuthors
        }
    }

    public void checkAlbum(String album, String cover,Integer year) {
        album = album.trim();
        System.out.println(album);
        System.out.println(hasAlbum);
        if (musicAlbumNameMap.containsKey(album)) return;
        if (hasAlbum.contains(album)) return;
        MusicAlbum musicAlbum = new MusicAlbum();
        musicAlbum.setName(album);
        musicAlbum.setCover(cover);
        musicAlbum.setYear(year);
        addAlbums.add(musicAlbum);
        hasAlbum.add(album);
    }
}

@AllArgsConstructor
class GetMusicTask implements Callable<Music> {
    Files files;
    FilesUtils filesUtils;

    @Override
    public Music call() {
        File cover = new File(MusicTask.cover.getPath() + File.separator + files.getHash() + ".jpg");
        try {
            AudioFile audioFile = AudioFileIO.read(files.getFile());
            Tag tag = audioFile.getTag();

            Music music = new Music();
            System.out.println(files.getId());
            music.setFilesId(files.getId());
            music.setTitle(tag.getFirst(FieldKey.TITLE));
            music.setYear(tag.getFirst(FieldKey.YEAR).isBlank() ? null : Integer.parseInt(tag.getFirst(FieldKey.YEAR)));
            music.setGenre(tag.getFirst(FieldKey.GENRE));
            music.setTrack(tag.getFirst(FieldKey.TRACK).isBlank() ? null : Integer.parseInt(tag.getFirst(FieldKey.TRACK)));
            music.setDisc(tag.getFirst(FieldKey.DISC_NO));
            music.setComposer(tag.getFirst(FieldKey.COMPOSER));
            music.setLanguage(tag.getFirst(FieldKey.LANGUAGE));
            music.setSeconds(audioFile.getAudioHeader().getTrackLength());


//            music.setAlbum();
//            for (String artist : artists) {
//                boolean found = false;
//                for (MusicAuthor musicAuthor : MusicTask.musicAuthorList) {
//                    if (musicAuthor.getName().equals(artist)) {
//                        artistsIds.add(musicAuthor.getId());
//                        found = true;
//                        break;
//                    }
//                }
//                if (!found) {
//                    MusicAuthor musicAuthor = new MusicAuthor();
//                    musicAuthor.setName(artist);
//                    authorList.add(musicAuthor);
//                }
//            }
//
//            if(!authorList.isEmpty()){
//                authorList = musicAuthorService.createData(authorList);
//                artistsIds.addAll(authorList.stream().map(value -> value.getId()).collect(Collectors.toList()));
//            }


            // 获取专辑封面
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) {
                byte[] imageData = artwork.getBinaryData();
                try (OutputStream fileOutputStream = new FileOutputStream(cover)) {
                    fileOutputStream.write(imageData);
                }
            }

            music.setCover(cover.getPath());
            music.setAlbum(tag.getFirst(FieldKey.ALBUM));
            music.setAuthors(tag.getAll(FieldKey.ARTIST));
            return music;
        } catch (Exception e) {
            throw new BizException("4000", e.getMessage());
        }
    }
}
