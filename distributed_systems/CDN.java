 // CDN Design and Implementation in Java (Simplified Model)

// =======================
// ENTITY CLASSES
// =======================

class Content {
    private String id;
    private String url;
    private String contentType;
    private byte[] data;

    // Getters, Setters, Constructors
}

class EdgeNode {
    private String id;
    private String location;
    private String ip;
    private Map<String, CachedContent> cachedContents = new HashMap<>();

    public void cacheContent(Content content) {
        cachedContents.put(content.getId(), new CachedContent(content));
    }

    public CachedContent getCachedContent(String contentId) {
        return cachedContents.get(contentId);
    }

    // Other health, capacity methods
}

class CachedContent {
    private Content content;
    private long cacheTime;

    public CachedContent(Content content) {
        this.content = content;
        this.cacheTime = System.currentTimeMillis();
    }

    // Getters
}

class ClientRequest {
    private String url;
    private String clientIp;

    // Getters, Setters, Constructors
}

// =======================
// SERVICE LAYER
// =======================

class GeoLocatorService {
    public static String getRegionFromIp(String ip) {
        // Stubbed geo lookup
        if (ip.startsWith("10.")) return "Asia";
        return "NA";
    }
}

class DnsResolverService {
    private Map<String, List<EdgeNode>> edgeMapByRegion;

    public DnsResolverService(Map<String, List<EdgeNode>> edgeMapByRegion) {
        this.edgeMapByRegion = edgeMapByRegion;
    }

    public EdgeNode resolveEdgeNode(String clientIp) {
        String region = GeoLocatorService.getRegionFromIp(clientIp);
        List<EdgeNode> nodes = edgeMapByRegion.get(region);
        return nodes != null && !nodes.isEmpty() ? nodes.get(0) : null;
    }
}

class CdnService {
    private DnsResolverService dnsResolver;

    public CdnService(DnsResolverService dnsResolver) {
        this.dnsResolver = dnsResolver;
    }

    public Content handleRequest(ClientRequest request) {
        EdgeNode edge = dnsResolver.resolveEdgeNode(request.getClientIp());
        if (edge == null) throw new RuntimeException("No edge node available");

        CachedContent cached = edge.getCachedContent(request.getUrl());
        if (cached != null) return cached.getContent();

        // Simulate origin fetch
        Content originContent = fetchFromOrigin(request.getUrl());
        edge.cacheContent(originContent);
        return originContent;
    }

    private Content fetchFromOrigin(String url) {
        return new Content(UUID.randomUUID().toString(), url, "text/html", url.getBytes());
    }
}

// =======================
// MAIN SIMULATION
// =======================

public class CDNMain {
    public static void main(String[] args) {
        // Setup edge nodes by region
        EdgeNode asiaEdge = new EdgeNode("edge-asia-1", "Asia", "123.1.1.1");
        EdgeNode naEdge = new EdgeNode("edge-na-1", "NorthAmerica", "123.9.9.9");

        Map<String, List<EdgeNode>> regionMap = new HashMap<>();
        regionMap.put("Asia", List.of(asiaEdge));
        regionMap.put("NA", List.of(naEdge));

        DnsResolverService dns = new DnsResolverService(regionMap);
        CdnService cdn = new CdnService(dns);

        // Simulate request
        ClientRequest request = new ClientRequest("http://cdn.com/index.html", "10.12.34.56");
        Content served = cdn.handleRequest(request);

        System.out.println("Served content from CDN: " + new String(served.getData()));
    }
}