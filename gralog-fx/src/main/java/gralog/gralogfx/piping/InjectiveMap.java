package gralog.gralogfx.piping;
import java.util.HashMap;
import java.util.Set;
class InjectiveMap<Integer, Value> {
	private HashMap<Integer, Value> integerToValueMap;
    private HashMap<Value, Integer> valueToIntegerMap;

	public InjectiveMap() {
		this.integerToValueMap = new HashMap<Integer, Value>();
	    this.valueToIntegerMap = new HashMap<Value, Integer>();
	}

    public void set(Integer k, Value v) {
        integerToValueMap.put(k, v);
        valueToIntegerMap.put(v, k);
    }


    public Value get(Integer k) {
    	return this.integerToValueMap.get(k);
    }
    public Integer getId(Value v) {
    	return this.valueToIntegerMap.get(v);
    }

    public Set<Integer> keySet() {
    	return this.integerToValueMap.keySet();
    }

    public Set<Value> valueSet() {
    	return this.valueToIntegerMap.keySet();
    }

    
}
