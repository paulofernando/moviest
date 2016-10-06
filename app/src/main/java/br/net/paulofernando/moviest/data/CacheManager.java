package br.net.paulofernando.moviest.data;


import android.content.Context;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirPutCallback;

import java.io.IOException;

import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.data.entities.Collections;
import br.net.paulofernando.moviest.data.entities.Page;

import static br.net.paulofernando.moviest.data.remote.TMDB.Services.CollectionsService;
import static br.net.paulofernando.moviest.data.remote.TMDB.Services.NowPlayingService;
import static br.net.paulofernando.moviest.data.remote.TMDB.Services.PopularService;
import static br.net.paulofernando.moviest.data.remote.TMDB.Services.TopRatedService;

public class CacheManager {

    public static final String TAG = "CacheManager";
    public static final long CACHE_SIZE = 1000000; //in bytes
    public static final String TIME_CACHED = "_timecached";
    public static final long EXPIRATION_NOW_PLAYING = 10000 * 60 * 60; //milliseconds
    public static final long EXPIRATION_POPULAR = 1000 * 60 * 60;
    public static final long EXPIRATION_TOP_RATED = 1000 * 60 * 60 * 24;
    public static final long EXPIRATION_COLLECTIONS = 1000 * 60 * 1;

    public static void startCacheService(Context context) {
        try {
            Reservoir.init(context, CACHE_SIZE);
        } catch (Exception e) {}
    }

    /**
     * Verifies if the cache of a service has expired.
     */
    public static boolean hasExpired(String cacheName, long expiration) {
        try {
            if(Reservoir.contains(cacheName + TIME_CACHED)) {
                return (System.currentTimeMillis() - Reservoir.get(cacheName + TIME_CACHED, Long.class) > expiration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) { //sometimes happens when the app come back from background
            e.printStackTrace();
        }
        return true;
    }

    public static void cachePage(final TMDB.Services serviceType, final Page page) {
        Reservoir.putAsync(serviceType.toString() + page.pageNumber, page, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Page " + page.pageNumber + " cached in tab " + serviceType.toString());
                try {
                    Reservoir.put(serviceType.toString() + page.pageNumber + TIME_CACHED, System.currentTimeMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failure on saving data in cache");
                e.printStackTrace();
            }
        });
    }

    public static void cacheCollection(final Collections collections) {
        Reservoir.putAsync(CollectionsService.toString(), collections, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Collections version " + collections.version + " cached");
                try {
                    Reservoir.put(CollectionsService.toString() + TIME_CACHED, System.currentTimeMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failure on saving data in cache");
                e.printStackTrace();
            }
        });
    }

    public static long getCacheExpiration(TMDB.Services service) {
        if(service == PopularService) {
            return EXPIRATION_POPULAR;
        } else if(service == TopRatedService) {
            return EXPIRATION_TOP_RATED;
        } else if(service == NowPlayingService) {
            return EXPIRATION_NOW_PLAYING;
        } else if(service == CollectionsService) {
            return EXPIRATION_COLLECTIONS;
        }
        return 0x0L;
    }

}
