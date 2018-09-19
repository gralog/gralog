package gralog.dialog;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.function.Function;

public class GralogList<T> {
    //default toString methods to choose from

    public SimpleStringProperty name;
    public SimpleStringProperty stringData;

    public ArrayList<T> list = new ArrayList<>();

    private Function<T, String> toString;


    public GralogList(String name){
        this.name = new SimpleStringProperty(name);
        this.stringData = new SimpleStringProperty("");
    }

    public GralogList(String name, Function<T, String> toString){
        this(name);
        overrideToString(toString);
    }

    public void overrideToString(Function<T, String> toString){
        this.toString = toString;
    }

    public void updateStringData(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            T elem = list.get(i);
            if(toString == null){
                sb.append(elem.toString());
            }
            else{
                sb.append(toString.apply(elem));
            }

            if(i < list.size() - 1){
                sb.append(", ");
            }
        }
        stringData.set(sb.toString());
    }
    @Override
    public String toString(){
        updateStringData();
        return stringData.getValue();
    }

    public void add(T element){
        list.add(element);
        updateStringData();
    }

    public void remove(int index){
        list.remove(index);
    }

    public void remove(T element){
        list.remove(element);
        updateStringData();
    }

}
