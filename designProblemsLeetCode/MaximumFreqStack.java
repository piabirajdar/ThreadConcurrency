class FreqStack {
    Map<Integer, Integer> map;
    Map<Integer, Stack<Integer>> group;
    int maxFreq;

    public FreqStack() {
        map = new HashMap<>();
          group = new HashMap<>();
        maxFreq = 0;
    }
    
    public void push(int val) {
        int f = map.getOrDefault(val, 0) + 1;
        map.put(val, f);
        
        if(f > maxFreq)
            maxFreq = f;
        group.computeIfAbsent(f, v->new Stack()).push(val);
    }
    
    public int pop() {
        int val = group.get(maxFreq).pop();
        if(group.get(maxFreq).size() == 0) {
            maxFreq--;
        }
        map.put(val, map.getOrDefault(val, 0)-1);
        return val;
    }
}

/**
 * Your FreqStack object will be instantiated and called as such:
 * FreqStack obj = new FreqStack();
 * obj.push(val);
 * int param_2 = obj.pop();
 */