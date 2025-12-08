public class CallTranscriptAnalyzerStream {
    public static Map<String, Object> analyze(String jsonInput) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonInput);
        Json callNodes = rootNode.get("calls");

        List<JsonNode> calls = StreamSupport.stream(callNodes.splititerator(), false).collect(Collectors.toList());

        // Total calls:
        int totalCalls = calls.size();


        int completedCalls = calls.stream().
           filter(c -> c.get('status').asText().equalsIgnoreCase("completed"))
           .collect(Collectors.toList()).size();
        
        int failedCalls = calls.stream().
        filter(c -> c.get('status').asText().equalsIgnoreCase("failed"))
        .collect(Collectors.toList()).size();

        double avgDuration = calls.stream().mapToInt(c-> c.get('duration').asInt()).average().orElse(0.0);

        // Calls per doctor
        Map<String, Long> callsPerDoctor = calls.stream().
        collect(Collectors.groupingBy(c -> c.get('doctor'), Collectors.counting()));

        // avg per doctor
        Map<String, Double> avgPerDoctor = calls.stream()
            .collect(Collectors.groupingBy(
                c -> c.get("doctor").asText(),
                Collectors.averagingInt(c -> c.get("duration").asInt())
            ));

        Map<String, Object> insights = new HashMap<>();
        insights.put("total_calls", totalCalls);
        insights.put("completed_calls", completedCalls);
        insights.put("failed_calls", failedCalls);
        insights.put("average_duration", avgDuration);
        insights.put("calls_per_doctor", callsPerDoctor);
        return insights;
    }
 
    public static void main(String[] args) throws IOException {
        String inputJson = """
            {
            "calls": [
                {"call_id": 1, "doctor": "Dr. Smith", "patient": "John Doe", "duration": 300, "status": "completed"},
                {"call_id": 2, "doctor": "Dr. Smith", "patient": "Jane Roe", "duration": 200, "status": "completed"},
                {"call_id": 3, "doctor": "Dr. Adams", "patient": "Bob Lee", "duration": 400, "status": "failed"}
            ]
            }
            """;

        Map<String, Object> insights = analyze(inputJson);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insights));
     }
}



Map<String, Integer> callsPerDoctor = calls.stream()
                                .collect(Collectors.groupingBy((c) -> c.get("Doctor").asText() , Counting.asInt))