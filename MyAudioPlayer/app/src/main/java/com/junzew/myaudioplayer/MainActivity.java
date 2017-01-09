package com.junzew.myaudioplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.junzew.myaudioplayer.model.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String[] SUPPORTED_AUDIO_FORMATS = {".mp3", ".aac", ".m4a", ".wma", ".flac", ".wav", ".ogg", ".ape", ".3gp"};
    public static final Set<String> FORMAT_SET = new HashSet<>(Arrays.asList(SUPPORTED_AUDIO_FORMATS));
    public static final Uri ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");

    @BindView(R.id.lv_audio) ListView mListView;

    private List<Song> mAudioFiles = new ArrayList<>();
    private SongAdapter mAdapter = new SongAdapter(mAudioFiles);

    private static final int PERMISSION_REQUEST_READ_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mListView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestReadStoragePermisson();
        } else {
            fetchAudios();
        }

    }

    // request permissions
    private void requestReadStoragePermisson() {
        Log.i("Main", "requesting permisson");
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Main", "need permission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                createDialog();
            } else {

                // No explanation needed, we can request the permission.
                Log.i("Main", "can request");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_STORAGE);
                Log.i("Main", "after request");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i("Main", "can request");
            fetchAudios();
        }
    }

    private void createDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.permission_hint))
                .setMessage(getString(R.string.permission_explanation))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_READ_STORAGE);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("Main", "request callback");
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_STORAGE: {
                Log.i("Main", "request callback PERMISSION_REQUEST_READ_STORAGE");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    fetchAudios();
                } else {
                    Toast.makeText(MainActivity.this, "external storage access denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void fetchAudios() {
        Log.i("MainActivity", "fetching");
        getExternalAudioFiles();
        mAdapter.notifyDataSetChanged();
        Log.i("MainActivity", "fetched");
    }

    private class SongAdapter extends BaseAdapter {

        private List<Song> songs;

        public SongAdapter(List<Song> songs) {
            this.songs = songs;
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Song song = songs.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.files_lv_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Uri uri = ContentUris.withAppendedId(ARTWORK_URI, song.getAlbumId());
            Picasso.with(MainActivity.this)
                    .load(uri)
                    .placeholder(R.drawable.default_album)
                    .into(holder.albumArt);
            holder.tvTitle.setText(song.getTitle());
            holder.tvArtist.setText(song.getArtist());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long id = song.getId();
//                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
//                    intent.putExtra("id", id);
//                    startActivity(intent);
//                    Uri contentUri = ContentUris.withAppendedId(
//                            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
//                    playMedia(contentUri);
                    Intent intent = new Intent(MainActivity.this, PlayService.class);
                    intent.putExtra("id", id);

                    startService(intent);

                    Intent i = new Intent(MainActivity.this, PlayActivity.class);
                    i.putExtra("albumId", song.getAlbumId());
                    i.putExtra("title", song.getTitle());
                    startActivity(i);
                }
            });
            return convertView;
        }

        public List<Song> getSongs() {
            return songs;
        }

        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }

        public void addSong(Song song) {
            this.songs.add(song);
        }
    }

    static class ViewHolder {
        @BindView(R.id.album_art) ImageView albumArt;
        @BindView(R.id.lv_item_title) TextView tvTitle;
        @BindView(R.id.lv_item_artist) TextView tvArtist;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void playMedia(Uri file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(file);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void getExternalAudioFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // query failed, handle error.
            Log.d("MainActivity", "error query");
        } else if (!cursor.moveToFirst()) {
            // no media on the device
            Log.d("MainActivity", "No media found");
            Toast.makeText(MainActivity.this, "No audio found on device", Toast.LENGTH_SHORT).show();
        } else {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST);
            int albumNameColumn = cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM);
            int albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ID);
            int nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                String thisArtist = cursor.getString(artistColumn);
                String thisAlbumName = cursor.getString(albumNameColumn);
                long thisAlbumId = cursor.getLong(albumIdColumn);
                String thisName = cursor.getString(nameColumn);
                // process entry
                Song.SongBuilder builder = new Song.SongBuilder();
                Song song = builder
                        .id(thisId)
                        .artist(thisArtist)
                        .title(thisTitle)
                        .album(thisAlbumName)
                        .name(thisName)
                        .albumId(thisAlbumId)
                        .build();
                // filter audio files that have the supported extensions
                for (String extension : FORMAT_SET) {
                    if (thisName.endsWith(extension)) {
                        mAdapter.addSong(song);
                        break;
                    }
                }
            } while (cursor.moveToNext());
        }
    }


}
