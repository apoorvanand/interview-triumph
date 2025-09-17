public class SnsClient {
    private final HttpClient http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
    String getIncident(String baseURL, String incidentId, String token) throws Exception {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(baseURL +"/api/now/table/incident/" + incidentId)).header("Authorization", "Bearer " + token)
        .header("Accept","application/json")
        .timeout(Duration.ofSeconds(10))
        .GET().build();
    int attempts = 0;
    while(true) {
        attempts++;
        HttpResponse<String> res = http.send(req,HttpResponse.BodyHandlers.ofString());
        int sc = res.statusCode();
        if(sc==200) return res.body();
        if(attempts>=3 || (sc>=400 && sc<500 && sc!=429)) throw new RuntimeException("HTTP "+sc);
        Thread.sleep(backoffMillis(attempts));
    }
    }
    private long backoffMillis(int attempt){
        long base = 200L * (1L << (attempt -1));
        long jitter = (long)(Math.random()*100L)
        return Math.min(2000L, base +jitter);
        }

}