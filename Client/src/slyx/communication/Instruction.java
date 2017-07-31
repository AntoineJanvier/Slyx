package slyx.communication;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class Instruction<K, V> {
    private K key;
    private V value;

    public Instruction(K key, V value) {
        this.setKey(key);
        this.setValue(value);
    }

    public K getKey() { return key; }
    public void setKey(K key) { this.key = key; }
    public V getValue() { return value; }
    public void setValue(V value) { this.value = value; }
}
