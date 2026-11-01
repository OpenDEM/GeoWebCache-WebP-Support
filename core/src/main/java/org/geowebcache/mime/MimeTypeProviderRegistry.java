package org.geowebcache.mime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

/** Maintains optional MIME providers and makes them available to the static GWC MIME parsers. */
public final class MimeTypeProviderRegistry {

    private static final List<MimeTypeProvider> PROVIDERS = new CopyOnWriteArrayList<>();
    private static volatile boolean initialized;

    private MimeTypeProviderRegistry() {}

    public static void clear() {
        synchronized (MimeTypeProviderRegistry.class) {
            PROVIDERS.clear();
            initialized = false;
        }
    }

    public static void register(MimeTypeProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Mime type provider cannot be null");
        }
        synchronized (MimeTypeProviderRegistry.class) {
            validateRegistration(provider);
            PROVIDERS.add(provider);
        }
    }

    public static List<MimeTypeProvider> getProviders() {
        ensureInitialized();
        return Collections.unmodifiableList(new ArrayList<>(PROVIDERS));
    }

    public static MimeType getMimeType(String format) {
        return getMimeTypeForFormat(format);
    }

    public static MimeType getMimeTypeForFormat(String formatStr) {
        ensureInitialized();
        if (formatStr == null) {
            return null;
        }
        for (MimeTypeProvider provider : PROVIDERS) {
            try {
                MimeType mimeType = provider.createFromFormat(formatStr);
                if (mimeType != null) {
                    return mimeType;
                }
            } catch (MimeException ignore) {
                // A provider may reject formats it doesn't understand.
            }
        }
        return null;
    }

    public static MimeType getMimeTypeForExtension(String fileExtension) {
        ensureInitialized();
        if (fileExtension == null) {
            return null;
        }
        for (MimeTypeProvider provider : PROVIDERS) {
            try {
                MimeType mimeType = provider.createFromExtension(fileExtension);
                if (mimeType != null) {
                    return mimeType;
                }
            } catch (MimeException ignore) {
                // A provider may reject extensions it doesn't understand.
            }
        }
        return null;
    }

    public static MimeType findByFormat(String formatStr) {
        return getMimeTypeForFormat(formatStr);
    }

    public static MimeType findByExtension(String fileExtension) {
        return getMimeTypeForExtension(fileExtension);
    }

    private static void ensureInitialized() {
        if (initialized) {
            return;
        }
        synchronized (MimeTypeProviderRegistry.class) {
            if (initialized) {
                return;
            }
            ServiceLoader<MimeTypeProvider> loader = ServiceLoader.load(MimeTypeProvider.class);
            for (MimeTypeProvider provider : loader) {
                register(provider);
            }
            initialized = true;
        }
    }

    private static void validateRegistration(MimeTypeProvider provider) {
        for (MimeTypeProvider existing : PROVIDERS) {
            if (existing.getClass().equals(provider.getClass())) {
                throw new IllegalArgumentException("Duplicate MIME type provider registration for class "
                        + provider.getClass().getName());
            }
        }

        for (String candidate : getKnownFormatCandidates()) {
            try {
                MimeType candidateMime = provider.createFromFormat(candidate);
                if (candidateMime != null) {
                    MimeType builtin = lookupBuiltinFormat(candidate);
                    if (builtin != null && builtin.getMimeType().equalsIgnoreCase(candidateMime.getMimeType())) {
                        throw new IllegalArgumentException("Duplicate MIME type registration for format " + candidate
                                + " by " + provider.getClass());
                    }
                }
            } catch (MimeException ignored) {
                // Providers are allowed to reject unknown candidates during validation.
            }
        }

        for (String candidate : getKnownExtensionCandidates()) {
            try {
                MimeType candidateMime = provider.createFromExtension(candidate);
                if (candidateMime != null) {
                    MimeType builtin = lookupBuiltinExtension(candidate);
                    if (builtin != null && builtin.getMimeType().equalsIgnoreCase(candidateMime.getMimeType())) {
                        throw new IllegalArgumentException("Duplicate MIME type registration for extension " + candidate
                                + " by " + provider.getClass());
                    }
                }
            } catch (MimeException ignored) {
                // Providers are allowed to reject unknown candidates during validation.
            }
        }
    }

    private static MimeType lookupBuiltinFormat(String format) {
        if (format == null || !format.startsWith("image/")) {
            return null;
        }

        String candidate = format;
        if (!"image/png; mode=24bit".equalsIgnoreCase(candidate) && candidate.contains(";")) {
            candidate = candidate.split(";")[0];
        }
        String tmpStr = candidate.substring(6);

        if (tmpStr.equalsIgnoreCase("png")) {
            return ImageMime.png;
        } else if (tmpStr.equalsIgnoreCase("jpeg")) {
            return ImageMime.jpeg;
        } else if (tmpStr.equalsIgnoreCase("gif")) {
            return ImageMime.gif;
        } else if (tmpStr.equalsIgnoreCase("tiff")) {
            return ImageMime.tiff;
        } else if (tmpStr.equalsIgnoreCase("png8")) {
            return ImageMime.png8;
        } else if (tmpStr.equalsIgnoreCase("png24")) {
            return ImageMime.png24;
        } else if (tmpStr.equalsIgnoreCase("png; mode=24bit") || tmpStr.equalsIgnoreCase("png;%20mode=24bit")) {
            return ImageMime.png_24;
        } else if (tmpStr.equalsIgnoreCase("vnd.jpeg-png")) {
            return ImageMime.jpegPng;
        } else if (tmpStr.equalsIgnoreCase("vnd.jpeg-png8")) {
            return ImageMime.jpegPng8;
        }

        if (candidate.equalsIgnoreCase("text/plain")) {
            return TextMime.txt;
        } else if (candidate.toLowerCase().startsWith("text/")) {
            try {
                return TextMime.checkForFormat(candidate);
            } catch (MimeException ignored) {
                return null;
            }
        }

        try {
            return ApplicationMime.checkForFormat(candidate);
        } catch (MimeException ignored) {
            return null;
        }
    }

    private static MimeType lookupBuiltinExtension(String extension) {
        if (extension == null) {
            return null;
        }

        if (extension.equalsIgnoreCase("png")) {
            return ImageMime.png;
        } else if (extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg")) {
            return ImageMime.jpeg;
        } else if (extension.equalsIgnoreCase("gif")) {
            return ImageMime.gif;
        } else if (extension.equalsIgnoreCase("tiff")) {
            return ImageMime.tiff;
        } else if (extension.equalsIgnoreCase("png8")) {
            return ImageMime.png8;
        } else if (extension.equalsIgnoreCase("png24")) {
            return ImageMime.png24;
        } else if (extension.equalsIgnoreCase("png_24")) {
            return ImageMime.png_24;
        } else if (extension.equalsIgnoreCase("jpeg-png")) {
            return ImageMime.jpegPng;
        } else if (extension.equalsIgnoreCase("jpeg-png8")) {
            return ImageMime.jpegPng8;
        } else if (extension.equalsIgnoreCase("dds")) {
            return ImageMime.dds;
        } else if (extension.equalsIgnoreCase("json")) {
            return ApplicationMime.json;
        } else if (extension.equalsIgnoreCase("geojson")) {
            return ApplicationMime.geojson;
        } else if (extension.equalsIgnoreCase("topojson")) {
            return ApplicationMime.topojson;
        } else if (extension.equalsIgnoreCase("utfgrid")) {
            return ApplicationMime.utfgrid;
        } else if (extension.equalsIgnoreCase("pbf")) {
            return ApplicationMime.mapboxVector;
        } else if (extension.equalsIgnoreCase("gml")) {
            return XMLMime.gml;
        } else if (extension.equalsIgnoreCase("xml")) {
            return TextMime.txtXml;
        }

        try {
            return ApplicationMime.checkForExtension(extension);
        } catch (MimeException ignored) {
            return null;
        }
    }

    private static List<String> getKnownFormatCandidates() {
        List<String> candidates = new ArrayList<>();
        candidates.add("image/png");
        candidates.add("image/jpeg");
        candidates.add("image/gif");
        candidates.add("image/tiff");
        candidates.add("image/png8");
        candidates.add("image/png24");
        candidates.add("image/vnd.jpeg-png");
        candidates.add("image/vnd.jpeg-png8");
        candidates.add("image/dds");
        candidates.add("application/json");
        candidates.add("application/vnd.mapbox-vector-tile");
        candidates.add("application/bil16");
        candidates.add("application/bil32");
        candidates.add("application/xml");
        candidates.add("text/plain");
        return candidates;
    }

    private static List<String> getKnownExtensionCandidates() {
        List<String> candidates = new ArrayList<>();
        candidates.add("png");
        candidates.add("jpg");
        candidates.add("jpeg");
        candidates.add("gif");
        candidates.add("tiff");
        candidates.add("png8");
        candidates.add("png24");
        candidates.add("jpeg-png");
        candidates.add("jpeg-png8");
        candidates.add("dds");
        candidates.add("json");
        candidates.add("geojson");
        candidates.add("topojson");
        candidates.add("utfgrid");
        candidates.add("pbf");
        candidates.add("bil16");
        candidates.add("bil32");
        candidates.add("gml");
        candidates.add("xml");
        return candidates;
    }
}
