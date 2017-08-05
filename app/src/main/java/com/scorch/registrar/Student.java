package com.scorch.registrar;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adria on 4/21/2017.
 */
public class Student implements Parcelable {
    String stuID;
    String name;
    String address;
    String phone;
    String email;
    String imgUrl;
    List<String> subjects;
    String paymentStatus;
    double amtPaid;

    public Student(String stuID,String name,String address,String phone,String email,List<String>subjects,String imgUrl){
        this.stuID=stuID;
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.email=email;
        this.subjects=subjects;
        this.imgUrl=imgUrl;

    }
    public Student(){

    }

    public List<String> getSubjects() {
        return subjects;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getStuID() {
        return stuID;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isMatch=false;
        if(obj!=null&&obj instanceof Student){
            Student a=(Student) obj;
            isMatch=a.stuID.equalsIgnoreCase(this.stuID);
        }
        return isMatch;
    }

    @Override
    public int hashCode() {

        int result = 17;

        //hash code for checking rollno
        //result = 31 * result + (this.s_rollNo == 0 ? 0 : this.s_rollNo);

        //hash code for checking fname
        result = 31 * result + (this.stuID == null ? 0 : this.stuID.hashCode());

        return result;    }

    protected Student(Parcel in) {
        stuID = in.readString();
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        email = in.readString();
        imgUrl=in.readString();
        if (in.readByte() == 0x01) {
            subjects = new ArrayList<String>();
            in.readList(subjects, String.class.getClassLoader());
        } else {
            subjects = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stuID);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(imgUrl);
        if (subjects == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(subjects);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}