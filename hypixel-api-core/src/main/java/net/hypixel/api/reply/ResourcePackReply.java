package net.hypixel.api.reply;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ResourcePackReply extends AbstractReply {
    List<ResourcePack> packs;

    /**
     * Convenience function.
     * @return Skyblock Resource Pack.
     * @throws IllegalAccessException if no resourcepack with the configured id is found.
     */
    ResourcePack getSkyblockPack() throws IllegalAccessException {
        return packs.stream().filter(a -> a.gameId.equals("SkyBlock")).findFirst().orElseThrow(()->new IllegalAccessException("SkyBlock pack not found"));
    }


    static class ResourcePack {
        /**
         * For example "SkyBlock"
         */
        String gameId;
        Long lastUpdated;
        UUID deployUUID;
        List<VersionResourcePack> versions;
        public VersionResourcePack latest(){
            return versions.stream().max(Comparator.comparingInt(a -> a.packFormat)).orElse(null);
        }
    }

    static class VersionResourcePack {
        /**
         * These are the mc packformats used.
         */
        int packFormat;
        String url;
        String hash;

        /**
         * Downloads the file from the download url to the specified file. Note that this is a Zip Archive!
         */
        public void downloadToFile(File file) throws IOException {
            URL url = new URL(this.url);
            try (InputStream in = url.openStream()) {
                Files.copy(in, Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
