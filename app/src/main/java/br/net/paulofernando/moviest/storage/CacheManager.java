package br.net.paulofernando.moviest.storage;


import android.content.Context;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirPutCallback;

import java.io.IOException;

import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.communication.entities.Page;

public class CacheManager {

    public static final String TAG = "cacheManager";
    public static final long CACHE_SIZE = 1000000; //in bytes
    public static final String CACHE_NOW_PLAYING = "now_playing";
    public static final String CACHE_POPULAR = "popular";
    public static final String CACHE_TOP = "top";
    public static final String CACHE_TIME = "_timecached";
    public static final int NOW_PLAYING_EXPIRATION = 10000 * 60 * 60; //milliseconds
    public static final int POPULAR_EXPIRATION = 1000 * 60 * 60;
    public static final int TOP_EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    public static void startCacheService(Context context) {
        try {
            Reservoir.init(context, CACHE_SIZE);
        } catch (Exception e) {}
    }

    /**
     * Verifies if the cache of a service has expired.
     */
    public static boolean hasExpired(MovieDB.Services serviceType) {
        if(serviceType == MovieDB.Services.NowPlaying) {
            try {
                if(Reservoir.contains(CACHE_NOW_PLAYING + CACHE_TIME)) {
                    return (System.currentTimeMillis() - Reservoir.get(CACHE_NOW_PLAYING + CACHE_TIME, Long.class)
                            > NOW_PLAYING_EXPIRATION);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) { //sometimes happens when the app come back from background
                e.printStackTrace();
            }
        } else if(serviceType == MovieDB.Services.Popular) {
            try {
                if(Reservoir.contains(CACHE_POPULAR + CACHE_TIME)) {
                    return (System.currentTimeMillis() - Reservoir.get(CACHE_POPULAR + CACHE_TIME, Long.class)
                            > POPULAR_EXPIRATION);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else if(serviceType == MovieDB.Services.TopRated) {
            try {
                if(Reservoir.contains(CACHE_TOP + CACHE_TIME)) {
                    return (System.currentTimeMillis() - Reservoir.get(CACHE_TOP + CACHE_TIME, Long.class)
                            > TOP_EXPIRATION);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void cachePage(final MovieDB.Services serviceType, final Page page) {
        String serviceCacheName = null;
        if(serviceType == MovieDB.Services.NowPlaying) {
            serviceCacheName = CacheManager.CACHE_NOW_PLAYING;
        } else if(serviceType == MovieDB.Services.NowPlaying) {
            serviceCacheName = CacheManager.CACHE_POPULAR;
        } else if(serviceType == MovieDB.Services.NowPlaying) {
            serviceCacheName = CacheManager.CACHE_TOP;
        }

        if(serviceCacheName != null) {
            Reservoir.putAsync(serviceCacheName + page, page, new ReservoirPutCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Page " + page + " cached in tab " + serviceType);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failure on saving data in cache");
                    e.printStackTrace();
                }
            });

            try {
                Reservoir.put(serviceCacheName + CacheManager.CACHE_TIME, System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
