package net.hypixel.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.hypixel.api.cache.Cache;
import net.hypixel.api.exceptions.BadResponseException;
import net.hypixel.api.exceptions.BadStatusCodeException;
import net.hypixel.api.http.HTTPQueryParams;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.http.HypixelHttpResponse;
import net.hypixel.api.pets.IPetRepository;
import net.hypixel.api.pets.impl.PetRepositoryImpl;
import net.hypixel.api.reply.*;
import net.hypixel.api.reply.skyblock.*;
import net.hypixel.api.reply.skyblock.bingo.SkyBlockBingoDataReply;
import net.hypixel.api.reply.skyblock.firesales.SkyBlockFireSalesReply;
import net.hypixel.api.util.ResourceType;
import net.hypixel.api.util.Utilities;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HypixelAPI class provides methods to interact with the Hypixel API.
 * It supports caching and authenticated requests.
 */
public class HypixelAPI {
    static final String BASE_URL = "https://api.hypixel.net/v2/";

    protected final HypixelHttpClient httpClient;
    protected final Cache<String, AbstractReply> cache;
    protected final long defaultCacheTime;

    /**
     * Constructs a HypixelAPI instance with a custom HTTP client and default cache time.
     *
     * @param httpClient       the HTTP client to use for requests
     * @param defaultCacheTime the default cache time in seconds
     */
    public HypixelAPI(HypixelHttpClient httpClient, long defaultCacheTime) {
        this(httpClient, defaultCacheTime, 100, 5, TimeUnit.MINUTES, 15, TimeUnit.MINUTES, new ScheduledThreadPoolExecutor(100));
    }

    /**
     * @param httpClient         the HTTP client to use for requests Example: {@code new ReactorHttpClient(UUID.fromString("your-api-key"))}
     * @param defaultCacheTime   the default cache time in seconds. This is just a convenience number for the methods that don't specify a cache time.
     * @param maxSize            the maximum size of the cache (Request Count)
     * @param proonTimeDelay     the time between prooning the cache (Deleting old entries)
     * @param proonTimeDelayUnit The time unit of the proon time
     * @param proonAfter         The time after which an entry is allowed to be prooned from the cache
     * @param proonAfterTimeUnit The time unit of the proonAfter time
     * @param executorService    the executor service to use for prooning the cache
     */
    public HypixelAPI(HypixelHttpClient httpClient, long defaultCacheTime, int maxSize, int proonTimeDelay, TimeUnit proonTimeDelayUnit, int proonAfter, TimeUnit proonAfterTimeUnit, ScheduledThreadPoolExecutor executorService) {
        this(httpClient, defaultCacheTime, new Cache<>(defaultCacheTime, executorService, proonTimeDelay, proonTimeDelayUnit, proonAfter, proonAfterTimeUnit, maxSize));
    }

    public HypixelAPI(HypixelHttpClient httpClient, long defaultCacheTime, Cache<String, AbstractReply> cache) {
        this.httpClient = httpClient;
        this.defaultCacheTime = defaultCacheTime;
        this.cache = cache;
    }


    /**
     * Shuts down the HTTP client.
     */
    public void shutdown() {
        httpClient.shutdown();
    }

    /**
     * Retrieves the boosters data.
     *
     * @return a CompletableFuture containing BoostersReply
     */
    public CompletableFuture<BoostersReply> getBoosters() {
        return get(false, BoostersReply.class, "boosters", null, defaultCacheTime);
    }

    /**
     * Retrieves the boosters data with a specified max cache time.
     *
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing BoostersReply
     */
    public CompletableFuture<BoostersReply> getBoosters(Long maxCacheTime) {
        return get(false, BoostersReply.class, "boosters", null, maxCacheTime);
    }

    /**
     * Retrieves the leaderboards data.
     *
     * @return a CompletableFuture containing LeaderboardsReply
     */
    public CompletableFuture<LeaderboardsReply> getLeaderboards() {
        return get(false, LeaderboardsReply.class, "leaderboards", null, defaultCacheTime);
    }

    /**
     * Retrieves the leaderboards data with a specified max cache time.
     *
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing LeaderboardsReply
     */
    public CompletableFuture<LeaderboardsReply> getLeaderboards(Long maxCacheTime) {
        return get(false, LeaderboardsReply.class, "leaderboards", null, maxCacheTime);
    }

    /**
     * Retrieves the punishment stats.
     *
     * @return a CompletableFuture containing PunishmentStatsReply
     */
    public CompletableFuture<PunishmentStatsReply> getPunishmentStats() {
        return get(true, PunishmentStatsReply.class, "punishmentstats", null, defaultCacheTime);
    }

    /**
     * Retrieves the punishment stats with a specified max cache time.
     *
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing PunishmentStatsReply
     */
    public CompletableFuture<PunishmentStatsReply> getPunishmentStats(Long maxCacheTime) {
        return get(true, PunishmentStatsReply.class, "punishmentstats", null, maxCacheTime);
    }

    /**
     * Retrieves player data by UUID.
     *
     * @param player the UUID of the player
     * @return a CompletableFuture containing PlayerReply
     */
    public CompletableFuture<PlayerReply> getPlayerByUuid(UUID player) {
        return get(true, PlayerReply.class, "player", HTTPQueryParams.create().add("uuid", player), defaultCacheTime);
    }

    /**
     * Retrieves player data by UUID with a specified max cache time.
     *
     * @param player       the UUID of the player
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing PlayerReply
     */
    public CompletableFuture<PlayerReply> getPlayerByUuid(UUID player, Long maxCacheTime) {
        return get(true, PlayerReply.class, "player", HTTPQueryParams.create().add("uuid", player), maxCacheTime);
    }

    /**
     * Retrieves player data by UUID in string format.
     *
     * @param player the UUID of the player in string format
     * @return a CompletableFuture containing PlayerReply
     */
    public CompletableFuture<PlayerReply> getPlayerByUuid(String player) {
        return get(true, PlayerReply.class, "player", HTTPQueryParams.create().add("uuid", player), defaultCacheTime);
    }

    /**
     * Retrieves guild data by player UUID.
     *
     * @param player the UUID of the player
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildByPlayer(UUID player) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("player", player), defaultCacheTime);
    }

    /**
     * Retrieves guild data by player UUID with a specified max cache time.
     *
     * @param player       the UUID of the player
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildByPlayer(UUID player, Long maxCacheTime) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("player", player), maxCacheTime);
    }

    /**
     * Retrieves guild data by player UUID in string format.
     *
     * @param player the UUID of the player in string format
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildByPlayer(String player) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("player", player), defaultCacheTime);
    }

    /**
     * Retrieves guild data by player UUID in string format with a specified max cache time.
     *
     * @param player       the UUID of the player in string format
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildByPlayer(String player, Long maxCacheTime) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("player", player), maxCacheTime);
    }

    /**
     * Retrieves guild data by name.
     *
     * @param name the name of the guild
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildByName(String name) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("name", name), defaultCacheTime);
    }

    /**
     * Retrieves guild data by name with a specified max cache time.
     *
     * @param name         the name of the guild
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildByName(String name, Long maxCacheTime) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("name", name), maxCacheTime);
    }

    /**
     * Retrieves guild data by ID.
     *
     * @param id the ID of the guild
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildById(String id) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("id", id), defaultCacheTime);
    }

    /**
     * Retrieves guild data by ID with a specified max cache time.
     *
     * @param id           the ID of the guild
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing GuildReply
     */
    public CompletableFuture<GuildReply> getGuildById(String id, Long maxCacheTime) {
        return get(true, GuildReply.class, "guild", HTTPQueryParams.create().add("id", id), maxCacheTime);
    }

    /**
     * Retrieves the counts data.
     *
     * @return a CompletableFuture containing CountsReply
     */
    public CompletableFuture<CountsReply> getCounts() {
        return get(true, CountsReply.class, "counts", null, defaultCacheTime);
    }

    /**
     * Retrieves the counts data with a specified max cache time.
     *
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing CountsReply
     */
    public CompletableFuture<CountsReply> getCounts(Long maxCacheTime) {
        return get(true, CountsReply.class, "counts", null, maxCacheTime);
    }

    /**
     * Retrieves the status of a player by UUID.
     *
     * @param uuid the UUID of the player
     * @return a CompletableFuture containing StatusReply
     */
    public CompletableFuture<StatusReply> getStatus(UUID uuid) {
        return get(true, StatusReply.class, "status", HTTPQueryParams.create().add("uuid", uuid), defaultCacheTime);
    }

    /**
     * Retrieves the status of a player by UUID with a specified max cache time.
     *
     * @param uuid         the UUID of the player
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing StatusReply
     */
    public CompletableFuture<StatusReply> getStatus(UUID uuid, Long maxCacheTime) {
        return get(true, StatusReply.class, "status", HTTPQueryParams.create().add("uuid", uuid), maxCacheTime);
    }

    /**
     * Retrieves recent games of a player by UUID.
     *
     * @param uuid the UUID of the player
     * @return a CompletableFuture containing RecentGamesReply
     */
    public CompletableFuture<RecentGamesReply> getRecentGames(UUID uuid) {
        return get(true, RecentGamesReply.class, "recentGames", HTTPQueryParams.create().add("uuid", uuid), defaultCacheTime);
    }

    /**
     * Retrieves recent games of a player by UUID with a specified max cache time.
     *
     * @param uuid         the UUID of the player
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing RecentGamesReply
     */
    public CompletableFuture<RecentGamesReply> getRecentGames(UUID uuid, Long maxCacheTime) {
        return get(true, RecentGamesReply.class, "recentGames", HTTPQueryParams.create().add("uuid", uuid), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock garden data by profile.
     *
     * @param profile the profile ID
     * @return a CompletableFuture containing SkyBlockGardenReply
     */
    public CompletableFuture<SkyBlockGardenReply> getSkyBlockGarden(String profile) {
        return get(true, SkyBlockGardenReply.class, "skyblock/garden", HTTPQueryParams.create().add("profile", profile), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock garden data by profile with a specified max cache time.
     *
     * @param profile      the profile ID
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing SkyBlockGardenReply
     */
    public CompletableFuture<SkyBlockGardenReply> getSkyBlockGarden(String profile, Long maxCacheTime) {
        return get(true, SkyBlockGardenReply.class, "skyblock/garden", HTTPQueryParams.create().add("profile", profile), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock museum data by profile.
     *
     * @param profile the profile ID
     * @return a CompletableFuture containing SkyBlockMuseumReply
     */
    public CompletableFuture<SkyBlockMuseumReply> getSkyblockMuseum(String profile) {
        return get(true, SkyBlockMuseumReply.class, "skyblock/museum", HTTPQueryParams.create().add("profile", profile), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock museum data by profile with a specified max cache time.
     *
     * @param profile      the profile ID
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing SkyBlockMuseumReply
     */
    public CompletableFuture<SkyBlockMuseumReply> getSkyblockMuseum(String profile, Long maxCacheTime) {
        return get(true, SkyBlockMuseumReply.class, "skyblock/museum", HTTPQueryParams.create().add("profile", profile), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock profile data by profile.
     *
     * @param profile the profile ID
     * @return a CompletableFuture containing SkyBlockProfileReply
     */
    public CompletableFuture<SkyBlockProfileReply> getSkyBlockProfile(String profile) {
        return get(true, SkyBlockProfileReply.class, "skyblock/profile", HTTPQueryParams.create().add("profile", profile), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock profile data by profile with a specified max cache time.
     *
     * @param profile      the profile ID
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing SkyBlockProfileReply
     */
    public CompletableFuture<SkyBlockProfileReply> getSkyBlockProfile(String profile, Long maxCacheTime) {
        return get(true, SkyBlockProfileReply.class, "skyblock/profile", HTTPQueryParams.create().add("profile", profile), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock profiles data by player UUID.
     *
     * @param player the UUID of the player
     * @return a CompletableFuture containing SkyBlockProfilesReply
     */
    public CompletableFuture<SkyBlockProfilesReply> getSkyBlockProfiles(UUID player) {
        return get(true, SkyBlockProfilesReply.class, "skyblock/profiles", HTTPQueryParams.create().add("uuid", player), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock profiles data by player UUID with a specified max cache time.
     *
     * @param player       the UUID of the player
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing SkyBlockProfilesReply
     */
    public CompletableFuture<SkyBlockProfilesReply> getSkyBlockProfiles(UUID player, Long maxCacheTime) {
        return get(true, SkyBlockProfilesReply.class, "skyblock/profiles", HTTPQueryParams.create().add("uuid", player), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock profiles data by player UUID in string format.
     *
     * @param player the UUID of the player in string format
     * @return a CompletableFuture containing SkyBlockProfilesReply
     */
    public CompletableFuture<SkyBlockProfilesReply> getSkyBlockProfiles(String player) {
        return get(true, SkyBlockProfilesReply.class, "skyblock/profiles", HTTPQueryParams.create().add("uuid", player), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock profiles data by player UUID in string format with a specified max cache time.
     *
     * @param player       the UUID of the player in string format
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing SkyBlockProfilesReply
     */
    public CompletableFuture<SkyBlockProfilesReply> getSkyBlockProfiles(String player, Long maxCacheTime) {
        return get(true, SkyBlockProfilesReply.class, "skyblock/profiles", HTTPQueryParams.create().add("uuid", player), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock bingo data by player UUID.
     *
     * @param player the UUID of the player
     * @return a CompletableFuture containing SkyBlockBingoDataReply
     */
    public CompletableFuture<SkyBlockBingoDataReply> getSkyblockBingoData(UUID player) {
        return get(true, SkyBlockBingoDataReply.class, "skyblock/bingo", HTTPQueryParams.create().add("uuid", player), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock bingo data by player UUID with a specified max cache time.
     *
     * @param player       the UUID of the player
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing SkyBlockBingoDataReply
     */
    public CompletableFuture<SkyBlockBingoDataReply> getSkyblockBingoData(UUID player, Long maxCacheTime) {
        return get(true, SkyBlockBingoDataReply.class, "skyblock/bingo", HTTPQueryParams.create().add("uuid", player), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock bingo data by player UUID in string format.
     *
     * @param player the UUID of the player in string format
     * @return a CompletableFuture containing SkyBlockBingoDataReply
     */
    public CompletableFuture<SkyBlockBingoDataReply> getSkyblockBingoData(String player) {
        return get(true, SkyBlockBingoDataReply.class, "skyblock/bingo", HTTPQueryParams.create().add("uuid", player), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock bingo data by player UUID in string format with a specified max cache time.
     *
     * @param player       the UUID of the player in string format
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing SkyBlockBingoDataReply
     */
    public CompletableFuture<SkyBlockBingoDataReply> getSkyblockBingoData(String player, Long maxCacheTime) {
        return get(true, SkyBlockBingoDataReply.class, "skyblock/bingo", HTTPQueryParams.create().add("uuid", player), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock news with default cache time.
     *
     * @return a CompletableFuture containing the SkyBlockNewsReply
     */
    public CompletableFuture<SkyBlockNewsReply> getSkyBlockNews() {
        return get(true, SkyBlockNewsReply.class, "skyblock/news", null, defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock news with specified max cache time.
     *
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing the SkyBlockNewsReply
     */
    public CompletableFuture<SkyBlockNewsReply> getSkyBlockNews(Long maxCacheTime) {
        return get(true, SkyBlockNewsReply.class, "skyblock/news", null, maxCacheTime);
    }

    /**
     * Retrieves SkyBlock auctions for a specific page with default cache time.
     *
     * @param page the page number to retrieve
     * @return a CompletableFuture containing the SkyBlockAuctionsReply
     */
    public CompletableFuture<SkyBlockAuctionsReply> getSkyBlockAuctions(int page) {
        return get(false, SkyBlockAuctionsReply.class, "skyblock/auctions", HTTPQueryParams.create().add("page", page), defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock auctions for a specific page with specified max cache time.
     *
     * @param page         the page number to retrieve
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing the SkyBlockAuctionsReply
     */
    public CompletableFuture<SkyBlockAuctionsReply> getSkyBlockAuctions(int page, Long maxCacheTime) {
        return get(false, SkyBlockAuctionsReply.class, "skyblock/auctions", HTTPQueryParams.create().add("page", page), maxCacheTime);
    }

    /**
     * Retrieves SkyBlock bazaar data with default cache time.
     *
     * @return a CompletableFuture containing the SkyBlockBazaarReply
     */
    public CompletableFuture<SkyBlockBazaarReply> getSkyBlockBazaar() {
        return get(false, SkyBlockBazaarReply.class, "skyblock/bazaar", null, defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock bazaar data with specified max cache time.
     *
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing the SkyBlockBazaarReply
     */
    public CompletableFuture<SkyBlockBazaarReply> getSkyBlockBazaar(Long maxCacheTime) {
        return get(false, SkyBlockBazaarReply.class, "skyblock/bazaar", null, maxCacheTime);
    }

    /**
     * Retrieves SkyBlock fire sales data with default cache time.
     *
     * @return a CompletableFuture containing the SkyBlockFireSalesReply
     */
    public CompletableFuture<SkyBlockFireSalesReply> getSkyBlockFireSales() {
        return get(false, SkyBlockFireSalesReply.class, "skyblock/firesales", null, defaultCacheTime);
    }

    /**
     * Retrieves SkyBlock fire sales data with specified max cache time.
     *
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing the SkyBlockFireSalesReply
     */
    public CompletableFuture<SkyBlockFireSalesReply> getSkyBlockFireSales(Long maxCacheTime) {
        return get(false, SkyBlockFireSalesReply.class, "skyblock/firesales", null, maxCacheTime);
    }

    /**
     * Retrieves a resource by its type with a default cache time of 10 seconds.
     *
     * @param resource the resource type to retrieve
     * @return a CompletableFuture containing the ResourceReply
     */
    public CompletableFuture<ResourceReply> getResource(ResourceType resource) {
        return getResource(resource.getPath());
    }

    /**
     * Retrieves a resource by its path with a default cache time of 10 seconds.
     *
     * @param resource the resource path to retrieve
     * @return a CompletableFuture containing the ResourceReply
     */
    public CompletableFuture<ResourceReply> getResource(String resource) {
        return requestResource(resource, 10L);
    }

    /**
     * Retrieves the pet repository.
     *
     * @return a CompletableFuture containing the IPetRepository
     */
    public CompletableFuture<IPetRepository> getPetRepository() {
        return getResource(ResourceType.VANITY_PETS)
                .thenApply(PetRepositoryImpl::new);
    }

    /**
     * Requests a resource with a specified max cache time.
     *
     * @param resource     the resource path to retrieve
     * @param maxCacheTime the maximum cache time in seconds
     * @return a CompletableFuture containing the ResourceReply
     */
    protected CompletableFuture<ResourceReply> requestResource(String resource, Long maxCacheTime) {
        return get(false, ResourceReply.class, "resources/" + resource, null, maxCacheTime);
    }

    /**
     * Checks the HTTP response for errors and returns the response if successful.
     *
     * @param response the HTTP response to check
     * @return the HypixelHttpResponse if the status code is 200
     * @throws BadStatusCodeException if the status code is not 200
     */
    protected HypixelHttpResponse checkResponse(HypixelHttpResponse response) {
        if (response.getStatusCode() == 200) {
            return response;
        }

        String cause;
        try {
            cause = Utilities.GSON.fromJson(response.getBody(), JsonObject.class).get("cause").getAsString();
        } catch (JsonSyntaxException ignored) {
            cause = "Unknown (body is not json)";
        }

        throw new BadStatusCodeException(response.getStatusCode(), cause);
    }

    /**
     * Checks the reply for errors and returns the reply if successful.
     *
     * @param reply the reply to check
     * @param <T>   the type of the reply
     * @return the reply if successful
     * @throws BadResponseException if the reply is not successful
     */
    protected <T extends AbstractReply> T checkReply(T reply) {
        if (reply != null) {
            if (!reply.isSuccess()) {
                throw new BadResponseException(reply.getCause());
            }
        }
        return reply;
    }

    /**
     * Makes an HTTP request and retrieves the response, optionally using authentication and caching.
     *
     * @param authenticated whether to use authentication for the request
     * @param clazz         the class of the reply
     * @param request       the request path
     * @param params        the query parameters
     * @param maxCacheTime  the maximum cache time in seconds
     * @param <R>           the type of the reply
     * @return a CompletableFuture containing the reply
     */
    protected <R extends AbstractReply> CompletableFuture<R> get(boolean authenticated, Class<R> clazz, String request, HTTPQueryParams params, Long maxCacheTime) {
        String cacheKey = request + (params != null ? params.toString() : "");
        if (maxCacheTime != null) {
            R cachedReply = (R) cache.get(cacheKey, maxCacheTime);
            if (cachedReply != null) {
                return CompletableFuture.completedFuture(cachedReply);
            }
        }

        String url = BASE_URL + request;
        if (params != null) {
            url = params.getAsQueryString(url);
        }

        CompletableFuture<HypixelHttpResponse> future = authenticated ? httpClient.makeAuthenticatedRequest(url) : httpClient.makeRequest(url);
        return future
                .thenApply(this::checkResponse)
                .thenApply(response -> {
                    R reply;
                    if (clazz == ResourceReply.class) {
                        reply = checkReply((R) new ResourceReply(Utilities.GSON.fromJson(response.getBody(), JsonObject.class)));
                    }
                    else {
                        R tempReply = Utilities.GSON.fromJson(response.getBody(), clazz);
                        if (tempReply instanceof RateLimitedReply) {
                            ((RateLimitedReply) tempReply).setRateLimit(response.getRateLimit());
                        }
                        reply = checkReply(tempReply);
                    }
                    cache.put(cacheKey, reply);
                    return reply;
                });
    }
}