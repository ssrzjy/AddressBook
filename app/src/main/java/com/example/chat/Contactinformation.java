package com.example.chat;

public class Contactinformation implements Comparable<Contactinformation> {
    public String name;
    public String number;
    public String sort;
    public String email_adress;
    public String notes;
    public boolean select;
    public boolean IsSelect(){return select;}
    public void SetSelect(Boolean select){this.select=select;}

    @Override
    public int compareTo(Contactinformation o) {
        int result = o.name.compareTo(this.name); // 姓名降序
        if (result == 0) { // 票数相等，按组别
            return this.sort.compareTo(o.sort); // 组别按升序
        } else {
            return result;
        }
    }
}
