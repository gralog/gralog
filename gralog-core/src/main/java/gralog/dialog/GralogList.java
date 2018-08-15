package gralog.dialog;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class GralogList<T> {

    public SimpleStringProperty name;
    public SimpleStringProperty stringData;

    private List<T> list = new ArrayList<>();

    public GralogList(String name){
        this.name = new SimpleStringProperty(name);
        this.stringData = new SimpleStringProperty("");
    }

    public void updateStringData(){
        StringBuffer sb = new StringBuffer();
        for(T elem : list){
            sb.append(elem.toString() + ", ");
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
