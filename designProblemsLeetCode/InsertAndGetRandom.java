class RandomizedSet {
  List<Integer> list;
    
    // map of element value to its index in the list
    Map<Integer, Integer> map;
    
    Random rand;

    public RandomizedSet() {
      list = new ArrayList<>();
        map = new HashMap<>();
        rand = new Random();
    }
    
    public boolean insert(int val) {
     if(!map.containsKey(val)){
         list.add(val);
         map.put(val, list.size()-1);
         return true;
     } else{
        return false;
     }
    }
    
    public boolean remove(int val) {
       if(map.containsKey(val)){
            int index = map.get(val);
          
           int ele = list.get(list.size()-1);
           
         list.set(index, ele);
           map.put(ele, index);
           
           list.remove(list.size() - 1);
            map.remove(val);
           return true;
       } 
        return false;
    }
    
    public int getRandom() {
        int index = rand.nextInt(list.size());
        return list.get(index);
    }
}

/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */