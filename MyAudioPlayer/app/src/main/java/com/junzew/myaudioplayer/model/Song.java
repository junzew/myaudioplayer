package com.junzew.myaudioplayer.model;

/**
 * Created by junze on 2016-11-26.
 */

public class Song {

    private long id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String name;
    private long albumId;

    public Song(SongBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.artist = builder.artist;
        this.album = builder.album;
        this.genre = builder.genre;
        this.name = builder.name;
        this.albumId = builder.albumId;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", genre='" + genre + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getName() {
        return name;
    }

    public long getAlbumId() {
        return albumId;
    }

    public static class SongBuilder {

        long id;
        String title;
        String artist;
        String album;
        String genre;
        String name;
        long albumId;

        public SongBuilder() {

        }

        public SongBuilder id(long id) {
            this.id = id;
            return this;
        }

        public SongBuilder title(String title) {
            this.title = title;
            return this;
        }

        public SongBuilder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public SongBuilder genre(String artist) {
            this.genre = artist;
            return this;
        }

        public SongBuilder album(String artist) {
            this.album = artist;
            return this;
        }
        public SongBuilder name(String name) {
            this.name = name;
            return this;
        }
        public SongBuilder albumId(long albumId) {
            this.albumId = albumId;
            return this;
        }


        public Song build() {
            return new Song(this);
        }
    }
}


