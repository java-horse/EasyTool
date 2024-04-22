package easy.restful.search;

public class RestServiceItemLog {

    private final String url;
    private final long time;

    public RestServiceItemLog(String url, long time) {
        this.url = url;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public long getTime() {
        return time;
    }

}
