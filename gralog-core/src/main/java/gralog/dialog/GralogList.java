package gralog.dialog;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class GralogList<T> {


    public SimpleStringProperty name;
    public SimpleStringProperty stringData;

    public List<T> list = new ArrayList<>();

    private Function<T, String> toString;

    public GralogList(String name){
        this.name = new SimpleStringProperty(name);
        this.stringData = new SimpleStringProperty("");
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
