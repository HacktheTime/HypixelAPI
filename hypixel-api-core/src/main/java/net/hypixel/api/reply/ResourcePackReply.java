package net.hypixel.api.reply;

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
   public List<ResourcePack> packs;

    /**
     * Convenience function.
     * @return Skyblock Resource Pack.
     * @throws IllegalAccessException if no resourcepack with the configured id is found.
     */
    public ResourcePack getSkyblockPack() throws IllegalAccessException {
        return packs.stream().filter(a -> a.gameId.equals("SkyBlock")).findFirst().orElseThrow(()->new IllegalAccessException("SkyBlock pack not found"));
    }


    public static class ResourcePack {
        /**
         * For example "SkyBlock"
         */
        public String gameId;
        public Long lastUpdated;
        public UUID deployUUID;
        public List<VersionResourcePack> versions;
        public VersionResourcePack latest(){
            return versions.stream().max(Comparator.comparingInt(a -> a.packFormat)).orElse(null);
        }
    }

    public static class VersionResourcePack {
        /**
         * These are the mc packformats used.
         */
       public int packFormat;
       public String url;
       public String hash;

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
