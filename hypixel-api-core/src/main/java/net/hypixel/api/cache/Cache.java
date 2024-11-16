package net.hypixel.api.cache;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cache<K, V> {
    private final Map<K, CacheEntry<V>> cache;
    private final long defaultCacheTime;
    private final int maxSize;
    private final int proonAfter;
    private final TimeUnit proonAfterTimeUnit;

    /**
     * @param defaultCacheTime   the time in seconds a cache entry is valid by default
     * @param executorService    the executor service to use for prooning the cache
     * @param proonTimeDelay     The time between prooning the cache (Deleting old entries)
     * @param proonTimeDelayUnit The time unit of the proon time
     * @param proonAfter         The time after which an entry is allowed to be prooned from the cache
     * @param proonAfterTimeUnit The time unit of the proonAfter time
     * @param maxSize            The maximum size of the cache
     */
    public Cache(long defaultCacheTime, ScheduledExecutorService executorService, int proonTimeDelay, TimeUnit proonTimeDelayUnit, int proonAfter, TimeUnit proonAfterTimeUnit, int maxSize) {
        this.defaultCacheTime = defaultCacheTime;
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<K, CacheEntry<V>>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > maxSize;
            }
        };
        this.proonAfter = proonAfter;
        this.proonAfterTimeUnit = proonAfterTimeUnit;
        executorService.scheduleAtFixedRate(this::proon, proonTimeDelay, proonTimeDelay, proonTimeDelayUnit);
    }

    private void proon() {
        cache.values().removeIf(entry -> !entry.isValid(proonAfterTimeUnit.toSeconds(proonAfter)));
    }


    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value));
    }

    public V get(K key, long maxCacheTime) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && entry.isValid(maxCacheTime)) {
            return entry.getValue();
        }
        cache.remove(key);
        return null;
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && entry.isValid(defaultCacheTime)) {
            return entry.getValue();
        }
        cache.remove(key);
        return null;
    }

    private static class CacheEntry<V> {
        private final V value;
        private final Instant creationTime;

        public CacheEntry(V value) {
            this.value = value;
            this.creationTime = Instant.now();
        }

        public V getValue() {
            return value;
        }

        public boolean isValid(long maxCacheTime) {
            Instant now = Instant.now();
            return now.isBefore(creationTime.plusSeconds(maxCacheTime));
        }
    }
}