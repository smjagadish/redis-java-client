package Pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {
    private String name;
    private int id;
    private String[] category;
    public Employee(String name , int id, String[] category)
    {
        this.name = name;
        this.id = id;
        this.category = category;
    }
}
