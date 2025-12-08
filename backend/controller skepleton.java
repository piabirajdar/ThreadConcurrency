@RestController
public class TranscribeController {

    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private Map<String, Map<String, Object>> store = new ConcurrentHashMap<>();

    @PostMapping("/transcribe")
    public Map<String, Object> transcribe(@RequestBody Map<String, String> req) {
        // TODO: create job, put in store, submit async
        return null;
    }

    @GetMapping("/status/{jobId}")
    public Map<String, Object> status(@PathVariable String jobId) {
        // TODO
        return null;
    }
}


