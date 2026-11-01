# GeoWebCache-WebP-Support

**WebP Support for in Geoserver integrated GeoWebCache**

Example: 
[https://opendem.info/geoserver/gwc/demo/opendem:webp?gridSet=EPSG:900913&format=image/webp](https://opendem.info/geoserver/gwc/demo/opendem:webp?gridSet=EPSG:900913&format=image/webp)

```The code was generated using AI and should be used with caution.```

**The following changes were made to the code**

- `geowebcache/core/src/main/java/org/geowebcache/mime/MimeTypeProvider.java` — generic provider interface for MIME lookup by format and file extension.
- `geowebcache/core/src/main/java/org/geowebcache/mime/ImageMimeProvider.java` — specialized provider interface that provides `ImageMime`.
- `geowebcache/core/src/main/java/org/geowebcache/mime/MimeTypeProviderRegistry.java` — registry with ServiceLoader detection, explicit registration, and duplicate-entry checks.
- `geowebcache/core/src/main/java/org/geowebcache/mime/ImageMimeProviderRegistry.java` — image registry facade for the provider registry.
- `geowebcache/core/src/main/java/org/geowebcache/mime/ImageMime.java` — constructor made visible for external subclasses; MIME and extension lookups also query providers.

**Test with Geoserver 3.0.x**

Copy the JARs in WEB-INF/lib and restart your servlet container.
Open your Geoserver Website and go to the GWCSettingsPage. 
Activate WebP as Output format.
Create a new Layer and go to the CachedLayersPage. 
WebP should then be available as a format option

 Adjustments were also made to the Geoserver Community module WebP: [https://docs.geoserver.org/3.0.x/en/user/community/webp/](https://docs.geoserver.org/3.0.x/en/user/community/webp/)
