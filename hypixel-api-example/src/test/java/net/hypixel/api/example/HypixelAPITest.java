package net.hypixel.api.example;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.cache.Cache;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.http.HypixelHttpResponse;
import net.hypixel.api.reactor.ReactorHttpClient;
import net.hypixel.api.reply.CountsReply;
import net.hypixel.api.reply.CountsReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class HypixelAPITest {
    private HypixelHttpClient httpClient;
    private HypixelAPI hypixelAPI;
    private Cache<String, CountsReply> cache;

    @BeforeEach
    public void setUp() {
        httpClient = Mockito.mock(ReactorHttpClient.class, withSettings().useConstructor(UUID.fromString(getKey())).defaultAnswer(CALLS_REAL_METHODS));
        hypixelAPI = new HypixelAPI(httpClient, 60L);
        cache = new Cache<>(60, new ScheduledThreadPoolExecutor(100),10, TimeUnit.MINUTES,10,TimeUnit.MINUTES,100);
    }

    @Test
    public void testCacheUtilization() throws Exception {
        // Mock the HTTP response

        // First call to populate the cache
        CompletableFuture<CountsReply> firstCall = hypixelAPI.getCounts();
        CountsReply firstResult = firstCall.get();
        assertNotNull(firstResult);

        // Second call should use the cache
        CompletableFuture<CountsReply> secondCall = hypixelAPI.getCounts();
        CountsReply secondResult = secondCall.get();
        assertNotNull(secondResult);

        // Verify that the HTTP client was called only once
        verify(httpClient, times(1)).makeAuthenticatedRequest(any());

        // Verify that the cached result is returned
        assertSame(firstResult, secondResult);
    }
    /**
     * Retrieves the API key from the system properties or prompts the user to enter it.
     *
     * @return the API key
     */
    public static String getKey() {
        // Check if the "key" property is set in the system properties
        return "923c950e-1292-4806-b182-76da1a51caa1";
//        String key = System.getProperty("key");
//        if (key == null || key.isEmpty()) {
//            // If not set, open a popup to ask for the key
//            key = JOptionPane.showInputDialog(null, "Enter your API key:", "API Key Required", JOptionPane.PLAIN_MESSAGE);
//            if (key != null && !key.isEmpty()) {
//                // Set the key in the system properties for future use
//                System.setProperty("key", key);
//            } else {
//                throw new IllegalArgumentException("API key is required.");
//            }
//        }
//        return key;
    }
}