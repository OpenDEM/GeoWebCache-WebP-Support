package org.geowebcache.mime;

/**
 * Extension point for resolving additional MIME types at runtime. Providers are expected to discover formats before
 * layer configuration is parsed so that custom image types are available in the usual GWC MIME resolution paths.
 */
public interface MimeTypeProvider {

    /** Resolve an image or document MIME type from a format string such as {@code image/webp}. */
    MimeType createFromFormat(String format) throws MimeException;

    /** Resolve a MIME type from a conventional file extension such as {@code webp}. */
    MimeType createFromExtension(String extension) throws MimeException;

    default boolean acceptsFormat(String format) {
        return false;
    }

    default boolean acceptsExtension(String extension) {
        return false;
    }
}
