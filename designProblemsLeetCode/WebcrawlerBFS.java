/**
 * // This is the HtmlParser's API interface.
 * // You should not implement it, or speculate about its implementation
 * interface HtmlParser {
 *     public List<String> getUrls(String url) {}
 * }
 */

class Solution {
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        Queue<String> q = new LinkedList<>();
        q.offer(startUrl);
        HashSet<String> visited = new HashSet<>();
        String hostName = getHostName(startUrl);
        while(!q.isEmpty()) {
            String url = q.poll();
            for( String nextUrl : htmlParser.getUrls(url)) {
                if(getHostName(nextUrl).equals(hostName) && !visited.contains(nextUrl)) {
                    q.offer(nextUrl);
                }

            }
            visited.add(url);
        }
        return new ArrayList<>(visited);
    }

    public String getHostName(String url) {
        return url.split("/")[2];
    }
}