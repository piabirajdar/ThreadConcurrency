public static List<Map<String, Object>> fetchRepos(String user) throws Exception {
    String urlStr = "https://api.github.com/users/" + user + "/repos";
    URL url = new URL(urlStr);

    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");

    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;

    while ((line = br.readLine()) != null) sb.append(line);
    br.close();

    JSONArray arr = new JSONArray(sb.toString());
    List<Map<String, Object>> list = new ArrayList<>();

    for (int i = 0; i < arr.length(); i++) {
        list.add(arr.getJSONObject(i).toMap());
    }

    return list;   // LIST OF OBJECTS
}


public static List<Map<String, Object>> filterPopular(List<Map<String, Object>> repos) {
    List<Map<String, Object>> result = new ArrayList<>();

    for (Map<String, Object> repo : repos) {
        int stars = ((Number) repo.get("stargazers_count")).intValue();
        if (stars > 1000) {
            result.add(repo);
        }
    }

    return result;
}