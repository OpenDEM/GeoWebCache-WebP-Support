package org.geowebcache.mime;

import java.util.List;

/** Backwards-compatible alias for the common image MIME provider registry. */
public final class ImageMimeProviderRegistry {

    private ImageMimeProviderRegistry() {}

    public static void clear() {
        MimeTypeProviderRegistry.clear();
    }

    public static void register(ImageMimeProvider provider) {
        MimeTypeProviderRegistry.register(provider);
    }

    public static List<MimeTypeProvider> getProviders() {
        return MimeTypeProviderRegistry.getProviders();
    }

    public static MimeType getMimeType(String format) {
        return MimeTypeProviderRegistry.getMimeType(format);
    }

    public static MimeType getMimeTypeForFormat(String format) {
        return MimeTypeProviderRegistry.getMimeTypeForFormat(format);
    }

    public static MimeType getMimeTypeForExtension(String extension) {
        return MimeTypeProviderRegistry.getMimeTypeForExtension(extension);
    }
}
