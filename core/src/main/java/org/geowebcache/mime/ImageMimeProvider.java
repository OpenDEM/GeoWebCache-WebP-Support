package org.geowebcache.mime;

/** Convenience extension point for providers that return ImageMime instances. */
public interface ImageMimeProvider extends MimeTypeProvider {

    @Override
    ImageMime createFromFormat(String format) throws MimeException;

    @Override
    ImageMime createFromExtension(String extension) throws MimeException;
}
